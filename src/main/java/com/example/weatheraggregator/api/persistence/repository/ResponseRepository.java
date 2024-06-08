package com.example.weatheraggregator.api.persistence.repository;

import com.example.weatheraggregator.api.persistence.entity.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
    Page<Response> findByTimeStampOrderByIdAsc(LocalDateTime timeStamp, Pageable pageable);

    @Query("select r from Response r where r.timeStamp = ?1 and r.provider.credential = ?2 order by r.id")
    Page<Response> findByTimeStampAndProviderCredentialOrderByIdAsc(LocalDateTime timeStamp, String credential, Pageable pageable);

}