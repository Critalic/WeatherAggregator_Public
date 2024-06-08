package com.example.weatheraggregator.dto.business;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@NoArgsConstructor
@Getter
@Setter
public class ForecastPageDTO {
    private Page<ForecastDTO> forecastDTOs;
    private String timeStep;
    private CityDTO cityDTO;
    private String providerCredential;

    public ForecastPageDTO(Page<ForecastDTO> forecastDTOs, String timeStep, CityDTO cityDTO, String providerCredential) {
        this.forecastDTOs = forecastDTOs;
        this.timeStep = timeStep;
        this.cityDTO = cityDTO;
        this.providerCredential = providerCredential;
    }
}
