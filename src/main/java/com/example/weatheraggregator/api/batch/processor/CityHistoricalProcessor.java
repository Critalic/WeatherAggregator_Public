package com.example.weatheraggregator.api.batch.processor;

import com.example.weatheraggregator.api.persistence.entity.City;
import com.example.weatheraggregator.api.persistence.entity.ForecastType;
import com.example.weatheraggregator.api.persistence.entity.Provider;
import com.example.weatheraggregator.api.persistence.entity.Response;
import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.collector.model.TimeStep;
import com.example.weatheraggregator.collector.provider.HistoricalWeatherDataProvider;
import com.example.weatheraggregator.dto.business.CityDTO;
import com.example.weatheraggregator.dto.business.ResponseDTO;
import lombok.NonNull;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class CityHistoricalProcessor implements ItemProcessor<City, Collection<Response>> {
    private final LocalDateTime timeStamp;
    private final HistoricalWeatherDataProvider openMeteoProvider;
    private final Map<String, Provider> providers;
    private final Map<String, ForecastType> forecastTypes;
    private final Set<TimeStep> timeSteps;
    private final RequestAttributes baseRequest;

    public CityHistoricalProcessor(LocalDateTime timeStamp,
                                   HistoricalWeatherDataProvider providerService,
                                   Map<String, Provider> providers,
                                   Map<String, ForecastType> forecastTypes,
                                   Set<TimeStep> timeSteps,
                                   RequestAttributes baseRequest) {
        this.timeStamp = timeStamp;
        this.openMeteoProvider = providerService;
        this.providers = providers;
        this.forecastTypes = forecastTypes;
        this.timeSteps = timeSteps;
        this.baseRequest = baseRequest;
    }

    @Override
    public Collection<Response> process(@NonNull City city) {
        baseRequest.setCity(new CityDTO(city));
        return timeSteps.stream()
                .map(timeStep -> {
                    baseRequest.setTimeStep(timeStep);
                    return openMeteoProvider.makeHistoricalRequest(baseRequest);
                })
                .filter(responseDTO -> !Objects.isNull(responseDTO))
                .map(responseDTO -> mapToResponse(responseDTO, city))
                .toList();
    }

    private Response mapToResponse(ResponseDTO responseDTO, City city) {
        return new Response(responseDTO.getData(), timeStamp, city,
                forecastTypes.get(responseDTO.getTimeStep().getType()),
                providers.get(responseDTO.getProviderCredential()));
    }
}
