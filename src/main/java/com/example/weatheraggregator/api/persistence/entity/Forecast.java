package com.example.weatheraggregator.api.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Forecast {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "forecast_sequence")
    @SequenceGenerator(name = "forecast_sequence", sequenceName = "forecast_sequence")
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
        return Objects.equals(time, forecast.time) && Objects.equals(city, forecast.city) && Objects.equals(forecastType, forecast.forecastType) && Objects.equals(provider, forecast.provider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, timeStamp, city, forecastType, provider);
    }
}
