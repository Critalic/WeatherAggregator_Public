package com.example.weatheraggregator.api.service;

import com.example.weatheraggregator.api.persistence.entity.Client;
import com.example.weatheraggregator.api.persistence.repository.ClientRepository;
import com.example.weatheraggregator.dto.business.ClientDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void addClient(ClientDTO clientDTO) {
        if(clientRepository.existsByAddress(clientDTO.getAddress())) {
            throw new IllegalArgumentException(String.format("Client with address %s already exists",
                    clientDTO.getAddress()));
        }
        clientRepository.save(clientDTO.toClient());
    }

    @Transactional
    public void deleteClient(ClientDTO clientDTO) {
        clientRepository.delete(clientDTO.toClient());
    }

    @Transactional
    public void deleteClientByAddress(String clientAddress) {
        clientRepository.deleteByAddress(clientAddress);
    }

    public void editClient(String clientAddress, ClientDTO clientDTO) {
        Client client = clientRepository.findByAddress(clientAddress).orElseThrow(() ->
                new IllegalArgumentException(String.format("Found no client with given address: %s", clientAddress)));
        Client updated = clientDTO.toClient();
        updated.setId(client.getId());
        clientRepository.save(updated);
    }

    public List<String> getClientServerAddresses() {
        return clientRepository.findAll().stream().map(Client::getName)
                .toList();
    }
}
