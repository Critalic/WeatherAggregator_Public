package com.example.weatheraggregator.collector.provider.tomorrowio;

import com.example.weatheraggregator.aspect.LogOnException;
import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.collector.provider.WeatherForecastDataProvider;
import com.example.weatheraggregator.collector.provider.tomorrowio.response.TIOResponseParser;
import com.example.weatheraggregator.dto.business.ForecastDTO;
import com.example.weatheraggregator.dto.business.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class TomorrowIO implements WeatherForecastDataProvider {
    private int PAUSE_COUNTER = 0;

    private final LinkedList<String> apiKeys;
    private final String credential;
    private final WebClient webClient;
    private final List<String> fields = Arrays.asList("temperature", "pressureSurfaceLevel",
            "weatherCode", "windDirection", "humidity", "windSpeed");
    private final TIOResponseParser responseParser;

    private String currentApiKey;

    public TomorrowIO(Builder webClientBuilder, LinkedList<String> apiKeys, String credential) {
        this.apiKeys = apiKeys;
        this.credential = credential;
        this.webClient = webClientBuilder
                .baseUrl("https://api.tomorrow.io/v4")
                .defaultHeader("accept", "application/json")
                .defaultHeader("Accept-Encoding", "gzip")
                .build();
        this.currentApiKey = swapApiKeys(this.apiKeys);
        responseParser = new TIOResponseParser();
    }

    @Override
    @LogOnException
    public ResponseDTO makeForecastRequest(RequestAttributes requestAttributes) {
        System.out.printf("Requesting %s with key %s city %s and type %s%n", credential, currentApiKey, requestAttributes.getCity(),
                requestAttributes.getTimeStep());

        String response = sendRequest(requestAttributes);
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

    private String sendRequest(RequestAttributes requestAttributes) {
        var argsMap = TIORequestPreparer.prepareRequest(requestAttributes, fields, currentApiKey);
        pause();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/timelines")
                        .queryParams(new MultiValueMapAdapter<>(argsMap))
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1))
                        .filter(e -> e instanceof WebClientResponseException &&
                                ((WebClientResponseException) e).getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS))
                        .doBeforeRetry((t) -> currentApiKey = swapApiKeys(apiKeys)))
                .block();
    }

    private void pause() {
        if (PAUSE_COUNTER > 10) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            PAUSE_COUNTER = 0;
        } else {
            PAUSE_COUNTER++;
        }
    }
}