package com.example.weatheraggregator.api.batch.processor;

import com.example.weatheraggregator.api.persistence.entity.Forecast;
import com.example.weatheraggregator.api.persistence.entity.Response;
import com.example.weatheraggregator.collector.provider.WeatherDataProvider;
import com.example.weatheraggregator.dto.business.ResponseDTO;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public class ResponseToForecastProcessor implements ItemProcessor<Response, Collection<Forecast>> {
    private final LocalDateTime timeStamp;
    private final Map<String, WeatherDataProvider> providerServices;

    public ResponseToForecastProcessor(LocalDateTime timeStamp, Map<String, WeatherDataProvider> providerServices) {
        this.timeStamp = timeStamp;
        this.providerServices = providerServices;
    }


    @Override
    public Collection<Forecast> process(Response response) {
        return providerServices.get(response.getProvider().getCredential()).parseResponse(new ResponseDTO(response))
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
