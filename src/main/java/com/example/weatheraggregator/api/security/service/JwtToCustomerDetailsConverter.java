package com.example.weatheraggregator.api.security.service;

import com.example.weatheraggregator.dto.auth.CustomerDetailsDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class JwtToCustomerDetailsConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {
    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        CustomerDetailsDTO customer = new CustomerDetailsDTO();
        customer.setName(jwt.getSubject());
        return new UsernamePasswordAuthenticationToken(customer, jwt,
                Collections.singletonList(new SimpleGrantedAuthority((String) jwt.getClaims().get("role"))));
    }
}
