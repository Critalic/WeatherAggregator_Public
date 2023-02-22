package com.example.weatheraggregator;

import com.example.weatheraggregator.api.persistence.repository.ResponseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
class WeatherAggregatorApplicationTests {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ResponseRepository responseRepository;

    @Test
    @Transactional
    void contextLoads() {
//        entityManager.persist(new Response("{}", LocalDateTime.now(), new City(), new ForecastType(), new Provider()));

//        System.out.println(responseRepository.findByTimeStampIsBefore(LocalDateTime.now()));
    }

}
