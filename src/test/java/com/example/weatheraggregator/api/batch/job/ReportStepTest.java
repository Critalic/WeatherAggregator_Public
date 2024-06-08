package com.example.weatheraggregator.api.batch.job;

import com.example.weatheraggregator.api.batch.jpa.ReportJobConfig;
import com.example.weatheraggregator.api.persistence.repository.ForecastRepository;
import com.example.weatheraggregator.api.persistence.repository.ForecastTypeRepository;
import com.example.weatheraggregator.dto.business.ForecastStatDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
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
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.sql.DataSource;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;


//@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBatchTest
@ContextConfiguration(classes = {DataSource.class, ReportJobConfig.class, ForecastTypeRepository.class,
        ForecastRepository.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application-test.properties")
@EnableBatchProcessing
@EnableJpaRepositories(basePackages = {"com.example.weatheraggregator.api.persistence.repository"})
@EntityScan(basePackages = {"com.example.weatheraggregator.api.persistence.entity"})
class ReportStepTest {
    private File testFile;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @BeforeEach
    void setUp() {
        testFile = new File(String.format("src/test/resources/bulkReport/test-%s.json",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))));
    }

    @AfterEach
    void cleanUp() {
        testFile.delete();
    }

    @Test
    void testTypeConversion() throws Exception {
        LocalDateTime time = LocalDateTime.of(2023, 1, 10, 0, 0, 0);
        ObjectMapper objectMapper = new ObjectMapper();
        File expectedFile = new File("src/test/resources/bulkReport/comparison.json");

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("startTime", time.minusDays(30).truncatedTo(ChronoUnit.DAYS).toString())
                .addString("endTime", time.truncatedTo(ChronoUnit.DAYS).toString())
                .addString("providerCredential", "VisualCrossing")
                .addString("forecastType", "Daily")
                .addString("outputFilePath", testFile.getPath())
                .toJobParameters();
        JobExecution jobExecution =
                this.jobLauncherTestUtils.launchStep("forecast-report", jobParameters);
        List<ForecastStatDTO> expected = objectMapper.readValue(expectedFile, new TypeReference<>() {
        });
        List<ForecastStatDTO> result = objectMapper.readValue(testFile, new TypeReference<>() {
        });

        Assertions.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        Assertions.assertTrue(expected.size() == result.size() && result.containsAll(expected) &&
                expected.containsAll(result));
    }
}
