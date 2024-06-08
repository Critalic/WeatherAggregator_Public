package com.example.weatheraggregator.api.controller;

import com.example.weatheraggregator.api.service.ReportService;
import com.example.weatheraggregator.dto.response.ExceptionResponseDTO;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/report")
public class ReportController {
    private final ReportService reportService;
    private final JobExplorer jobExplorer;

    public ReportController(ReportService reportService, JobExplorer jobExplorer) {
        this.reportService = reportService;
        this.jobExplorer = jobExplorer;
    }

    @GetMapping("/weekly/{providerCredential}")
    public ResponseEntity<?> getWeeklyReport(WebRequest request,
                                             @PathVariable("providerCredential") String providerCredential)
            throws IOException {
        if (anyJobsRunning()) {
            return getUnableToServeErrorDTO(request);
        }

        Resource resource = reportService.getReportForProvider(providerCredential, "week-hr");
        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename="
                        + resource.getFilename())
                .body(resource);
    }

    @GetMapping("/monthly/{providerCredential}")
    public ResponseEntity<?> getMonthlyReport(WebRequest request,
                                                     @PathVariable("providerCredential") String providerCredential)
            throws IOException {
        if (anyJobsRunning()) {
            return getUnableToServeErrorDTO(request);
        }

        Resource resource = reportService.getReportForProvider(providerCredential, "month-d");
        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename="
                        + resource.getFilename())
                .body(resource);
    }

    private boolean anyJobsRunning() {
        return Stream.concat(jobExplorer.findRunningJobExecutions("reportJob").stream(),
                        jobExplorer.findRunningJobExecutions("forecastJob").stream())
                .anyMatch(JobExecution::isRunning);
    }

    private ResponseEntity<ExceptionResponseDTO> getUnableToServeErrorDTO(WebRequest request) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ExceptionResponseDTO(
                "Server is currently unable to perform this request",
                request.getDescription(false),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                LocalDateTime.now().toString()));
    }
}
