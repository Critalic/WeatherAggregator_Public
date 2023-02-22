package com.example.weatheraggregator.collector.provider.tomorrowio;

import com.example.weatheraggregator.aspect.LogOnException;
import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.collector.model.TimeStep;
import com.example.weatheraggregator.collector.provider.WeatherDataProvider;
import com.example.weatheraggregator.collector.provider.tomorrowio.response.TIOResponseParser;
import com.example.weatheraggregator.dto.business.ForecastDTO;
import com.example.weatheraggregator.dto.business.ResponseDTO;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import java.util.*;

import static java.util.Collections.singletonList;

public class TomorrowIO implements WeatherDataProvider {
    private final String apiKey;
    private final String credential;

    private final WebClient webClient;
    private final List<String> fields = Arrays.asList("temperature", "pressureSurfaceLevel",
            "weatherCode", "windDirection", "humidity", "windSpeed");
    private final TIOResponseParser responseParser;

    public TomorrowIO(Builder webClientBuilder, String apiKey, String credential) {
        this.apiKey = apiKey;
        this.credential = credential;
        this.webClient = webClientBuilder
                .baseUrl("https://api.tomorrow.io/v4")
                .defaultHeader("accept", "application/json")
                .defaultHeader("Accept-Encoding", "gzip")
                .build();
        responseParser = new TIOResponseParser();
    }

    @Override
    @LogOnException
    public ResponseDTO makeRequest(RequestAttributes requestAttributes) {
        Map<String, List<String>> params = new HashMap<>();
        params.put("apikey", singletonList(apiKey));
        params.put("startTime", singletonList(requestAttributes.getStartDate().plusDays(1).toString()));
        params.put("endTime", singletonList(requestAttributes.getEndDate().plusDays(1).toString()));
        params.put("timesteps", singletonList(parseTimestep(requestAttributes.getTimeStep())));
        params.put("units", singletonList("metric"));
        params.put("location", singletonList(requestAttributes.getCity().getLat() + "," +
                requestAttributes.getCity().getLng()));
        params.put("fields", fields);

        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/timelines")
                        .queryParams(new MultiValueMapAdapter<>(params))
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return new ResponseDTO(response, getCredential(), requestAttributes.getTimeStep());
    }

    @Override
    public String getCredential() {
        return credential;
    }

    @Override
    @LogOnException
    public Collection<ForecastDTO> parseResponse(ResponseDTO responseDTO) {
        return responseParser.parseForecast(responseDTO.getData(), responseDTO.getTimeStep());
    }

    private String parseTimestep(TimeStep timeStep) {
        return switch (timeStep) {
            case HOUR -> "1h";
            case DAY -> "1d";
            default -> throw new IllegalArgumentException("Unsupported timeStep format provided: " + timeStep);
        };
    }
}