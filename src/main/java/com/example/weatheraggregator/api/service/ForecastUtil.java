package com.example.weatheraggregator.api.service;

import com.example.weatheraggregator.api.persistence.entity.Forecast;
import com.example.weatheraggregator.dto.request.MeasurementUnit;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface ForecastUtil {
    static Page<Forecast> convertMeasurementUnits(Page<Forecast> forecasts, MeasurementUnit measurementUnit) {
        return switch (measurementUnit) {
            case UK -> forecasts.map(forecast -> {
                forecast.setWindSpeed(toMpH(forecast.getWindSpeed()));
                return forecast;
            });
            case US -> forecasts.map(forecast -> {
                forecast.setWindSpeed(toMpH(forecast.getWindSpeed()));
                forecast.setTemperature(toFahrenheit(forecast.getWindSpeed()));
                return forecast;
            });
            default -> forecasts;
        };
    }

    private static double toFahrenheit(double celsius) {
        return (celsius * 1.8) + 32;
    }

    private static double toMpH(double kph) {
        return 0.6214 * kph;
    }

    static void verifyDates(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException(String.format("The start date – %s is after the end – %s", start, end));
        }
    }
}
