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
    Optional<Forecast> findFirstByProviderOrderByTimeStampDesc(Provider provider);

    Optional<Forecast> findFirstByProvider_CredentialOrderByTimeStampDesc(String credential);

    @Transactional
    @Modifying
    @Query("delete from Forecast f where f.timeStamp = ?1 and f.provider = ?2 and f.city = ?3 and f.forecastType = ?4")
    int delete(LocalDateTime timeStamp, Provider provider, City city, ForecastType forecastType);

    @Query("select f from Forecast f where f.timeStamp = ?1 and f.city = ?2 and f.provider.credential = ?3")
    List<Forecast> findByTimestampAndCityAndProvider(LocalDateTime timeStamp, City city, String providerCredential);

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

    @Query("select f.time from Forecast f where f.timeStamp > ?1 group by f.time")
    List<LocalDateTime> getForecastedDaysAfter(LocalDateTime timeStamp);

    @Query("select f from Forecast f where f.city = ?1")
    List<Forecast> findByCity(City city);

    @Query("select f from Forecast f where f.city = ?1 and f.provider.credential = ?2 and f.time >= ?3 and f.time <= ?4 and f.timeStamp != ?5")
    List<Forecast> findByCityAndProviderInRangeExcluding(City city, String providerCredential, LocalDateTime biggerThan, LocalDateTime lessThan, LocalDateTime timeStampExclude);


}