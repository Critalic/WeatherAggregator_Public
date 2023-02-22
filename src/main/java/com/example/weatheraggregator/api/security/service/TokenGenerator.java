package com.example.weatheraggregator.api.security.service;

import com.example.weatheraggregator.dto.auth.CustomerDetailsDTO;
import com.example.weatheraggregator.dto.auth.TokenDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class TokenGenerator {
    @Value("${issuer.name}")
    private String issuerName;

    @Value("${access-token.lifetime}")
    private String accessTokenLifeTime;

    @Value("${refresh-token.lifetime}")
    private String refreshTokenLifeTime;

    private final JwtEncoder accessTokenEncoder;
    private final JwtEncoder refreshTokenEncoder;

    public TokenGenerator(@Qualifier("accessTokenEncoder") JwtEncoder accessTokenEncoder,
                          @Qualifier("refreshTokenEncoder") JwtEncoder refreshTokenEncoder) {
        this.accessTokenEncoder = accessTokenEncoder;
        this.refreshTokenEncoder = refreshTokenEncoder;
    }

    public TokenDTO createToken(Authentication authentication, String refreshTokenClaim) {
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setAccessToken(createAccessToken(authentication));

        String refreshToken;
        if (authentication.getCredentials().getClass().equals(Jwt.class)) {
            Jwt jwt = (Jwt) authentication.getCredentials();
            Instant now = Instant.now();
            Instant expiresAt = jwt.getExpiresAt();
            Duration duration = Duration.between(now, expiresAt);
            long daysUntilExpired = duration.toDays();
            if (daysUntilExpired < 7) {
                refreshToken = createRefreshToken(authentication, refreshTokenClaim);
            } else {
                refreshToken = jwt.getTokenValue();
            }
        } else {
            refreshToken = createRefreshToken(authentication, refreshTokenClaim);
        }
        tokenDTO.setRefreshToken(refreshToken);

        return tokenDTO;
    }

    private String createAccessToken(Authentication authentication) {
        CustomerDetailsDTO user = (CustomerDetailsDTO) authentication.getPrincipal();
        Instant now = Instant.now();

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer(issuerName)
                .issuedAt(now)
                .expiresAt(now.plus(Duration.parse(accessTokenLifeTime)))
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .build();

        return accessTokenEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    private String createRefreshToken(Authentication authentication, String refreshTokenClaim) {
        CustomerDetailsDTO user = (CustomerDetailsDTO) authentication.getPrincipal();
        Instant now = Instant.now();

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer(issuerName)
                .issuedAt(now)
                .expiresAt(now.plus(Duration.parse(refreshTokenLifeTime)))
                .subject(user.getUsername())
                .claim("tokenId", refreshTokenClaim)
                .claim("role", user.getRole())
                .build();

        return refreshTokenEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }
}
