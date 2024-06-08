package com.example.weatheraggregator.api.persistence.repository;

import com.example.weatheraggregator.api.persistence.entity.HistoricalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface HistoricalDataRepository extends JpaRepository<HistoricalData, Long> {
    @Query("select max(f.timeStamp) from HistoricalData f")
    Optional<LocalDateTime> getMaxTimestamp();

}
