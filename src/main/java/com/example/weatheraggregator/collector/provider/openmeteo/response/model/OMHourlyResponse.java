package com.example.weatheraggregator.collector.provider.openmeteo.response.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OMHourlyResponse extends OMResponse {
    private HourlyData hourly;
}
