package com.example.weatheraggregator.api.controller;

import com.example.weatheraggregator.api.service.ProviderService;
import com.example.weatheraggregator.dto.business.ProviderDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/provider")
public class ProviderController {
    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @GetMapping("/active")
    public ResponseEntity<Collection<ProviderDTO>> getActiveProviders() {
        return ResponseEntity.ok(providerService.getProvidersWithStatus(true));
    }

    @GetMapping()
    public ResponseEntity<Collection<ProviderDTO>> getProviders() {
        return ResponseEntity.ok(providerService.getProviders());
    }
}
