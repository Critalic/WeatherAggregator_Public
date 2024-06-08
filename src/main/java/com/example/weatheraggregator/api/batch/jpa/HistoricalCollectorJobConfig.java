package com.example.weatheraggregator.api.batch.jpa;

import com.example.weatheraggregator.api.batch.jpa.writer.RepositoryCollectionWriter;
import com.example.weatheraggregator.api.batch.processor.CityHistoricalProcessor;
import com.example.weatheraggregator.api.batch.processor.ResponseToHistoricalDataProcessor;
import com.example.weatheraggregator.api.persistence.entity.City;
import com.example.weatheraggregator.api.persistence.entity.ForecastType;
import com.example.weatheraggregator.api.persistence.entity.HistoricalData;
import com.example.weatheraggregator.api.persistence.entity.Provider;
import com.example.weatheraggregator.api.persistence.repository.ForecastTypeRepository;
import com.example.weatheraggregator.api.persistence.repository.HistoricalDataRepository;
import com.example.weatheraggregator.api.persistence.repository.ProviderRepository;
import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.collector.model.TimeStep;
import com.example.weatheraggregator.collector.provider.HistoricalWeatherDataProvider;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@PropertySource("classpath:properties/persistence.properties")
public class HistoricalCollectorJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ForecastTypeRepository forecastTypeRepository;
    private final HistoricalDataRepository historicalDataRepository;
    private final ForecastCollectorJobConfig forecastCollectorJobConfig;
    private final ApplicationContext applicationContext;


    private final Map<String, ForecastType> forecastTypes;
    private final Map<String, Provider> providers;

    public HistoricalCollectorJobConfig(JobRepository jobRepository,
                                        PlatformTransactionManager transactionManager,
                                        ForecastTypeRepository forecastTypeRepository1,
                                        ProviderRepository providerRepository,
                                        HistoricalDataRepository historicalDataRepository,
                                        ForecastTypeRepository forecastTypeRepository,
                                        ForecastCollectorJobConfig forecastCollectorJobConfig,
                                        ApplicationContext applicationContext) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.forecastTypeRepository = forecastTypeRepository1;
        this.historicalDataRepository = historicalDataRepository;
        this.forecastCollectorJobConfig = forecastCollectorJobConfig;
        this.applicationContext = applicationContext;

        providers = providerRepository.findAll().stream()
                .collect(Collectors.toMap(Provider::getCredential, item -> item));
        forecastTypes = forecastTypeRepository.findAll().stream()
                .collect(Collectors.toMap(ForecastType::getType, item -> item));
    }

    @Bean(name = "responseToHistoricalDataProcessor")
    @StepScope
    public ResponseToHistoricalDataProcessor responseToHistoricalDataProcessor(
            @Value("#{jobParameters['providerCredential']}") String providerCredential,
            @Value("#{jobParameters['timeStamp']}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDateTime timeStamp) {
        return new ResponseToHistoricalDataProcessor(timeStamp, getProviderServiceBean(providerCredential));
    }

    @Bean(name = "cityHistoricalProcessor")
    @StepScope
    public CityHistoricalProcessor cityHistoricalProcessor(
            @Value("#{jobParameters['providerCredential']}") String providerCredential,
            @Value("#{jobParameters['timeStamp']}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDateTime timeStamp,
            @Value("#{jobParameters['startDate']}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDateTime startDate,
            @Value("#{jobParameters['endDate']}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDateTime endDate) {
        RequestAttributes baseRequest = new RequestAttributes();
        baseRequest.setStartDate(startDate);
        baseRequest.setEndDate(endDate);
        baseRequest.setTimeStamp(timeStamp);

        Set<TimeStep> timeStepSet = forecastTypeRepository.findAll().stream()
                .map(forecastType -> TimeStep.getByType(forecastType.getType()))
                .collect(Collectors.toSet());

        return new CityHistoricalProcessor(timeStamp, getProviderServiceBean(providerCredential), providers,
                forecastTypes, timeStepSet, baseRequest);
    }

    @Bean(name = "compositeHistoricalProcessor")
    @StepScope
    public CompositeItemProcessor<City, Collection<HistoricalData>> compositeHistoricalProcessor() {
        CompositeItemProcessor<City, Collection<HistoricalData>> itemProcessor =
                new CompositeItemProcessor<>();
        itemProcessor.setDelegates(Arrays.asList(
                cityHistoricalProcessor(null, null, null, null),
                responseToHistoricalDataProcessor(null, null)));
        return itemProcessor;
    }

    @Bean(name = "historicalDataWriter")
    @StepScope
    public RepositoryCollectionWriter<HistoricalData> historicalDataWriter() {
        return new RepositoryCollectionWriter<>(historicalDataRepository::saveAll);
    }

    @Bean("requestHistoricalData")
    public Step requestHistoricalData() {
        return new StepBuilder("requestHistoricalData", jobRepository)
                .<City, Collection<HistoricalData>>chunk(50, transactionManager)
                .reader(forecastCollectorJobConfig.cityRepositoryReader())
                .processor(compositeHistoricalProcessor())
                .writer(historicalDataWriter())
                .taskExecutor(new SyncTaskExecutor())
                .build();
    }

    @Bean(name = "collectHistoricalData")
    public Job runHistoricalDataJob() {
        return new JobBuilder("historicalDataJob", jobRepository)
                .flow(requestHistoricalData())
                .end()
                .build();
    }

    private HistoricalWeatherDataProvider getProviderServiceBean(String providerCredential) {
        return applicationContext.getBean(providerCredential, HistoricalWeatherDataProvider.class);
    }
}
