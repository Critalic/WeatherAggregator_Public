package com.example.weatheraggregator.api.service;

import com.example.weatheraggregator.api.persistence.entity.ForecastType;
import com.example.weatheraggregator.api.persistence.repository.ForecastTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForecastTypeService {
    private final ForecastTypeRepository forecastTypeRepository;

    public ForecastTypeService(ForecastTypeRepository forecastTypeRepository) {
        this.forecastTypeRepository = forecastTypeRepository;
    }

    public List<String> getForecastTypes() {
        return forecastTypeRepository.findAll().stream().map(ForecastType::getType).toList();
    }
}
