package com.example.weatheraggregator.api.service;

import com.example.weatheraggregator.api.persistence.entity.Forecast;
import com.example.weatheraggregator.dto.business.ForecastDTO;
import com.example.weatheraggregator.dto.request.MeasurementUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        if(start.isAfter(end)) {
            throw new IllegalArgumentException(String.format("The start date – %s is after the end – %s", start, end));
        }
    }

    static Page<ForecastDTO> aggregateForecasts(Page<Forecast> forecasts) {
        List<ForecastDTO> forecastDTOs = forecasts.stream()
                .map(Forecast::getTime)
                .distinct()
                .map(time -> aggregate(time, forecasts))
                .toList();
        return new PageImpl<>(forecastDTOs);
    }

    private static ForecastDTO aggregate(LocalDateTime time, Page<Forecast> forecasts) {
        ForecastDTO forecastDTO = new ForecastDTO(0D, 0D, 0D, 0D, 0D,
                time, "Unknown");
        List<Forecast> forecastList = forecasts.stream()
                .filter(forecast -> forecast.getTime().equals(time))
                .toList();

        forecastList.forEach((forecast -> {
            forecastDTO.setHumidity(forecastDTO.getHumidity() + forecast.getHumidity());
            forecastDTO.setPressure(forecastDTO.getPressure() + forecast.getPressure());
            forecastDTO.setTemperature(forecastDTO.getTemperature() + forecast.getTemperature());
            forecastDTO.setWindDirection(forecastDTO.getWindDirection() + forecast.getWindDirection());
            forecastDTO.setWindSpeed(forecastDTO.getWindSpeed() + forecast.getWindSpeed());
        }));

        forecastList.stream()
                .max(Comparator.comparing(forecast -> forecast.getProvider().getTrustCoefficient()))
                .ifPresent(forecast -> forecastDTO.setConditions(forecast.getConditions()));

        int size = forecastList.size();
        forecastDTO.setHumidity(forecastDTO.getHumidity() / size);
        forecastDTO.setTemperature(forecastDTO.getTemperature() / size);
        forecastDTO.setPressure(forecastDTO.getPressure() / size);
        forecastDTO.setWindSpeed(forecastDTO.getWindSpeed() / size);
        forecastDTO.setWindDirection(forecastDTO.getWindDirection() / size);
        return forecastDTO;
    }
}
