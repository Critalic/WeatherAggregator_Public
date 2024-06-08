package com.example.weatheraggregator.collector.provider.aggregator;

import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.collector.provider.WeatherForecastDataProvider;
import com.example.weatheraggregator.dto.business.ForecastDTO;
import com.example.weatheraggregator.dto.business.ResponseDTO;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class AggregatorAPI implements WeatherForecastDataProvider {
    private final DateTimeFormatter formatter;
    private final TokenService tokenService;
    private final String credential;
    private final WebClient webClient;

    public AggregatorAPI(String aggregatorDomain, String timeStampFormat, TokenService tokenService,
                         WebClient.Builder webClientBuilder, String credential) {
        this.tokenService = tokenService;
        this.credential = credential;
        this.formatter = DateTimeFormatter.ofPattern(timeStampFormat);
        this.webClient = webClientBuilder
                .baseUrl(aggregatorDomain)
                .defaultHeader("accept", "application/json")
                .build();
    }

    @Override
    public ResponseDTO makeForecastRequest(RequestAttributes requestAttributes) {
        String accessToken = tokenService.getAccessToken();
        Map<String, List<String>> argsMap = new HashMap<>();
        argsMap.put("timestamp", singletonList(requestAttributes.getTimeStamp().format(formatter)));

        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/weather/aggregate")
                        .queryParams(new MultiValueMapAdapter<>(argsMap))
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return new ResponseDTO(response, getCredential(), null);
    }

    @Override
    public String getCredential() {
        return this.credential;
    }

    @Override
    public Collection<ForecastDTO> parseResponse(ResponseDTO responseDTO) {
        return List.of();
    }
}
