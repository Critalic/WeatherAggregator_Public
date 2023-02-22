package com.example.weatheraggregator.api.batch.processor;

import com.example.weatheraggregator.api.persistence.entity.City;
import com.example.weatheraggregator.api.persistence.entity.ForecastType;
import com.example.weatheraggregator.api.persistence.entity.Provider;
import com.example.weatheraggregator.api.persistence.entity.Response;
import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.collector.provider.WeatherDataProvider;
import com.example.weatheraggregator.dto.business.CityDTO;
import com.example.weatheraggregator.dto.business.ResponseDTO;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CityProcessor implements ItemProcessor<City, Collection<Response>> {
    private final LocalDateTime timeStamp;
    private final Map<String, WeatherDataProvider> providerServices;
    private final List<RequestAttributes> timeIntervals;
    private final Map<String, Provider> providers;
    private final Map<String, ForecastType> forecastTypes;

    public CityProcessor(LocalDateTime timeStamp,
                         List<RequestAttributes> timeIntervals,
                         Map<String, WeatherDataProvider> providerServices,
                         Map<String, Provider> providers,
                         Map<String, ForecastType> forecastTypes) {
        this.timeStamp = timeStamp;
        this.providerServices = providerServices;
        this.timeIntervals = timeIntervals;
        this.providers = providers;
        this.forecastTypes = forecastTypes;
    }

    @Override
    public Collection<Response> process(City city) {
        timeIntervals.forEach(interval -> {
            interval.setCity(new CityDTO(city));
            interval.setTimeStamp(timeStamp);
        });

        return providerServices.values().stream()
                .flatMap(provider -> timeIntervals.stream().map(provider::makeRequest))
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
