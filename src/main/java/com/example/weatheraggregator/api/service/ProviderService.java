package com.example.weatheraggregator.api.service;

import com.example.weatheraggregator.api.persistence.entity.Provider;
import com.example.weatheraggregator.api.persistence.repository.ProviderRepository;
import com.example.weatheraggregator.dto.business.ProviderDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProviderService {
    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public void setStatusForProvider(String providerCredential, Boolean status) {
        Provider provider = providerRepository.findByCredential(providerCredential).orElseThrow(() ->
                new IllegalArgumentException(String.format("Couldn't find report for the credential specified: %s",
                        providerCredential)));
        provider.setActive(status);
        providerRepository.save(provider);
    }

    public List<ProviderDTO> getProvidersWithStatus(Boolean status) {
        return providerRepository.findByStatus(status).stream().map(ProviderDTO::new).collect(Collectors.toList());
    }

    public List<ProviderDTO> getProviders() {
        return providerRepository.findAll().stream().map(ProviderDTO::new).collect(Collectors.toList());
    }
}
