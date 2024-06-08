package com.example.weatheraggregator.dto.business;

import com.example.weatheraggregator.api.persistence.entity.Forecast;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
public class ForecastStatDTO {
    private Double windSpeed;
    private Double windDirection;
    private Double temperature;
    private Double pressure;
    private Double humidity;
    private String time;
    private String conditions;
    private CityDTO city;

    public ForecastStatDTO(Forecast forecast) {
        this.windSpeed = forecast.getWindSpeed();
        this.windDirection = forecast.getWindDirection();
        this.temperature = forecast.getTemperature();
        this.pressure = forecast.getPressure();
        this.humidity = forecast.getHumidity();
        this.time = forecast.getTime().toString();
        this.conditions = forecast.getConditions();
        this.city=new CityDTO(forecast.getCity());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForecastStatDTO that = (ForecastStatDTO) o;
        return Objects.equals(windSpeed, that.windSpeed) && Objects.equals(windDirection, that.windDirection) && Objects.equals(temperature, that.temperature) && Objects.equals(pressure, that.pressure) && Objects.equals(humidity, that.humidity) && Objects.equals(time, that.time) && Objects.equals(conditions, that.conditions) && Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(windSpeed, windDirection, temperature, pressure, humidity, time, conditions, city);
    }
}
