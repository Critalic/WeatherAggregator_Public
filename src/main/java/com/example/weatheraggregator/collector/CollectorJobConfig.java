package com.example.weatheraggregator.collector;

import com.example.weatheraggregator.api.persistence.entity.Provider;
import com.example.weatheraggregator.api.persistence.repository.ForecastRepository;
import com.example.weatheraggregator.api.persistence.repository.HistoricalDataRepository;
import com.example.weatheraggregator.api.persistence.repository.ProviderRepository;
import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.collector.provider.aggregator.AggregatorAPI;
import com.example.weatheraggregator.collector.provider.openmeteo.OpenMeteo;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
@PropertySource("classpath:properties/collector.properties")
@ConditionalOnProperty(name = "scheduling.enabled", matchIfMissing = true)
public class CollectorJobConfig {
    private final AggregatorAPI aggregatorAPI;
    private final OpenMeteo openMeteo;
    private final JobLauncher jobLauncher;
    private final Job collectForecastJob;
    private final Job collectHistoricalJob;
    private final Job reportJob;
    private final Job deleteDuplicatedJob;
    private final ForecastRepository forecastRepository;
    private final ProviderRepository providerRepository;
    private final HistoricalDataRepository historicalDataRepository;

    @Value("${collector.intervals}")
    private String timeIntervals;
    @Value("${report.directory}")
    private String reportDirecoryPath;

    public CollectorJobConfig(AggregatorAPI aggregatorAPI, OpenMeteo openMeteo,
                              JobLauncher jobLauncher,
                              @Qualifier("collectForecast") Job collectForecastJob,
                              @Qualifier("collectHistoricalData") Job collectHistoricalJob,
                              @Qualifier("createReport") Job reportJob,
                              @Qualifier("deleteDuplicatedForecasts") Job deleteDuplicatedJob,
                              ForecastRepository forecastRepository,
                              ProviderRepository providerRepository, HistoricalDataRepository historicalDataRepository) {
        this.aggregatorAPI = aggregatorAPI;
        this.openMeteo = openMeteo;
        this.jobLauncher = jobLauncher;
        this.collectForecastJob = collectForecastJob;
        this.collectHistoricalJob = collectHistoricalJob;
        this.reportJob = reportJob;
        this.deleteDuplicatedJob = deleteDuplicatedJob;
        this.forecastRepository = forecastRepository;
        this.providerRepository = providerRepository;
        this.historicalDataRepository = historicalDataRepository;
    }

    @Scheduled(initialDelay = 1000L, fixedDelayString = "${forecast.collector.delay}")
    public void fetchForecastData() {
        List<Provider> activeProviders = providerRepository.findByStatus(true);
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        activeProviders.forEach(provider -> {

            JobParameters collectJobParams = new JobParametersBuilder()
                    .addString("timeStamp", currentTime.toString())
                    .addString("intervals", provider.getCredential().equals(openMeteo.getCredential()) ? "4d, d; 4d, h" : timeIntervals)
                    .addString("providerCredential", provider.getCredential())
                    .toJobParameters();
            try {
                jobLauncher.run(collectForecastJob, collectJobParams);
                for (JobParameters j : getReportJobParameters(providerRepository.findAll())) {
                    jobLauncher.run(reportJob, j);
                }
            } catch (JobExecutionAlreadyRunningException | JobRestartException |
                     JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });

        RequestAttributes aggregatorParams = new RequestAttributes();
        aggregatorParams.setTimeStamp(currentTime);
        aggregatorAPI.makeForecastRequest(aggregatorParams);

        try {
            JobParameters deleteDuplicatesParams = new JobParametersBuilder()
                    .addString("timeStamp", currentTime.toString())
                    .addString("providerCredential", aggregatorAPI.getCredential())
                    .toJobParameters();
            jobLauncher.run(deleteDuplicatedJob, deleteDuplicatesParams);
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Scheduled(initialDelay = 1000L, fixedDelayString = "${historical.collector.delay}")
    public void fetchHistoricData() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDate start = LocalDate.of(2024, 06, 01);
        LocalDate end = LocalDate.of(2024, 06, 04);
        LocalDateTime previousTime = historicalDataRepository
                .getMaxTimestamp().orElseGet(() -> LocalDateTime.of(2020, 01, 01,
                        01, 01, 01));

        List<LocalDate> dateList = forecastRepository.getForecastedDaysAfter(previousTime).stream()
                .map(LocalDateTime::toLocalDate)
                .filter(date -> date.isBefore(LocalDate.now().minusDays(2)))
                .distinct()
                .sorted()
                .toList();

        long count = ChronoUnit.WEEKS.between(start, end);
        for (int i = 0; i < count; i++) {
            JobParameters collectJobParams = new JobParametersBuilder()
                    .addString("endDate", end.atStartOfDay().toString())
                    .addString("startDate", start.atStartOfDay().toString())
                    .addString("timeStamp", currentTime.toString())
                    .addString("providerCredential", openMeteo.getCredential())
                    .toJobParameters();


            try {
                jobLauncher.run(collectHistoricalJob, collectJobParams);
            } catch (JobExecutionAlreadyRunningException | JobRestartException |
                     JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            start = start.plusWeeks(1);
            end = start.plusWeeks(1);
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

}
