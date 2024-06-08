package com.example.weatheraggregator.collector.provider.visualcrossing.response.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class HourData {
    @NotBlank
    @SerializedName(value = "datetime")
    private String time;
    @SerializedName(value = "temp")
    @NotNull
    private Double temperature;
    @NotNull
    private Double humidity;
    @NotNull
    @SerializedName(value = "windspeed")
    private Double windSpeed;
    @NotNull
    @SerializedName(value = "winddir")
    private Double windDirection;
    @NotBlank
    private String conditions;
    @NotNull
    private Double pressure;
}
