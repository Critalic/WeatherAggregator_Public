package com.example.weatheraggregator.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignUpDTO {
    @NotEmpty(message = "Name can't be empty")
    @Size(min = 4, max=64, message = "Username must be between 4 and 100 characters long")
    private String name;
    @NotEmpty(message = "Password can't be empty")
    @Size(min = 10, max=64, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$",
            message = "Password must contain at least one small letter, one capital letter and one digit")
    private String password;
    @NotEmpty(message = "Client address can't be empty")
    @Pattern(regexp = "^https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)$",
            message = "Invalid client address format")
    private String address;

    public CustomerDetailsDTO toDetailsDTO() {
        return new CustomerDetailsDTO(this.getName(), this.getPassword(), "User", address);
    }
}