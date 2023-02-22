package com.example.weatheraggregator.dto.business;

import com.example.weatheraggregator.api.persistence.entity.Forecast;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForecastDTO implements Serializable {
    private Double windSpeed;
    private Double windDirection;
    private Double temperature;
    private Double pressure;
    private Double humidity;
    private LocalDateTime time;
    private String conditions;

    public ForecastDTO(Forecast forecast) {
        this.windSpeed = forecast.getWindSpeed();
        this.windDirection = forecast.getWindDirection();
        this.temperature = forecast.getTemperature();
        this.pressure = forecast.getPressure();
        this.humidity = forecast.getHumidity();
        this.time = forecast.getTime();
        this.conditions = forecast.getConditions();
    }

    public ForecastDTO(Double windSpeed, Double windDirection, Double temperature, Double pressure, Double humidity, String conditions) {
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.conditions = conditions;
    }

    @Override
    public String toString() {
        return "ForecastDTO{" +
                "windSpeed=" + windSpeed +
                ", windDirection=" + windDirection +
                ", temperature=" + temperature +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                ", time=" + time +
                ", conditions='" + conditions + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForecastDTO that = (ForecastDTO) o;
        return Objects.equals(windSpeed, that.windSpeed) && Objects.equals(windDirection, that.windDirection) && Objects.equals(temperature, that.temperature) && Objects.equals(pressure, that.pressure) && Objects.equals(humidity, that.humidity) && Objects.equals(time, that.time) && Objects.equals(conditions, that.conditions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(windSpeed, windDirection, temperature, pressure, humidity, time, conditions);
    }
}
