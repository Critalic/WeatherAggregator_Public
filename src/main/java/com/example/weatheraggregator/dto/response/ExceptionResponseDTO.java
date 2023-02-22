package com.example.weatheraggregator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExceptionResponseDTO {
    private final String exceptionMessage;
    private final String requestDescription;
    private final int httpStatus;
    private final String date;
}
