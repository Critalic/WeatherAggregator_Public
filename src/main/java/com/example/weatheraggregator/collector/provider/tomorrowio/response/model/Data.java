package com.example.weatheraggregator.collector.provider.tomorrowio.response.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class Data {
    @NotNull
    private Double temperature;
    @NotNull
    private Double humidity;
    @NotNull
    private Double windSpeed;
    @NotNull
    private Double windDirection;
    @NotEmpty
    private Integer weatherCode;
    @NotNull
    @SerializedName(value = "pressureSurfaceLevel")
    private Double pressure;
}
