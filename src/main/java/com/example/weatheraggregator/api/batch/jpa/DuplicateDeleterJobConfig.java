package com.example.weatheraggregator.api.batch.jpa;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DuplicateDeleterJobConfig {
    private final JobRepository jobRepository;
    private final ForecastCollectorJobConfig forecastCollectorJobConfig;

    public DuplicateDeleterJobConfig(JobRepository jobRepository, ForecastCollectorJobConfig forecastCollectorJobConfig) {
        this.jobRepository = jobRepository;
        this.forecastCollectorJobConfig = forecastCollectorJobConfig;
    }

    @Bean(name = "deleteDuplicatedForecasts")
    public Job runDeleteJob() {
        return new JobBuilder("duplicateDeleterJob", jobRepository)
                .flow(forecastCollectorJobConfig.dropOldForecasts())
                .end()
                .build();
    }

}
