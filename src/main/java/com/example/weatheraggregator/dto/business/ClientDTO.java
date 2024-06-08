package com.example.weatheraggregator.dto.business;

import com.example.weatheraggregator.api.persistence.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    @NotEmpty(message = "Client address can't be empty")
    @Pattern(regexp = "^https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)$",
            message = "Invalid client address format")
    private String address;
    @NotEmpty(message = "Name can't be empty")
    private String name;

    public Client toClient() {
        return new Client(this.name, this.address, LocalDate.now());
    }
}
