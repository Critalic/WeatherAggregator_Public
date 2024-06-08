package com.example.weatheraggregator.api.batch.processor;

import com.example.weatheraggregator.api.persistence.entity.HistoricalData;
import com.example.weatheraggregator.api.persistence.entity.Response;
import com.example.weatheraggregator.collector.provider.HistoricalWeatherDataProvider;
import com.example.weatheraggregator.collector.provider.openmeteo.OpenMeteo;
import com.example.weatheraggregator.dto.business.ResponseDTO;
import lombok.NonNull;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class ResponseToHistoricalDataProcessor implements ItemProcessor<Collection<Response>, Collection<HistoricalData>> {
    private final LocalDateTime timeStamp;
    private final HistoricalWeatherDataProvider openMeteo;

    public ResponseToHistoricalDataProcessor(LocalDateTime timeStamp, HistoricalWeatherDataProvider providerServices) {
        this.timeStamp = timeStamp;
        this.openMeteo = providerServices;
    }


    @Override
    public Collection<HistoricalData> process(@NonNull Collection<Response> responses) {
        Collection<HistoricalData> historicalData = new ArrayList<>();
        for (Response response : responses) {
            historicalData.addAll(openMeteo.parseResponse(new ResponseDTO(response))
                    .stream().map(forecastDTO -> new HistoricalData(forecastDTO.getWindSpeed(),
                            forecastDTO.getWindDirection(),
                            forecastDTO.getTemperature(),
                            forecastDTO.getPressure(),
                            forecastDTO.getHumidity(),
                            forecastDTO.getTime(),
                            forecastDTO.getConditions(),
                            response.getCity(),
                            response.getForecastType(),
                            timeStamp)).toList());
        }

        return historicalData;
    }
}
