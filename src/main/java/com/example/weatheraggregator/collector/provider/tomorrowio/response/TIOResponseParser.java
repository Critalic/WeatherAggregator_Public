package com.example.weatheraggregator.collector.provider.tomorrowio.response;

import com.example.weatheraggregator.collector.exception.WrongResponseSizeException;
import com.example.weatheraggregator.collector.model.TimeStep;
import com.example.weatheraggregator.collector.provider.tomorrowio.response.model.Data;
import com.example.weatheraggregator.collector.provider.tomorrowio.response.model.Interval;
import com.example.weatheraggregator.collector.provider.tomorrowio.response.model.ResultTIO;
import com.example.weatheraggregator.dto.business.ForecastDTO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class TIOResponseParser {

    public List<ForecastDTO> parseForecast(String data, TimeStep timeStep) {
        ResultTIO result = parseTIOResult(data);

        switch (timeStep) {
            case DAY:
                return result.getIntervals().stream()
                        .map(interval -> {
                            ForecastDTO response = setCommonIntervalFields(interval);
                            response.setTime(Instant.parse(interval.getStartTime()).atZone(ZoneOffset.UTC)
                                    .toLocalDateTime().truncatedTo(ChronoUnit.DAYS));
                            return response;
                        })
                        .collect(Collectors.toList());
            case HOUR:
                return result.getIntervals().stream()
                        .map(interval -> {
                            ForecastDTO response = setCommonIntervalFields(interval);
                            response.setTime(Instant.parse(interval.getStartTime()).atZone(ZoneOffset.UTC)
                                    .toLocalDateTime());
                            return response;
                        })
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unexpected timeStep format provided: " + timeStep);
        }
    }

    private ForecastDTO setCommonIntervalFields(Interval interval) {
        Data responseData = interval.getValues();
        return new ForecastDTO(
                responseData.getWindSpeed(),
                responseData.getWindDirection(),
                responseData.getTemperature(),
                responseData.getPressure(),
                responseData.getHumidity(),
                TIOWeatherCodeMapper.getCodeDescription(responseData.getWeatherCode())
        );
    }

    private ResultTIO parseTIOResult(String data) {
        JsonArray responseArray;
        responseArray = new JsonParser().parse(data)
                .getAsJsonObject()
                .get("data").getAsJsonObject()
                .get("timelines").getAsJsonArray();

        if (responseArray.size() > 1) {
            throw new WrongResponseSizeException("API response array has wrong size");
        }
        return new Gson().fromJson(
                responseArray.get(0).getAsJsonObject().toString(), ResultTIO.class);
    }
}
