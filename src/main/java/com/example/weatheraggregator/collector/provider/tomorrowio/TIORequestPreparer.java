package com.example.weatheraggregator.collector.provider.tomorrowio;

import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.collector.model.TimeStep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public interface TIORequestPreparer {
    static Map<String, List<String>> prepareRequest(RequestAttributes requestAttributes, List<String> requestFields, String apiKey) {
        Map<String, List<String>> params = new HashMap<>();
        params.put("apikey", singletonList(apiKey));
        params.put("startTime", singletonList(requestAttributes.getStartDate().plusDays(1).toString()));
        params.put("endTime", singletonList(requestAttributes.getEndDate().toString()));
        params.put("timesteps", singletonList(parseTimestep(requestAttributes.getTimeStep())));
        params.put("units", singletonList("metric"));
        params.put("location", singletonList(requestAttributes.getCity().getLat() + "," +
                requestAttributes.getCity().getLng()));
        params.put("fields", requestFields);
        return params;
    }

    private static String parseTimestep(TimeStep timeStep) {
        return switch (timeStep) {
            case HOUR -> "1h";
            case DAY -> "1d";
            default -> throw new IllegalArgumentException("Unsupported timeStep format provided: " + timeStep);
        };
    }
}
