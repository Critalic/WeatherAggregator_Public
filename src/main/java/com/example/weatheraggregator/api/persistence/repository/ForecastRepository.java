package com.example.weatheraggregator.api.persistence.repository;

import com.example.weatheraggregator.api.persistence.entity.City;
import com.example.weatheraggregator.api.persistence.entity.Forecast;
import com.example.weatheraggregator.api.persistence.entity.ForecastType;
import com.example.weatheraggregator.api.persistence.entity.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast, Long> {
    Optional<Forecast> findFirstByOrderByTimeStampDesc();

    @Transactional
    @Modifying
    @Query("delete from Forecast f " +
            "where f.timeStamp = ?1 and f.time = ?2 and f.city = ?3 and f.provider = ?4 and f.forecastType = ?5")
    int deleteInstance(LocalDateTime timeStamp, LocalDateTime time, City city, Provider provider,
                       ForecastType forecastType);

    @Query("select f from Forecast f where f.timeStamp = ?1 and f.city = ?2 and f.provider = ?3 and f.forecastType = ?4")
    List<Forecast> find(LocalDateTime timeStamp, City city, Provider provider, ForecastType forecastType);

    @Query("select f from Forecast f " +
            "where f.city = ?1 and f.forecastType = ?2 and f.time between ?3 and ?4 and f.provider.isActive = ?5 " +
            "order by f.time")
    Page<Forecast> findForCity(City city, ForecastType forecastType, LocalDateTime timeStart, LocalDateTime timeEnd,
                               Boolean providerStatus, Pageable pageable);
    
    @Query("select f from Forecast f " +
            "where f.city = ?1 and f.provider.credential = ?2 and f.forecastType = ?3 and f.time between ?4 and ?5 " +
            "and f.provider.isActive = ?6 order by f.time")
    Page<Forecast> findForCityAndProvider(City city, String credential, ForecastType forecastType,
                                          LocalDateTime timeStart, LocalDateTime timeEnd, Boolean providerStatus,
                                          Pageable pageable);

    @Query("select f from Forecast f " +
            "where f.forecastType = ?1 and f.provider.credential = ?2 and f.time between ?3 and ?4 " +
            "order by f.city.name")
    Page<Forecast> findForProvider(ForecastType forecastType, String credential, LocalDateTime timeStart, LocalDateTime timeEnd, Pageable pageable);

}