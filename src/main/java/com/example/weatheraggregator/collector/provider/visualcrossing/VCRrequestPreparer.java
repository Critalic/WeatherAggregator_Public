package com.example.weatheraggregator.collector.provider.visualcrossing;

import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.collector.model.TimeStep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public interface VCRrequestPreparer {

    static Map<String, List<String>> prepareRequest(RequestAttributes requestAttributes, String apiKey) {
        Map<String, List<String>> params = new HashMap<>();
        params.put("key", singletonList(apiKey));
        params.put("include", singletonList(parseTimestep(requestAttributes.getTimeStep())));
        params.put("unitGroup", singletonList("metric"));
        params.put("contentType", singletonList("json"));
        return params;
    }

    private static String parseTimestep(TimeStep timeStep) {
        return switch (timeStep) {
            case HOUR -> "hours";
            case DAY -> "days";
            default -> throw new IllegalArgumentException("Unsupported timeStep format provided: " + timeStep);
        };
    }
}
