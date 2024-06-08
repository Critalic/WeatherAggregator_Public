package com.example.weatheraggregator.dto.business;

import com.example.weatheraggregator.api.persistence.entity.City;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
public class CityDTO {
    private String name;
    private double lat;
    private double lng;
    private String country;

    public CityDTO(String name, double lat, double lng, String country) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.country = country;
    }

    public CityDTO(City city) {
        this.name = city.getName();
        this.lat = city.getLat();
        this.lng = city.getLng();
        this.country = city.getCountry();
    }

    public City toCity() {
        return new City(this.name, this.lat, this.lng);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CityDTO cityDTO = (CityDTO) o;
        return Double.compare(cityDTO.lat, lat) == 0 && Double.compare(cityDTO.lng, lng) == 0 && Objects.equals(name, cityDTO.name) && Objects.equals(country, cityDTO.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lat, lng, country);
    }

    @Override
    public String toString() {
        return "CityDTO{" +
                "name='" + name + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
