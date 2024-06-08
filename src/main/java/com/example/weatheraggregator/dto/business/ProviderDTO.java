package com.example.weatheraggregator.dto.business;

import com.example.weatheraggregator.api.persistence.entity.Provider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProviderDTO {
    private String credential;
    private boolean isActive;

    public ProviderDTO(String credential, boolean isActive) {
        this.credential = credential;
        this.isActive = isActive;
    }

    public ProviderDTO(Provider provider) {
        this.credential = provider.getCredential();
        this.isActive = provider.isActive();
    }
}
