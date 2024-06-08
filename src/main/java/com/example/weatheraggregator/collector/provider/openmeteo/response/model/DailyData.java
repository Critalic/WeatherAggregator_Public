package com.example.weatheraggregator.collector.provider.openmeteo.response.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DailyData {
    private List<String> time;

    @SerializedName(value = "weather_code")
    private List<Integer> weatherCode;

    @SerializedName(value = "temperature_2m_max")
    private List<Double> temperatureMax;

    @SerializedName(value = "temperature_2m_min")
    private List<Double> temperatureMin;

    @SerializedName(value = "wind_speed_10m_max")
    private List<Double> windSpeedMax;

    @SerializedName(value = "wind_direction_10m_dominant")
    private List<Double> windDirection;
}
