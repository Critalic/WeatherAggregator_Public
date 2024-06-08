package com.example.weatheraggregator.collector.provider.visualcrossing.response;

import com.example.weatheraggregator.collector.model.TimeStep;
import com.example.weatheraggregator.collector.provider.visualcrossing.response.model.DayData;
import com.example.weatheraggregator.collector.provider.visualcrossing.response.model.ResultVC;
import com.example.weatheraggregator.dto.business.ForecastDTO;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VCResponseParser {

    public List<ForecastDTO> parseForecast(String data, TimeStep timeStep) {
        ResultVC result = new Gson().fromJson(data, ResultVC.class);

        switch (timeStep) {
            case DAY:
                return getForecastDayData(result.getDayData());
            case HOUR:
                return getForecastHourData(result.getDayData());
            case DAY_HOUR:
                return Stream.of(
                                getForecastDayData(result.getDayData()),
                                getForecastHourData(result.getDayData()))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unexpected timeStep format provided: " + timeStep);
        }
    }

    private List<ForecastDTO> getForecastDayData(List<DayData> dayData) {
        return dayData.stream()
                .map(data -> new ForecastDTO(
                        data.getWindSpeed(),
                        data.getWindDirection(),
                        data.getTemperature(),
                        data.getPressure(),
                        data.getHumidity(),
                        LocalDate.parse(data.getDate()).atStartOfDay(),
                        data.getConditions()))
                .collect(Collectors.toList());
    }

    private List<ForecastDTO> getForecastHourData(List<DayData> dayData) {
        return dayData.stream()
                .flatMap(data -> {
                    LocalDate date = LocalDate.parse(data.getDate());
                    return data.getHourData().stream()
                            .map(hourData -> new ForecastDTO(
                                    hourData.getWindSpeed(),
                                    hourData.getWindDirection(),
                                    hourData.getTemperature(),
                                    hourData.getPressure(),
                                    hourData.getHumidity(),
                                    LocalDateTime.of(date, LocalTime.parse(hourData.getTime())),
                                    hourData.getConditions()));
                }).collect(Collectors.toList());
    }
}
