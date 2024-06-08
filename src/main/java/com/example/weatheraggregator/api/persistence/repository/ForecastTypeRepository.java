package com.example.weatheraggregator.api.persistence.repository;

import com.example.weatheraggregator.api.persistence.entity.ForecastType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForecastTypeRepository extends JpaRepository<ForecastType, Long> {
    ForecastType findByType(String type);

}