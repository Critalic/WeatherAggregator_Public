package com.example.weatheraggregator.collector.provider.openmeteo;

import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.collector.provider.HistoricalWeatherDataProvider;
import com.example.weatheraggregator.collector.provider.WeatherForecastDataProvider;
import com.example.weatheraggregator.collector.provider.openmeteo.response.OMResponseParser;
import com.example.weatheraggregator.dto.business.ForecastDTO;
import com.example.weatheraggregator.dto.business.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.Collections.singletonList;

public class OpenMeteo implements WeatherForecastDataProvider, HistoricalWeatherDataProvider {
    private static final List<String> DAILY_FIELDS = Arrays.asList("weather_code", "temperature_2m_max",
            "temperature_2m_min", "rain_sum", "wind_speed_10m_max", "wind_direction_10m_dominant");
    private static final List<String> HOURLY_FIELDS = Arrays.asList("weather_code", "temperature_2m",
            "relative_humidity_2m", "surface_pressure", "cloud_cover", "wind_speed_10m", "wind_direction_10m");

    private final String credential;
    private final OMResponseParser responseParser;
    private final WebClient webClientForecast;
    private final WebClient webClientHistorical;

    public OpenMeteo(WebClient.Builder webClientBuilder, String credential) {
        this.credential = credential;
        this.responseParser = new OMResponseParser();

        this.webClientForecast = webClientBuilder
                .baseUrl("https://api.open-meteo.com/v1/forecast")
                .defaultHeader("accept", "application/json")
                .defaultHeader("Accept-Encoding", "gzip")
                .build();

        this.webClientHistorical = webClientBuilder
                .baseUrl("https://archive-api.open-meteo.com/v1/archive")
                .defaultHeader("accept", "application/json")
                .defaultHeader("Accept-Encoding", "gzip")
                .build();
    }

    @Override
    public ResponseDTO makeForecastRequest(RequestAttributes requestAttributes) {
        System.out.printf("Requesting %s with city %s and type %s%n", credential, requestAttributes.getCity(),
                requestAttributes.getTimeStep());
        Map<String, List<String>> params = new HashMap<>();
        params.put("latitude", singletonList(String.valueOf(requestAttributes.getCity().getLat())));
        params.put("longitude", singletonList(String.valueOf(requestAttributes.getCity().getLng())));
        params.put("forecast_days", singletonList(parseDaysNum(requestAttributes)));
        parseTimestep(requestAttributes, params);

        pause();
        String response = webClientForecast.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParams(new MultiValueMapAdapter<>(params))
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return new ResponseDTO(response, getCredential(), requestAttributes.getTimeStep());
    }

    @Override
    public ResponseDTO makeHistoricalRequest(RequestAttributes requestAttributes) {
        System.out.printf("Requesting historical data for city %s and type %s%n", requestAttributes.getCity(),
                requestAttributes.getTimeStep());
        Map<String, List<String>> params = new HashMap<>();
        params.put("latitude", singletonList(String.valueOf(requestAttributes.getCity().getLat())));
        params.put("longitude", singletonList(String.valueOf(requestAttributes.getCity().getLng())));
        params.put("start_date", singletonList(requestAttributes.getStartDate().toLocalDate().toString()));
        params.put("end_date", singletonList(requestAttributes.getEndDate().toLocalDate().toString()));
        parseTimestep(requestAttributes, params);

        pause();
        String response = webClientHistorical.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParams(new MultiValueMapAdapter<>(params))
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(2))
                        .filter(e -> e instanceof WebClientResponseException &&
                                ((WebClientResponseException) e).getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS))
                )
                .block();
        return new ResponseDTO(response, getCredential(), requestAttributes.getTimeStep());
    }

    @Override
    public String getCredential() {
        return credential;
    }

    @Override
    public Collection<ForecastDTO> parseResponse(ResponseDTO responseDTO) {
        return responseParser.parseForecast(responseDTO);
    }

    private void parseTimestep(RequestAttributes requestAttributes, Map<String, List<String>> params) {
        switch (requestAttributes.getTimeStep()) {
            case DAY -> params.put("daily", DAILY_FIELDS);
            case HOUR -> params.put("hourly", HOURLY_FIELDS);
            default -> throw new IllegalArgumentException("Unsupported timeStep format provided: " +
                    requestAttributes.getTimeStep());
        }
    }

    private String parseDaysNum(RequestAttributes requestAttributes) {
        if (requestAttributes.getStartDate().toLocalDate().equals(LocalDate.now())) {
            long difference = ChronoUnit.DAYS.between(requestAttributes.getStartDate(), requestAttributes.getEndDate());
            return String.valueOf(difference);
        }
        throw new IllegalArgumentException(
                String.format("Start date for %s must be today, received %s", credential, requestAttributes.getStartDate()));
    }

    private void pause() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
