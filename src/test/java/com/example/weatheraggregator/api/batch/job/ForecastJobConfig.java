package com.example.weatheraggregator.api.batch.job;

import com.example.weatheraggregator.api.persistence.repository.*;
import com.example.weatheraggregator.collector.provider.visualcrossing.VisualCrossing;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedList;

@ExtendWith(MockitoExtension.class)
@TestConfiguration
public class ForecastJobConfig {
    String apiKey = "EDK793CFQSEEFUSU7PKCSFAPQ";

    @Autowired
    private ForecastTypeRepository forecastTypeRepository;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private ResponseRepository responseRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private ForecastRepository forecastRepository;
    @Mock
    private ApplicationContext applicationContext;

//    @Bean
//    public CollectorStepConfig collectorConfig() {
//        MockitoAnnotations.openMocks(this);
//        when(applicationContext.getBean("VisualCrossing", WeatherDataProvider.class)).thenReturn(visualCrossing());
//        return new CollectorStepConfig(jobRepository, jobBuilderFactory, stepBuilderFactory, transactionManager, providerRepository,
//                forecastRepository, forecastTypeRepository, responseRepository, cityRepository, applicationContext);
//    }

//    @Bean(name = "collectForecast")
//    public Job runForecastJob() {
//        return collectorConfig().runForecastJob();
//    }

    @Bean("VisualCrossing")
    public VisualCrossing visualCrossing() {
        LinkedList<String> apiKeys = new LinkedList<>();
        apiKeys.add(apiKey);
        return new VisualCrossing(WebClient.builder(), apiKeys, "VisualCrossing");
    }
}
