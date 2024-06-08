package com.example.weatheraggregator.collector.provider.aggregator;

import com.example.weatheraggregator.collector.provider.aggregator.response.model.TokenServiceResponse;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class TokenService {
    @Value("${token.service.request.client_id}")
    private String clientId;
    @Value("${token.service.request.client_secret}")
    private String clientSecret;
    @Value("${token.service.request.audience}")
    private String audience;
    @Value("${token.service.request.grant_type}")
    private String grantType;

    private final WebClient webClient;

    public TokenService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://dev-k8oxeailjr0d1qyq.us.auth0.com")
                .defaultHeader("accept", "application/json")
                .defaultHeader("Accept-Encoding", "gzip")
                .build();
    }

    public String getAccessToken() {
        String response = webClient.post()
                .uri("/oauth/token")
                .body(BodyInserters.fromValue(Map.of(
                        "client_id", clientId,
                        "client_secret", clientSecret,
                        "audience", audience,
                        "grant_type", grantType
                )))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return new Gson().fromJson(response, TokenServiceResponse.class).getAccessToken();
    }
}
