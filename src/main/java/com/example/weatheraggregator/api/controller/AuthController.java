package com.example.weatheraggregator.api.controller;

import com.example.weatheraggregator.api.security.service.TokenGenerator;
import com.example.weatheraggregator.api.service.AuthService;
import com.example.weatheraggregator.api.service.RefreshTokenService;
import com.example.weatheraggregator.dto.auth.CustomerDetailsDTO;
import com.example.weatheraggregator.dto.auth.LoginDTO;
import com.example.weatheraggregator.dto.auth.SignUpDTO;
import com.example.weatheraggregator.dto.auth.TokenDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final TokenGenerator tokenGenerator;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final AuthService authService;
    private final JwtDecoder refreshTokenDecoder;
    private final RefreshTokenService refreshTokenService;

    public AuthController(JwtAuthenticationProvider jwtAuthenticationProvider,
                          TokenGenerator tokenGenerator,
                          DaoAuthenticationProvider daoAuthenticationProvider,
                          AuthService authService,
                          @Qualifier("refreshTokenDecoder") JwtDecoder refreshTokenDecoder,
                          RefreshTokenService refreshTokenService) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.tokenGenerator = tokenGenerator;
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.authService = authService;
        this.refreshTokenDecoder = refreshTokenDecoder;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenDTO> register(@Valid @RequestBody SignUpDTO signUpDTO) {
        CustomerDetailsDTO customer = signUpDTO.toDetailsDTO();
        UUID refreshTokenCredential = authService.createUserAndToken(customer);

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(customer,
                customer.getPassword(), customer.getAuthorities());

        return ResponseEntity.ok(tokenGenerator.createToken(authentication, refreshTokenCredential.toString()));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        Authentication authentication = daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken
                .unauthenticated(loginDTO.getUsername(), loginDTO.getPassword()));

        UUID tokenCredential = refreshTokenService.getTokenCredentialForCustomer(loginDTO.getUsername());

        return ResponseEntity.ok(tokenGenerator.createToken(authentication, tokenCredential.toString()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenDTO> refresh(@Valid @RequestBody TokenDTO tokenDTO) {
        Authentication authentication = jwtAuthenticationProvider
                .authenticate(new BearerTokenAuthenticationToken(tokenDTO.getRefreshToken()));

        Jwt jwt = refreshTokenDecoder.decode(tokenDTO.getRefreshToken());
        UUID tokenCredential = refreshTokenService.refreshTokenByCredential(jwt.getClaimAsString("tokenId"));

        return ResponseEntity.ok(tokenGenerator.createToken(authentication, tokenCredential.toString()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody TokenDTO tokenDTO) {
        jwtAuthenticationProvider.authenticate(new BearerTokenAuthenticationToken(tokenDTO.getRefreshToken()));

        Jwt jwt = refreshTokenDecoder.decode(tokenDTO.getRefreshToken());
        refreshTokenService.deleteToken(jwt.getClaimAsString("tokenId"));


        return ResponseEntity.ok().build();
    }
}
