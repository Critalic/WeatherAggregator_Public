package com.example.weatheraggregator.collector.provider.tomorrowio.response.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResultTIO {
    @NotEmpty
    private String startTime;
    @NotEmpty
    private String endTime;
    @NotEmpty
    private String timestep;
    @NotEmpty
    private List<@Valid Interval> intervals;
}