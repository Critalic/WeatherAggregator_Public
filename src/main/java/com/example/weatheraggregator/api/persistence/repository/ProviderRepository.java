package com.example.weatheraggregator.api.persistence.repository;

import com.example.weatheraggregator.api.persistence.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    @Query("select p from Provider p where p.isActive = ?1")
    List<Provider> findByStatus(boolean isActive);

    @Query("select p from Provider p where p.credential = ?1")
    Optional<Provider> findByCredential(String credential);
}