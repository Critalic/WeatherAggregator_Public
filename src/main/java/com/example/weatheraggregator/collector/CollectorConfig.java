package com.example.weatheraggregator.collector;

import com.example.weatheraggregator.api.persistence.entity.Forecast;
import com.example.weatheraggregator.api.persistence.entity.Provider;
import com.example.weatheraggregator.api.persistence.repository.ForecastRepository;
import com.example.weatheraggregator.api.persistence.repository.ProviderRepository;
import com.example.weatheraggregator.collector.provider.tomorrowio.TomorrowIO;
import com.example.weatheraggregator.collector.provider.visualcrossing.VisualCrossing;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableScheduling
//@EnableBatchProcessing
@PropertySource("classpath:properties/collector.properties")
@ConditionalOnProperty(name = "scheduling.enabled", matchIfMissing = true)
public class CollectorConfig {
    private final WebClient.Builder builder;
    private final JobLauncher jobLauncher;
    private final Job collectJob;
    private final Job reportJob;
    private final ForecastRepository forecastRepository;
    private final ProviderRepository providerRepository;

    @Value("${collector.intervals}")
    private String timeIntervals;
    @Value("${tomorrowIo.api.key}")
    private String tomorrowioApiKey;
    @Value("${visualCrossing.api.key}")
    private String visualCrossingApiKey;
    @Value("${collector.delay}")
    private String delay;
    @Value("${report.directory}")
    private String reportDirecoryPath;

    public CollectorConfig(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") WebClient.Builder builder,
                           JobLauncher jobLauncher,
                           @Qualifier("collectForecast") Job collectJob,
                           @Qualifier("createReport") Job reportJob,
                           ForecastRepository forecastRepository,
                           ProviderRepository providerRepository) {
        this.builder = builder;
        this.jobLauncher = jobLauncher;
        this.collectJob = collectJob;
        this.forecastRepository = forecastRepository;
        this.reportJob = reportJob;
        this.providerRepository = providerRepository;
    }

    @Scheduled(initialDelay = 1000L, fixedDelayString = "${collector.delay}")
    public void fetchData() {
        Optional<Forecast> forecast = forecastRepository.findFirstByOrderByTimeStampDesc();

        LocalDateTime current = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime previous = current.minus(Duration.parse(delay));
        if (forecast.isPresent()) {
            previous = forecast.get().getTimeStamp();
        }
        JobParameters collectJobParams = new JobParametersBuilder()
                .addString("timeStamp", current.toString())
                .addString("timeStampPrev", previous.toString())
                .addString("intervals", timeIntervals)
                .toJobParameters();

        try {
            jobLauncher.run(collectJob, collectJobParams);
            for (JobParameters j : getReportJobParameters(providerRepository.findAll())) {
                jobLauncher.run(reportJob, j);
            }
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }

    private List<JobParameters> getReportJobParameters(List<Provider> providers) {
        List<JobParameters> response = new ArrayList<>();
        providers.forEach(provider -> {
            response.add(new JobParametersBuilder()
                    .addString("startTime", LocalDateTime.now().minusDays(7).truncatedTo(ChronoUnit.DAYS).toString())
                    .addString("endTime", LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).toString())
                    .addString("providerCredential", provider.getCredential())
                    .addString("forecastType", "Hourly")
                    .addString("outputFilePath", String.format(
                            "%s/week-hr-%s.json", reportDirecoryPath, provider.getCredential()))
                    .toJobParameters());
            response.add(new JobParametersBuilder()
                    .addString("startTime", LocalDateTime.now().minusDays(28).truncatedTo(ChronoUnit.DAYS).toString())
                    .addString("endTime", LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).toString())
                    .addString("providerCredential", provider.getCredential())
                    .addString("forecastType", "Daily")
                    .addString("outputFilePath", String.format(
                            "%s/month-d-%s.json", reportDirecoryPath, provider.getCredential()))
                    .toJobParameters());
        });
        return response;
    }

    @Bean("TomorrowIO")
    public TomorrowIO tomorrowIO() {
        return new TomorrowIO(builder, tomorrowioApiKey, "TomorrowIO");
    }

    @Bean("VisualCrossing")
    public VisualCrossing visualCrossing() {
        return new VisualCrossing(builder, visualCrossingApiKey, "VisualCrossing");
    }
}
