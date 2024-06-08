package com.example.weatheraggregator.api.batch.job;

import com.example.weatheraggregator.api.persistence.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.sql.DataSource;
import java.time.LocalDateTime;


//@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBatchTest
@ContextConfiguration(classes = {DataSource.class, ForecastTypeRepository.class, ResponseRepository.class,
        CityRepository.class, ProviderRepository.class, ForecastRepository.class, JobLauncher.class,
        ForecastJobConfig.class})

@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application-test.properties")
@EnableBatchProcessing
@EnableJpaRepositories(basePackages = {"com.example.weatheraggregator.api.persistence.repository"})
@EntityScan(basePackages = {"com.example.weatheraggregator.api.persistence.entity"})
class ForecastJobTest {

    private final LocalDateTime now = LocalDateTime.now();
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private ResponseRepository responseRepository;

    @Test
    @Disabled
    void testResponseCollector() {
        JobParameters collectJobParams = new JobParametersBuilder()
                .addString("intervals", "1d, d")
                .addString("timeStamp", now.toString())
                .addString("timeStampPrev", now.minusDays(2).toString())
                .toJobParameters();

        JobExecution jobExecution =
                this.jobLauncherTestUtils.launchStep("persist-response", collectJobParams);

        Assertions.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        Assertions.assertEquals(1, responseRepository.findAll().size());
    }

    @Test
    @Disabled
    void testForecastCollector() {
        JobParameters collectJobParams = new JobParametersBuilder()
                .addString("intervals", "1d, d")
                .addString("timeStamp", now.toString())
                .addString("timeStampPrev", now.minusDays(2).toString())
                .toJobParameters();

        JobExecution jobExecution =
                this.jobLauncherTestUtils.launchStep("persist-forecast", collectJobParams);

        Assertions.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
}
