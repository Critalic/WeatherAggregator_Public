package com.example.weatheraggregator.collector.model;

public enum TimeStep {
    HOUR("Hourly"),
    DAY("Daily"),
    DAY_HOUR;

    private String type;

    TimeStep(String type) {
        this.type = type;
    }

    TimeStep() {
    }

    public String getType() {
        return type;
    }

    public static TimeStep getByType(String type) {
        return switch (type) {
            case "Hourly" -> HOUR;
            case "Daily" -> DAY;
            default -> throw new IllegalArgumentException("Unexpected parameter passed: " + type);
        };
    }
}
