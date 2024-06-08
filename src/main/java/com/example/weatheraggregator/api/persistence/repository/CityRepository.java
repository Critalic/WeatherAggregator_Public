package com.example.weatheraggregator.api.persistence.repository;

import com.example.weatheraggregator.api.persistence.entity.City;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.AvailableHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    @Query("select c from City c where c.name = ?1 and c.lat = ?2 and c.lng = ?3")
    City findByNameAndCoordinates(String name, double lat, double lng);

    Optional<City> findByLatAndLng(double lat, double lng);

    List<City> findByName(String name);

    Page<City> getByOrderByNameAsc(Pageable pageable);

    @Query("select c from City c order by c.name")
    @QueryHints(
            @QueryHint(name = AvailableHints.HINT_FETCH_SIZE, value = "50")
    )
    Stream<City> streamAllOrderByNameAsc();


}