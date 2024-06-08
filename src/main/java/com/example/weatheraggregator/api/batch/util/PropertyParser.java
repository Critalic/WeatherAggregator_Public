package com.example.weatheraggregator.api.batch.util;

import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.collector.model.TimeStep;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;

public interface PropertyParser {
    static List<RequestAttributes> parseIntervals(String intervals) {
        if (isNull(intervals)) {
            throw new IllegalArgumentException("Unexpected input parameter: null");
        } else if (intervals.isBlank() || intervals.length() < 5) {
            throw new IllegalArgumentException("Unexpected input parameter length: " + intervals.length());
        }
        String[] intervals1d = intervals.split(";");

        return Arrays.stream(intervals1d)
                .map(interval -> Arrays.stream(interval.trim().split(","))
                        .map(String::trim)
                        .toArray(String[]::new))
                .map(PropertyParser::toRequestAttributes)
                .toList();
    }

    private static RequestAttributes toRequestAttributes(String[] params) {
        RequestAttributes attributes = new RequestAttributes();
        attributes.setTimeStep(getTimeStep(params[params.length - 1]));
        switch (params.length) {
            case 3 -> {
                attributes.setStartDate(parseTime(params[0], -1));
                attributes.setEndDate(parseTime(params[1], 1));
            }
            case 2 -> {
                attributes.setStartDate(LocalDateTime.now());
                attributes.setEndDate(parseTime(params[0], 1));
            }
            default -> throw new IllegalArgumentException("Unexpected number of parameters provided: " + params.length);
        }
        return attributes;
    }

    private static LocalDateTime parseTime(String property, int sign) {
        int timeAmount = Integer.parseInt(property.substring(0, property.length() - 1));
        if (timeAmount < 0) {
            throw new IllegalArgumentException("Provided interval parameters can't be negative");
        }
        return switch (property.charAt(property.length() - 1)) {
            case 'd' -> LocalDateTime.now().plusDays((long) timeAmount * sign);
            case 'h' -> LocalDateTime.now().plusHours((long) timeAmount * sign);
            default -> throw new IllegalArgumentException("Unexpected parameter provided: " + property);
        };
    }

    private static TimeStep getTimeStep(String property) {
        return switch (property) {
            case "h" -> TimeStep.HOUR;
            case "d" -> TimeStep.DAY;
            default -> throw new IllegalArgumentException("Unsupported timeStep format provided: " + property);
        };
    }
}
