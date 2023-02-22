package com.example.weatheraggregator.api.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String address;
    private LocalDate dateAdded;

    public Client(String name, String address, LocalDate dateAdded) {
        this.name = name;
        this.address = address;
        this.dateAdded = dateAdded;
    }
}
