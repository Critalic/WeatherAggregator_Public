package com.example.weatheraggregator.dto.business;

import com.example.weatheraggregator.collector.model.TimeStep;
import com.example.weatheraggregator.api.persistence.entity.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO {
    private String data;
    private String providerCredential;
    private TimeStep timeStep;

    public ResponseDTO(Response response) {
        this.data = response.getData();
        this.providerCredential = response.getProvider().getCredential();
        this.timeStep = TimeStep.getByType(response.getForecastType().getType());
    }
}
