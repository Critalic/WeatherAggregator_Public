package com.example.weatheraggregator.api.controller;

import com.example.weatheraggregator.api.service.ClientService;
import com.example.weatheraggregator.api.service.ProviderService;
import com.example.weatheraggregator.dto.business.ClientDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final ProviderService providerService;
    private final ClientService clientService;

    public AdminController(ProviderService providerService, ClientService clientService) {
        this.providerService = providerService;
        this.clientService = clientService;
    }

    @PatchMapping("/provider")
    public ResponseEntity<String> setStatus(@RequestParam(name = "status") boolean status,
                                            @RequestParam String providerCredential) {
        providerService.setStatusForProvider(providerCredential, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/client")
    public ResponseEntity<String> addClient(@RequestBody @Valid ClientDTO clientDTO) {
        clientService.addClient(clientDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/client")
    public ResponseEntity<String> deleteClientByAddress(@RequestParam(name = "address")
                                                        @NotEmpty(message = "Client address can't be empty")
                                                        @Pattern(regexp = "^https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]" +
                                                                "{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+." +
                                                                "~#?&/=]*)$",
                                                                message = "Invalid client address format")
                                                                String clientAddress) {
        clientService.deleteClientByAddress(clientAddress);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/client")
    public ResponseEntity<String> editClientByAddress(@RequestParam(name = "address")
                                                      @NotEmpty(message = "Client address can't be empty")
                                                      @Pattern(regexp = "^https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]" +
                                                              "{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+." +
                                                              "~#?&/=]*)$",
                                                              message = "Invalid client address format")
                                                              String clientAddress,
                                                      @RequestBody @Valid ClientDTO clientDTO) {
        clientService.editClient(clientAddress, clientDTO);
        return ResponseEntity.ok().build();
    }
}
