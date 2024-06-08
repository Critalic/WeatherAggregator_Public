package com.example.weatheraggregator.collector.provider.openmeteo.response.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class HourlyData {
    private List<String> time;

    @SerializedName(value = "temperature_2m")
    private List<Double> temperature;

    @SerializedName(value = "weather_code")
    private List<Integer> weatherCode;

    @SerializedName(value = "relative_humidity_2m")
    private List<Double> relativeHumidity;

    @SerializedName(value = "surface_pressure")
    private List<Double> surfacePressure;

    @SerializedName(value = "wind_speed_10m")
    private List<Double> windSpeed;

    @SerializedName(value = "wind_direction_10m")
    private List<Double> windDirection;
}
