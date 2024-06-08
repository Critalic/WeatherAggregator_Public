package com.example.weatheraggregator.api.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private UUID credential;

    @ManyToOne()
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public RefreshToken(UUID credential, Customer customer) {
        this.credential = credential;
        this.customer = customer;
    }
}
