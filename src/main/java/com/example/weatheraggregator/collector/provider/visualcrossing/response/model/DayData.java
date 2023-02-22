package com.example.weatheraggregator.collector.provider.visualcrossing.response.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DayData {
    @NotBlank
    @SerializedName(value = "datetime")
    private String date;
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
    @SerializedName(value = "hours")
    @NotEmpty
    private List<@Valid HourData> hourData;
}
