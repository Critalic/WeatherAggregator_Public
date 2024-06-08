package com.example.weatheraggregator.collector.provider.openmeteo.response.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OMDailyResponse extends OMResponse {
    private DailyData daily;
}
