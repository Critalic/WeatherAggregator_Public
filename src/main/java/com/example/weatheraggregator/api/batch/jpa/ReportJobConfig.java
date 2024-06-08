package com.example.weatheraggregator.api.batch.jpa;

import com.example.weatheraggregator.api.persistence.entity.Forecast;
import com.example.weatheraggregator.api.persistence.entity.ForecastType;
import com.example.weatheraggregator.api.persistence.repository.ForecastRepository;
import com.example.weatheraggregator.api.persistence.repository.ForecastTypeRepository;
import com.example.weatheraggregator.dto.business.ForecastStatDTO;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Collections;

@Configuration
public class ReportJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ForecastRepository forecastRepository;
    private final ForecastTypeRepository forecastTypeRepository;

    public ReportJobConfig(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           ForecastRepository forecastRepository,
                           ForecastTypeRepository forecastTypeRepository) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.forecastRepository = forecastRepository;
        this.forecastTypeRepository = forecastTypeRepository;
    }

    @Bean(name = "forecastReader")
    @StepScope
    public RepositoryItemReader<Forecast> cityRepositoryReader(
            @Value("#{jobParameters['startTime']}") LocalDateTime startTime,
            @Value("#{jobParameters['endTime']}") LocalDateTime endTime,
            @Value("#{jobParameters['providerCredential']}") String providerCredential,
            @Value("#{jobParameters['forecastType']}") String forecastType) {
        ForecastType type = forecastTypeRepository.findByType(forecastType);
        return new RepositoryItemReaderBuilder<Forecast>()
                .name("forecastRepositoryReader")
                .methodName("findForProvider")
                .arguments(type, providerCredential, startTime, endTime)
                .repository(forecastRepository)
                .sorts(Collections.singletonMap("city.name", Sort.Direction.ASC))
                .build();
    }

    @Bean(name = "forecastToDTOProcessor")
    @StepScope
    public ItemProcessor<Forecast, ForecastStatDTO> forecastToDTOProcessor() {
        return ForecastStatDTO::new;
    }

    @StepScope
    @Bean(name = "forecastDTOFileWriter")
    public JsonFileItemWriter<ForecastStatDTO> forecastDTOFileWriter(
            @Value("#{jobParameters['outputFilePath']}") String outputFilePath) {
        File outputFile = new File(outputFilePath);
        FileSystemResource fileSystemResource = new FileSystemResource(outputFile);
        return new JsonFileItemWriterBuilder<ForecastStatDTO>()
                .name("forecastDTOFileWriter")
                .resource(fileSystemResource)
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .build();
    }

    @Bean("reportStep")
    public Step reportStep() {
        return new StepBuilder("forecast-report", jobRepository)
                .<Forecast, ForecastStatDTO>chunk(10, transactionManager)
                .reader(cityRepositoryReader(null, null, null, null))
                .processor(forecastToDTOProcessor())
                .writer(forecastDTOFileWriter(null))
                .build();
    }

    @Bean(name = "createReport")
    public Job runFileJob() {
        return new JobBuilder("reportJob", jobRepository)
                .start(reportStep())
                .build();
    }
}
