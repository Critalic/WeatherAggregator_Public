package com.example.weatheraggregator.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotEmpty(message = "Name can't be empty")
    @Size(min = 4, max=64, message = "Username must be between 4 and 100 characters long")
    private String username;
    @NotEmpty(message = "Password can't be empty")
    @Size(min = 10, max=64, message = "Password must be at least 8 characters long")
    private String password;
}