package com.example.weatheraggregator.collector.provider.tomorrowio.response.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class Interval {
    @NotEmpty
    private String startTime;
    @Valid
    private Data values;
}
