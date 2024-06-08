package com.example.weatheraggregator.api.service;

import com.example.weatheraggregator.api.persistence.repository.CityRepository;
import com.example.weatheraggregator.dto.business.CityDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityService {
    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<CityDTO> getCityByName(String cityName) {
        return cityRepository.findByName(cityName).stream().map(CityDTO::new).toList();
    }

    public CityDTO getCityByCoordinates(double lat, double lng) {
        return new CityDTO(cityRepository.findByLatAndLng(lat, lng).orElseThrow(() ->
                new IllegalArgumentException(String.format("Couldn't find city with coordinates: %f, %f", lat, lng))));
    }
}
