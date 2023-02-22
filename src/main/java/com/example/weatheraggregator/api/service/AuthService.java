package com.example.weatheraggregator.api.service;

import com.example.weatheraggregator.api.persistence.entity.Client;
import com.example.weatheraggregator.api.persistence.entity.Customer;
import com.example.weatheraggregator.api.persistence.entity.RefreshToken;
import com.example.weatheraggregator.api.persistence.entity.Role;
import com.example.weatheraggregator.api.persistence.repository.ClientRepository;
import com.example.weatheraggregator.api.persistence.repository.CustomerRepository;
import com.example.weatheraggregator.api.persistence.repository.RefreshTokenRepository;
import com.example.weatheraggregator.api.persistence.repository.RoleRepository;
import com.example.weatheraggregator.dto.auth.CustomerDetailsDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsManager {
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ClientRepository clientRepository;

    public AuthService(RoleRepository roleRepository,
                       CustomerRepository customerRepository,
                       PasswordEncoder passwordEncoder,
                       RefreshTokenRepository refreshTokenRepository,
                       ClientRepository clientRepository) {
        this.roleRepository = roleRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public UUID createUserAndToken(UserDetails user) {
        ((CustomerDetailsDTO) user).setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepository.findByName(((CustomerDetailsDTO) user).getRole())
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Couldn't find role with name: %s", ((CustomerDetailsDTO) user).getRole())));
        Client client = clientRepository.findByAddress(((CustomerDetailsDTO) user).getClientAddress())
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Couldn't find client with address: %s", ((CustomerDetailsDTO) user).getClientAddress())));

        if(userExists(user.getUsername())) {
            throw new IllegalArgumentException(String.format("Customer with name %s already exists",
                    user.getUsername()));
        }

        Customer customer = new Customer(user.getUsername(), user.getPassword(), LocalDate.now(), role, client);
        customerRepository.save(customer);

        UUID refreshTokenCredential = UUID.randomUUID();
        RefreshToken refreshToken = new RefreshToken(refreshTokenCredential, customer);
        refreshTokenRepository.save(refreshToken);

        return refreshTokenCredential;
    }

    @Override
    public void createUser(UserDetails user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateUser(UserDetails user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteUser(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean userExists(String username) {
        return customerRepository.existsByName(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new CustomerDetailsDTO(customerRepository.findByName(username).orElseThrow(() ->
                new IllegalArgumentException(String.format("Couldn't find user for username: %s", username))));
    }
}
