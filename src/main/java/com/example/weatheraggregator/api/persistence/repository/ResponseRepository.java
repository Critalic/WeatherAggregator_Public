package com.example.weatheraggregator.api.persistence.repository;

import com.example.weatheraggregator.api.persistence.entity.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
    Page<Response> findByTimeStampOrderByIdAsc(LocalDateTime timeStamp, Pageable pageable);
}