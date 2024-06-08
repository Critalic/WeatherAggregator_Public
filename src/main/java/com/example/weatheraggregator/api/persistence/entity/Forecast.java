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
public class Forecast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
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

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "provider_id")
    private Provider provider;

    public Forecast(Double windSpeed, Double windDirection, Double temperature, Double pressure, Double humidity,
                    LocalDateTime time, String conditions, City city, ForecastType forecastType,
                    Provider provider, LocalDateTime timeStamp) {
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.time = time;
        this.city = city;
        this.conditions = conditions;
        this.forecastType = forecastType;
        this.provider = provider;
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Forecast forecast = (Forecast) o;
        return Objects.equals(getTime(), forecast.getTime()) && Objects.equals(getCity().getId(), forecast.getCity().getId())
                && Objects.equals(getForecastType().getId(), forecast.getForecastType().getId())
                && Objects.equals(getProvider().getId(), forecast.getProvider().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTime(), getCity(), getForecastType(), getProvider());
    }
}
