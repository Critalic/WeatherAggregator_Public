package com.example.weatheraggregator.api.batch.processor;

import com.example.weatheraggregator.api.persistence.entity.City;
import com.example.weatheraggregator.api.persistence.entity.ForecastType;
import com.example.weatheraggregator.api.persistence.entity.Provider;
import com.example.weatheraggregator.api.persistence.entity.Response;
import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.collector.provider.WeatherForecastDataProvider;
import com.example.weatheraggregator.dto.business.CityDTO;
import com.example.weatheraggregator.dto.business.ResponseDTO;
import lombok.NonNull;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CityForecastProcessor implements ItemProcessor<City, Collection<Response>> {
    private final LocalDateTime timeStamp;
    private final WeatherForecastDataProvider providerService;
    private final List<RequestAttributes> timeIntervals;
    private final Map<String, Provider> providers;
    private final Map<String, ForecastType> forecastTypes;

    public CityForecastProcessor(LocalDateTime timeStamp,
                                 List<RequestAttributes> timeIntervals,
                                 WeatherForecastDataProvider providerService,
                                 Map<String, Provider> providers,
                                 Map<String, ForecastType> forecastTypes) {
        this.timeStamp = timeStamp;
        this.providerService = providerService;
        this.timeIntervals = timeIntervals;
        this.providers = providers;
        this.forecastTypes = forecastTypes;
    }

    @Override
    public Collection<Response> process(@NonNull City city) {
        timeIntervals.forEach(interval -> {
            interval.setCity(new CityDTO(city));
            interval.setTimeStamp(timeStamp);
        });

        return timeIntervals.stream()
                .map(providerService::makeForecastRequest)
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
