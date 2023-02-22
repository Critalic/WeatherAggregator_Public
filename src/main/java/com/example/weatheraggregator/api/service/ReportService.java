package com.example.weatheraggregator.api.service;

import com.example.weatheraggregator.api.persistence.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@PropertySource("classpath:properties/collector.properties")
public class ReportService {
    @Value("${report.directory}")
    private String reportDirectoryPath;

    private final ProviderRepository providerRepository;

    public ReportService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public Resource getReportForProvider(String providerCredential, String reportType) {
        boolean isProviderActive = providerRepository.findByCredential(providerCredential).orElseThrow(() ->
                new IllegalArgumentException(String.format("Couldn't find report for the credential specified:" +
                        " %s", providerCredential))).isActive();

        if (isProviderActive) {
            File outputFile = new File(String.format("%s/%s-%s.json", reportDirectoryPath, reportType, providerCredential));
            return new FileSystemResource(outputFile);
        }
        throw new IllegalArgumentException(String.format("Specified provider: %s is currently inactive",
                providerCredential));
    }
}
