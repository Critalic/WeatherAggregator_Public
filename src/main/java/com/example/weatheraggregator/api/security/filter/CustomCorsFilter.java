package com.example.weatheraggregator.api.security.filter;

import com.example.weatheraggregator.api.service.ClientService;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.*;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class CustomCorsFilter extends OncePerRequestFilter {
    private final CorsProcessor processor = new DefaultCorsProcessor();
    private final ClientService clientService;

    public CustomCorsFilter(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        List<String> origins = clientService.getClientServerAddresses();
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("OPTIONS", "HEAD", "GET", "POST"));
        config.addAllowedHeader("*");
        source.registerCorsConfiguration("/**", config);
        CorsConfiguration corsConfiguration = source.getCorsConfiguration(request);
        boolean isValid = this.processor.processRequest(corsConfiguration, request, response);
        if (!isValid || CorsUtils.isPreFlightRequest(request)) {
            return;
        }
        filterChain.doFilter(request, response);
    }
}
