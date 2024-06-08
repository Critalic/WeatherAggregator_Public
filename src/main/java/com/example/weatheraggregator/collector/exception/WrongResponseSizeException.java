package com.example.weatheraggregator.collector.exception;

public class WrongResponseSizeException extends RuntimeException {
    public WrongResponseSizeException(String errorMessage) {
        super(errorMessage);
    }
}
