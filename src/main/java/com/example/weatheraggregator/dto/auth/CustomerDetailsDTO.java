package com.example.weatheraggregator.dto.auth;

import com.example.weatheraggregator.api.persistence.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
@NoArgsConstructor
@Setter
public class CustomerDetailsDTO implements UserDetails {
    private String name;
    private String password;
    private String role;
    private String clientAddress;

    public CustomerDetailsDTO(Customer customer) {
        this.name = customer.getName();
        this.password= customer.getPassword();
        this.role = customer.getRole().getName();
        this.clientAddress = customer.getClient().getAddress();
    }

    public String getRole() {
        return role;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
