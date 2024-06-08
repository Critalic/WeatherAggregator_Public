package com.example.weatheraggregator.collector.provider.openmeteo.response;

import com.example.weatheraggregator.collector.exception.WrongResponseSizeException;
import com.example.weatheraggregator.collector.provider.openmeteo.response.model.DailyData;
import com.example.weatheraggregator.collector.provider.openmeteo.response.model.HourlyData;
import com.example.weatheraggregator.collector.provider.openmeteo.response.model.OMDailyResponse;
import com.example.weatheraggregator.collector.provider.openmeteo.response.model.OMHourlyResponse;
import com.example.weatheraggregator.dto.business.ForecastDTO;
import com.example.weatheraggregator.dto.business.ResponseDTO;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OMResponseParser {
    private static final DateTimeFormatter HOUR_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter DAY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<ForecastDTO> parseForecast(ResponseDTO responseDTO) {
        switch (responseDTO.getTimeStep()) {
            case DAY:
                OMDailyResponse responseDaily = new Gson().fromJson(responseDTO.getData(), OMDailyResponse.class);
                DailyData dataDaily = responseDaily.getDaily();
                validateDailyResponseLength(dataDaily);
                return extractDailyData(dataDaily);
            case HOUR:
                OMHourlyResponse responseHourly = new Gson().fromJson(responseDTO.getData(), OMHourlyResponse.class);
                HourlyData dataHourly = responseHourly.getHourly();
                validateDailyResponseLength(dataHourly);
                return extractHourlyData(dataHourly);
            default:
                throw new IllegalArgumentException("Unknown time step " + responseDTO.getTimeStep());
        }

    }

    private void validateDailyResponseLength(Object data) {
        int length = -1;
        try {
            for (Field field : data.getClass().getFields()) {
                field.setAccessible(true);

                if (length == -1) {
                    length = ((List<?>) field.get(data)).size();
                } else if (((List<?>) field.get(data)).size() != length) {
                    throw new WrongResponseSizeException("API response array has wrong size");
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ForecastDTO> extractDailyData(DailyData data) {
        List<ForecastDTO> forecastDTOList = new ArrayList<>();
        for (int i = 0; i < data.getTime().size(); i++) {

            try {
                ForecastDTO forecast = new ForecastDTO(
                        data.getWindSpeedMax().get(i),
                        data.getWindDirection().get(i),
                        (data.getTemperatureMax().get(i) + data.getTemperatureMin().get(i)) / 2,
                        null,
                        null,
                        LocalDate.parse(data.getTime().get(i), DAY_TIME_FORMATTER).atStartOfDay(),
                        OMWeatherCodes.getCodeDescription(data.getWeatherCode().get(i))

                );
                forecastDTOList.add(forecast);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return forecastDTOList;
    }

    private List<ForecastDTO> extractHourlyData(HourlyData data) {
        List<ForecastDTO> forecastDTOList = new ArrayList<>();
        for (int i = 0; i < data.getTime().size(); i++) {
            try {
                ForecastDTO forecast = new ForecastDTO(
                        data.getWindSpeed().get(i),
                        data.getWindDirection().get(i),
                        data.getTemperature().get(i),
                        data.getSurfacePressure().get(i),
                        data.getRelativeHumidity().get(i),
                        LocalDateTime.parse(data.getTime().get(i), HOUR_TIME_FORMATTER),
                        OMWeatherCodes.getCodeDescription(data.getWeatherCode().get(i))
                );
                forecastDTOList.add(forecast);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return forecastDTOList;
    }
}
