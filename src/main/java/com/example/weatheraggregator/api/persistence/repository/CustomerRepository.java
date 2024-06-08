package com.example.weatheraggregator.api.persistence.repository;

import com.example.weatheraggregator.api.persistence.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByName(String name);

    Optional<Customer> findByName(String name);

    @Query("select c from Customer c inner join c.refreshTokens refreshTokens where refreshTokens.credential in ?1")
    Optional<Customer> findByRefreshToken(UUID credentials);
}