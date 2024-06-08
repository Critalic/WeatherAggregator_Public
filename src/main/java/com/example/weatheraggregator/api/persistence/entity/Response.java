package com.example.weatheraggregator.api.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Response {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(columnDefinition="mediumtext")
    private String data;
    private LocalDateTime timeStamp;
    private boolean isUpdated;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "forecast_type_id")
    private ForecastType forecastType;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "provider_id")
    private Provider provider;


    public Response(String data, LocalDateTime timeStamp, City city, ForecastType forecastType, Provider provider) {
        this.data = data;
        this.timeStamp = timeStamp;
        this.city = city;
        this.forecastType = forecastType;
        this.provider = provider;
    }
}
