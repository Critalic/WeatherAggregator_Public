package com.example.weatheraggregator.dto.request;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForecastRequest {
    @NotNull(message = "End time can't be empty")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @NotNull(message = "End time can't be empty")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    @NotEmpty(message = "Forecast type can't be empty")
    private String type;
    private MeasurementUnit unit;
    private double lat;
    private double lng;
}
