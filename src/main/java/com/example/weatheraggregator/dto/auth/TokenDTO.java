package com.example.weatheraggregator.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {
    @NotEmpty(message = "Access Token can't be empty")
    private String accessToken;
    @NotEmpty(message = "Refresh Token can't be empty")
    private String refreshToken;
}

