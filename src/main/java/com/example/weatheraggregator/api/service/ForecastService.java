package com.example.weatheraggregator.api.service;

import com.example.weatheraggregator.api.persistence.entity.City;
import com.example.weatheraggregator.api.persistence.entity.Forecast;
import com.example.weatheraggregator.api.persistence.entity.ForecastType;
import com.example.weatheraggregator.api.persistence.entity.Provider;
import com.example.weatheraggregator.api.persistence.repository.CityRepository;
import com.example.weatheraggregator.api.persistence.repository.ForecastRepository;
import com.example.weatheraggregator.api.persistence.repository.ForecastTypeRepository;
import com.example.weatheraggregator.api.persistence.repository.ProviderRepository;
import com.example.weatheraggregator.collector.provider.aggregator.AggregatorAPI;
import com.example.weatheraggregator.dto.business.CityDTO;
import com.example.weatheraggregator.dto.business.ForecastDTO;
import com.example.weatheraggregator.dto.business.ForecastPageDTO;
import com.example.weatheraggregator.dto.request.ForecastRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static com.example.weatheraggregator.api.service.ForecastUtil.convertMeasurementUnits;
import static com.example.weatheraggregator.api.service.ForecastUtil.verifyDates;

@Service
public class ForecastService {
    private final ForecastTypeRepository forecastTypeRepository;
    private final ForecastRepository forecastRepository;
    private final CityRepository cityRepository;
    private final ProviderRepository providerRepository;
    private final AggregatorAPI aggregatorAPI;

    public ForecastService(ForecastTypeRepository forecastTypeRepository,
                           ForecastRepository forecastRepository,
                           CityRepository cityRepository,
                           ProviderRepository providerRepository, AggregatorAPI aggregatorAPI) {
        this.forecastTypeRepository = forecastTypeRepository;
        this.forecastRepository = forecastRepository;
        this.cityRepository = cityRepository;
        this.providerRepository = providerRepository;
        this.aggregatorAPI = aggregatorAPI;
    }

    public ForecastPageDTO getForecastsFromProvider(ForecastRequest request, String providerCredential,
                                                    PageRequest pageRequest) {
        verifyDates(request.getStartDate(), request.getEndDate());

        ForecastType type = forecastTypeRepository.findByType(request.getType());
        Provider provider = providerRepository.findByCredential(providerCredential).orElseThrow(() ->
                new IllegalArgumentException(String.format("Couldn't find provider with credential: %s",
                        providerCredential)));
        if(!provider.isActive()) {
            throw new IllegalArgumentException(String.format("provider with credential: %s is currently inactive",
                    providerCredential));
        }
        City city = cityRepository.findByLatAndLng(request.getLat(), request.getLng()).orElseThrow(() ->
                new IllegalArgumentException(String.format("Couldn't find city with coordinates: %f, %f",
                        request.getLat(), request.getLng())));

        Page<Forecast> forecasts = convertMeasurementUnits(forecastRepository.findForCityAndProvider(city,
                provider.getCredential(), type, request.getStartDate().atStartOfDay(),
                request.getEndDate().atStartOfDay(), true, pageRequest), request.getUnit());

        return new ForecastPageDTO(forecasts.map(ForecastDTO::new), type.getType(), new CityDTO(city),
                provider.getCredential());
    }

    public ForecastPageDTO getForecastsAggregated(ForecastRequest request, PageRequest pageRequest) {
        return getForecastsFromProvider(request, aggregatorAPI.getCredential(), pageRequest);
    }
}
