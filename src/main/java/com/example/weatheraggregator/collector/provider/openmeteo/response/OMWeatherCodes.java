package com.example.weatheraggregator.collector.provider.openmeteo.response;

import java.util.Map;

import static java.util.Map.entry;

public class OMWeatherCodes {
    private static final Map<Integer, String> weatherCodes = Map.<Integer, String>ofEntries(
            entry(0, "Clear sky"),
            entry(1, "Mainly clear"),
            entry(2, "Partly cloudy"),
            entry(3, "Overcast"),
            entry(45, "Fog"),
            entry(48, "Depositing rime fog"),
            entry(51, "Drizzle:Light"),
            entry(53, "Drizzle:moderate"),
            entry(55, "Drizzle:dense"),
            entry(56, "Freezing Drizzle:Light"),
            entry(57, "Freezing Drizzle:dense"),
            entry(61, "Rain:Slight"),
            entry(63, "Rain:moderate"),
            entry(65, "Rain:heavy"),
            entry(66, "Freezing Rain:Light"),
            entry(67, "Freezing Rain:heavy"),
            entry(71, "Snow fall:Slight"),
            entry(73, "Snow fall:moderate"),
            entry(75, "Snow fall:heavy"),
            entry(77, "Snow grains"),
            entry(80, "Rain showers:Slight"),
            entry(81, "Rain showers:moderate"),
            entry(82, "Rain showers:violent"),
            entry(85, "Snow showers slight"),
            entry(86, "Snow showers heavy"),
            entry(96, "Thunderstorm with slight hail"),
            entry(99, "Thunderstorm with heavy hail")
    );

    public static String getCodeDescription(Integer code) {
        return weatherCodes.get(code);
    }
}
