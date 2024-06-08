package com.example.weatheraggregator.dto.business;

import com.example.weatheraggregator.collector.model.TimeStep;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ForecastCollectionDTO {
    private Collection<ForecastDTO> forecastDTOs;
    private TimeStep timeStep;
    private CityDTO cityDTO;
    private String providerCredential;
}
