package com.example.weatheraggregator.collector.model;

import com.example.weatheraggregator.dto.business.CityDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestAttributes implements Cloneable {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private TimeStep timeStep;
    private CityDTO city;
    private LocalDateTime timeStamp;

    public RequestAttributes(LocalDateTime startDate, LocalDateTime endDate, TimeStep timeStep, CityDTO city) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeStep = timeStep;
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestAttributes that = (RequestAttributes) o;
        return Objects.equals(startDate.truncatedTo(ChronoUnit.MINUTES), that.startDate.truncatedTo(ChronoUnit.MINUTES))
                && Objects.equals(endDate.truncatedTo(ChronoUnit.MINUTES), that.endDate.truncatedTo(ChronoUnit.MINUTES))
                && timeStep == that.timeStep && Objects.equals(city, that.city)
                && Objects.equals(timeStamp, that.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, timeStep, city, timeStamp);
    }

    @Override
    public String toString() {
        return "RequestAttributes{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", timeStep=" + timeStep +
                ", city=" + city +
                '}';
    }
}
