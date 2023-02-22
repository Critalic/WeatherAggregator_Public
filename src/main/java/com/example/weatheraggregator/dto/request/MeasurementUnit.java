package com.example.weatheraggregator.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum MeasurementUnit {
    METRIC("METRIC", "°C", "kph"),
    US("US", "°F", "mph"),
    UK("UK", "°C", "mph");

    private final String name;
    private final String temp;
    private final String speed;

    MeasurementUnit(String name, String temp, String speed) {
        this.name = name;
        this.temp = temp;
        this.speed = speed;
    }
}
