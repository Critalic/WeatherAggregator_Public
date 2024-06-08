package com.example.weatheraggregator.api.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class HistoricalData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Double windSpeed;
    private Double windDirection;
    private Double temperature;
    private Double pressure;
    private Double humidity;
    private String conditions;
    private LocalDateTime time;
    private LocalDateTime timeStamp;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "forecast_type_id")
    private ForecastType forecastType;

    public HistoricalData(Double windSpeed, Double windDirection, Double temperature, Double pressure, Double humidity,
                          LocalDateTime time, String conditions, City city, ForecastType forecastType,
                          LocalDateTime timeStamp) {
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.time = time;
        this.city = city;
        this.conditions = conditions;
        this.forecastType = forecastType;
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoricalData forecast = (HistoricalData) o;
        return Objects.equals(time, forecast.time) && Objects.equals(city, forecast.city) && Objects.equals(forecastType, forecast.forecastType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, timeStamp, city, forecastType);
    }
}
