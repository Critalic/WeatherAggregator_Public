package com.example.weatheraggregator.collector.provider.visualcrossing;

import com.example.weatheraggregator.aspect.LogOnException;
import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.collector.model.TimeStep;
import com.example.weatheraggregator.collector.provider.WeatherDataProvider;
import com.example.weatheraggregator.collector.provider.visualcrossing.response.VCResponseParser;
import com.example.weatheraggregator.dto.business.ForecastDTO;
import com.example.weatheraggregator.dto.business.ResponseDTO;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class VisualCrossing implements WeatherDataProvider {
    private final String apiKey;
    private final String credential;

    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final VCResponseParser responseParser;
    private final WebClient client;

    public VisualCrossing(WebClient.Builder webClientBuilder, String apiKey, String credential) {
        this.apiKey = apiKey;
        this.credential = credential;
        this.client = webClientBuilder
                .baseUrl("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services")
                .defaultHeader("accept", "application/json")
                .defaultHeader("Accept-Encoding", "gzip")
                .build();

        responseParser = new VCResponseParser();
    }

    @Override
    @LogOnException
    public ResponseDTO makeRequest(RequestAttributes requestAttributes) {
        Map<String, List<String>> params = new HashMap<>();
        params.put("key", singletonList(apiKey));
        params.put("include", singletonList(parseTimestep(requestAttributes.getTimeStep())));
        params.put("unitGroup", singletonList("metric"));
        params.put("contentType", singletonList("json"));

        String response = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/timeline/" + requestAttributes.getCity().getName() + "/" +
                                parseTimeFormat(requestAttributes.getStartDate()) + "/" +
                                parseTimeFormat(requestAttributes.getEndDate()))
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

    private String parseTimeFormat(LocalDateTime time) {
        return time.format(format);
    }

    private String parseTimestep(TimeStep timeStep) {
        return switch (timeStep) {
            case HOUR -> "hours";
            case DAY -> "days";
            default -> throw new IllegalArgumentException("Unsupported timeStep format provided: " + timeStep);
        };
    }
}
