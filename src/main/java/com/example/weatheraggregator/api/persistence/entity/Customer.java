package com.example.weatheraggregator.api.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;
    private LocalDate dateAdded;

    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "customer")
    private List<RefreshToken> refreshTokens;

    public Customer(String name, String password, LocalDate dateAdded, Role role, Client client) {
        this.name = name;
        this.password = password;
        this.dateAdded = dateAdded;
        this.role = role;
        this.client = client;
    }
}
