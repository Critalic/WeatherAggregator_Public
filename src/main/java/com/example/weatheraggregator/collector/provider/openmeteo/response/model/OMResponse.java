package com.example.weatheraggregator.collector.provider.openmeteo.response.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OMResponse {
    @NotEmpty
    private String latitude;
    @NotEmpty
    private String longitude;

}
