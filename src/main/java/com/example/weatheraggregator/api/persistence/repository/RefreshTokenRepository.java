package com.example.weatheraggregator.api.persistence.repository;

import com.example.weatheraggregator.api.persistence.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Query("select (count(r) > 0) from RefreshToken r where r.customer.name = ?1")
    boolean existsForCustomerName(String name);

    @Query("select r from RefreshToken r where r.customer.name = ?1")
    Optional<RefreshToken> findByCustomerName(String name);

    Optional<RefreshToken> findByCredential(UUID credential);

    long deleteByCredential(UUID credential);

}