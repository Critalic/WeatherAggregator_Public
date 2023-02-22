package com.example.weatheraggregator.api.batch.jpa;

import com.example.weatheraggregator.api.batch.jpa.writer.RepositoryCollectionWriter;
import com.example.weatheraggregator.api.batch.processor.CityProcessor;
import com.example.weatheraggregator.api.batch.processor.ResponseToForecastProcessor;
import com.example.weatheraggregator.api.batch.util.PropertyParser;
import com.example.weatheraggregator.api.persistence.entity.*;
import com.example.weatheraggregator.api.persistence.repository.*;
import com.example.weatheraggregator.aspect.LogOnException;
import com.example.weatheraggregator.collector.provider.WeatherDataProvider;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@PropertySource("classpath:properties/persistence.properties")
public class CollectorStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ResponseRepository responseRepository;
    private final CityRepository cityRepository;
    private final ProviderRepository providerRepository;
    private final ForecastRepository forecastRepository;
    private final ApplicationContext applicationContext;

    private final Map<String, ForecastType> forecastTypes;
    private final Map<String, Provider> providers;

    public CollectorStepConfig(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               ProviderRepository providerRepository,
                               ForecastRepository forecastRepository,
                               ForecastTypeRepository forecastTypeRepository,
                               ResponseRepository responseRepository,
                               CityRepository cityRepository,
                               ApplicationContext applicationContext) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.forecastRepository = forecastRepository;
        this.responseRepository = responseRepository;
        this.cityRepository = cityRepository;
        this.providerRepository = providerRepository;
        this.applicationContext = applicationContext;

        providers = providerRepository.findAll().stream()
                .collect(Collectors.toMap(Provider::getCredential, item -> item));
        forecastTypes = forecastTypeRepository.findAll().stream()
                .collect(Collectors.toMap(ForecastType::getType, item -> item));
    }

    @Bean(name = "cityRepositoryReader")
    @StepScope
    public RepositoryItemReader<City> cityRepositoryReader() {
        return new RepositoryItemReaderBuilder<City>()
                .name("cityRepositoryReader")
                .methodName("getByOrderByNameAsc")
                .arguments()
                .repository(cityRepository)
                .sorts(Collections.singletonMap("name", Sort.Direction.ASC))
                .build();
    }

    @Bean(name = "cityProcessor")
    @StepScope
    @LogOnException
    public CityProcessor cityProcessor(
            @Value("#{jobParameters['intervals']}") String intervals,
            @Value("#{jobParameters['timeStamp']}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDateTime timeStamp) {
        return new CityProcessor(timeStamp, PropertyParser.parseIntervals(intervals),
                getActiveProviderServices(), providers, forecastTypes);
    }

    @Bean(name = "responseWriter")
    @StepScope
    public RepositoryCollectionWriter<Response> responseWriter() {
        return new RepositoryCollectionWriter<>(responseRepository::saveAll);
    }

    @Bean(name = "repositoryResponseReader")
    @StepScope
    public RepositoryItemReader<Response> repositoryResponseReader(
            @Value("#{jobParameters['timeStamp']}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDateTime timeStamp) {
        return new RepositoryItemReaderBuilder<Response>()
                .name("responseRepositoryReader")
                .methodName("findByTimeStampOrderByIdAsc")
                .repository(responseRepository)
                .arguments(timeStamp)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean(name = "responseProcessor")
    @StepScope
    public ResponseToForecastProcessor responseProcessor(
            @Value("#{jobParameters['timeStamp']}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDateTime timeStamp) {
        return new ResponseToForecastProcessor(timeStamp, getActiveProviderServices());
    }

    @Bean(name = "updateForecast")
    @StepScope
    public RepositoryCollectionWriter<Forecast> updateForecast(
            @Value("#{jobParameters['timeStampPrev']}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDateTime timeStampPrev) {
        return new RepositoryCollectionWriter<>(forecasts -> {
            Forecast base = forecasts.stream().findFirst().get();
            List<Forecast> existing = forecastRepository.find(timeStampPrev,
                    base.getCity(), base.getProvider(), base.getForecastType());
            forecastRepository.saveAll(forecasts.stream()
                    .map(forecast -> updateForecast(existing, forecast))
                    .toList());
        });
    }

    @Bean("persistResponse")
    public Step persistResponse() {
        return new StepBuilder("persist-response", jobRepository)
                .<City, Collection<Response>>chunk(50, transactionManager)
                .reader(cityRepositoryReader())
                .processor(cityProcessor(null, null))
                .writer(responseWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean("persistForecast")
    public Step persistForecast() {
        return new StepBuilder("persist-forecast", jobRepository)
                .<Response, Collection<Forecast>>chunk(50, transactionManager)
                .reader(repositoryResponseReader(null))
                .processor(responseProcessor(null))
                .writer(updateForecast(null))
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean(name = "collectForecast")
    public Job runForecastJob() {
        return new JobBuilder("forecastJob", jobRepository)
                .flow(persistResponse())
                .next(persistForecast())
                .end()
                .build();
    }

    @Bean
    @Qualifier("asyncTaskExecutor")
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }

    private Map<String, WeatherDataProvider> getActiveProviderServices() {
        return providerRepository.findAll().stream()
                .filter(Provider::isActive)
                .collect(Collectors.toMap(Provider::getCredential, provider ->
                        applicationContext.getBean(provider.getCredential(), WeatherDataProvider.class)));
    }

    private Forecast updateForecast(Collection<Forecast> existing, Forecast update) {
        if (existing.isEmpty()) {
            return update;
        }
        Long id = existing.stream().filter(update::equals).map(Forecast::getId).findAny().orElse(null);
        update.setId(id);
        return update;
    }
}
