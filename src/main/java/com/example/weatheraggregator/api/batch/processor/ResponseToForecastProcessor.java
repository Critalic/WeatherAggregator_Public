package com.example.weatheraggregator.api.batch.processor;

import com.example.weatheraggregator.api.persistence.entity.Forecast;
import com.example.weatheraggregator.api.persistence.entity.Response;
import com.example.weatheraggregator.collector.provider.WeatherForecastDataProvider;
import com.example.weatheraggregator.dto.business.ResponseDTO;
import lombok.NonNull;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;
import java.util.Collection;

public class ResponseToForecastProcessor implements ItemProcessor<Response, Collection<Forecast>> {
    private final LocalDateTime timeStamp;
    private final WeatherForecastDataProvider providerService;

    public ResponseToForecastProcessor(LocalDateTime timeStamp, WeatherForecastDataProvider providerServices) {
        this.timeStamp = timeStamp;
        this.providerService = providerServices;
    }


    @Override
    public Collection<Forecast> process(@NonNull Response response) {
        return providerService.parseResponse(new ResponseDTO(response))
                .stream().map(forecastDTO -> new Forecast(forecastDTO.getWindSpeed(),
                        forecastDTO.getWindDirection(),
                        forecastDTO.getTemperature(),
                        forecastDTO.getPressure(),
                        forecastDTO.getHumidity(),
                        forecastDTO.getTime(),
                        forecastDTO.getConditions(),
                        response.getCity(),
                        response.getForecastType(),
                        response.getProvider(),
                        timeStamp)).toList();
    }
}
