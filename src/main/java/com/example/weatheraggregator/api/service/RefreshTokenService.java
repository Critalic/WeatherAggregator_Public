package com.example.weatheraggregator.api.service;

import com.example.weatheraggregator.api.persistence.entity.Customer;
import com.example.weatheraggregator.api.persistence.entity.RefreshToken;
import com.example.weatheraggregator.api.persistence.repository.CustomerRepository;
import com.example.weatheraggregator.api.persistence.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final CustomerRepository customerRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(CustomerRepository customerRepository,
                               RefreshTokenRepository refreshTokenRepository) {
        this.customerRepository = customerRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public UUID getTokenCredentialForCustomer(String customerName) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByCustomerName(customerName);
        if(refreshToken.isPresent()) {
            return refreshToken.get().getCredential();
        }

        RefreshToken newToken = new RefreshToken(UUID.randomUUID(), customerRepository.findByName(customerName)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Couldn't find customer with given name: %s", customerName))));
        return refreshTokenRepository.save(newToken).getCredential();
    }

    @Transactional
    public UUID refreshTokenByCredential(String tokenCredential) {
        UUID refreshTokenUUID = UUID.fromString(tokenCredential);
        Customer customer = customerRepository.findByRefreshToken(refreshTokenUUID).orElseThrow(() ->
                new IllegalArgumentException(String.format("Found no token with given credential: %s",
                        tokenCredential)));

        refreshTokenRepository.deleteByCredential(refreshTokenUUID);
        return refreshTokenRepository.save(new RefreshToken(UUID.randomUUID(), customer)).getCredential();
    }

    public void deleteToken(String tokenCredential) {
        refreshTokenRepository.deleteByCredential(UUID.fromString(tokenCredential));
    }
}
