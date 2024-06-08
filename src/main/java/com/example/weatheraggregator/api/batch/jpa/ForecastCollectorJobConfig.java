package com.example.weatheraggregator.api.batch.jpa;

import com.example.weatheraggregator.api.batch.jpa.writer.RepositoryCollectionWriter;
import com.example.weatheraggregator.api.batch.processor.CityForecastProcessor;
import com.example.weatheraggregator.api.batch.processor.OldForecastDropper;
import com.example.weatheraggregator.api.batch.processor.ResponseToForecastProcessor;
import com.example.weatheraggregator.api.batch.util.PropertyParser;
import com.example.weatheraggregator.api.persistence.entity.*;
import com.example.weatheraggregator.api.persistence.repository.*;
import com.example.weatheraggregator.aspect.LogOnException;
import com.example.weatheraggregator.collector.provider.WeatherForecastDataProvider;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@PropertySource("classpath:properties/persistence.properties")
public class ForecastCollectorJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ResponseRepository responseRepository;
    private final CityRepository cityRepository;
    private final ForecastRepository forecastRepository;
    private final ApplicationContext applicationContext;

    private final Map<String, ForecastType> forecastTypes;
    private final Map<String, Provider> providers;

    public ForecastCollectorJobConfig(JobRepository jobRepository,
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
    public CityForecastProcessor cityProcessor(
            @Value("#{jobParameters['providerCredential']}") String providerCredential,
            @Value("#{jobParameters['intervals']}") String intervals,
            @Value("#{jobParameters['timeStamp']}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDateTime timeStamp) {
        return new CityForecastProcessor(timeStamp, PropertyParser.parseIntervals(intervals),
                getProviderServiceBean(providerCredential), providers, forecastTypes);
    }

    @Bean(name = "responseWriter")
    @StepScope
    public RepositoryCollectionWriter<Response> responseWriter() {
        return new RepositoryCollectionWriter<>(responseRepository::saveAll);
    }

    @Bean(name = "repositoryResponseReader")
    @StepScope
    public RepositoryItemReader<Response> repositoryResponseReader(
            @Value("#{jobParameters['providerCredential']}") String providerCredential,
            @Value("#{jobParameters['timeStamp']}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDateTime timeStamp) {
        return new RepositoryItemReaderBuilder<Response>()
                .name("responseRepositoryReader")
                .methodName("findByTimeStampAndProviderCredentialOrderByIdAsc")
                .repository(responseRepository)
                .arguments(timeStamp, providerCredential)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean(name = "responseToForecastProcessor")
    @StepScope
    public ResponseToForecastProcessor responseToForecastProcessor(
            @Value("#{jobParameters['providerCredential']}") String providerCredential,
            @Value("#{jobParameters['timeStamp']}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDateTime timeStamp) {
        return new ResponseToForecastProcessor(timeStamp, getProviderServiceBean(providerCredential));
    }

    @Bean(name = "forecastWriter")
    @StepScope
    public RepositoryCollectionWriter<Forecast> forecastWriter() {
        return new RepositoryCollectionWriter<>(forecastRepository::saveAll);
    }

    @Bean(name = "oldForecastDropper")
    @StepScope
    public ItemWriter<City> oldForecastDropper(
            @Value("#{jobParameters['providerCredential']}") String providerCredential,
            @Value("#{jobParameters['timeStamp']}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDateTime timeStampCurrent
    ) {
        return new OldForecastDropper(timeStampCurrent, providerCredential, forecastRepository);
    }

    @Bean("persistResponse")
    public Step persistResponse() {
        return new StepBuilder("persist-response", jobRepository)
                .<City, Collection<Response>>chunk(50, transactionManager)
                .reader(cityRepositoryReader())
                .processor(cityProcessor(null, null, null))
                .writer(responseWriter())
                .taskExecutor(new SyncTaskExecutor())
                .build();
    }

    @Bean("persistForecast")
    public Step persistForecast() {
        return new StepBuilder("persist-forecast", jobRepository)
                .<Response, Collection<Forecast>>chunk(50, transactionManager)
                .reader(repositoryResponseReader(null, null))
                .processor(responseToForecastProcessor(null, null))
                .writer(forecastWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean("deleteOldForecasts")
    public Step dropOldForecasts() {
        return new StepBuilder("drop-old-forecast", jobRepository)
                .<City, City>chunk(50, transactionManager)
                .reader(cityRepositoryReader())
                .writer(oldForecastDropper(null, null))
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean(name = "collectForecast")
    public Job runForecastJob() {
        return new JobBuilder("forecastJob", jobRepository)
                .flow(persistResponse())
                .next(persistForecast())
                .next(dropOldForecasts())
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

    private WeatherForecastDataProvider getProviderServiceBean(String providerCredential) {
        return applicationContext.getBean(providerCredential, WeatherForecastDataProvider.class);
    }
}
