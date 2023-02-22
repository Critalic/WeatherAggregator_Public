package com.example.weatheraggregator.api.controller;

import com.example.weatheraggregator.api.service.ForecastService;
import com.example.weatheraggregator.api.service.ForecastTypeService;
import com.example.weatheraggregator.dto.business.ForecastPageDTO;
import com.example.weatheraggregator.dto.request.ForecastRequest;
import com.example.weatheraggregator.dto.request.MeasurementUnit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/forecast")
public class ForecastController {
    private final ForecastService forecastService;
    private final ForecastTypeService forecastTypeService;

    public ForecastController(ForecastService forecastService, ForecastTypeService forecastTypeService) {
        this.forecastService = forecastService;
        this.forecastTypeService = forecastTypeService;
    }

    @GetMapping("/type")
    public ResponseEntity<Collection<String>> getForecastTypes() {
        return ResponseEntity.ok(forecastTypeService.getForecastTypes());
    }

    @GetMapping("/measurement")
    public ResponseEntity<Collection<MeasurementUnit>> getMeasurementUnits() {
        return ResponseEntity.ok(Arrays.stream(MeasurementUnit.values()).collect(Collectors.toList()));
    }

    @GetMapping("/provider/{providerCredential}")
    public ResponseEntity<ForecastPageDTO> getCityForecastFromProvider(@Valid ForecastRequest request,
                                                                       @PathVariable("providerCredential")
                                                                       String providerCredential,
                                                                       @RequestParam(required = false, defaultValue = "0")
                                                                                   int page,
                                                                       @RequestParam(required = false, defaultValue = "50")
                                                                           @Max(100) int size) {
        setDefaultRequestUnitIfNull(request);
        ForecastPageDTO response = forecastService.getForecastsFromProvider(request, providerCredential,
                PageRequest.of(page, size));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/aggregated")
    public ResponseEntity<ForecastPageDTO> getCityForecastAggregated(@Valid ForecastRequest request,
                                                                     @RequestParam(required = false, defaultValue = "0")
                                                                                 int page,
                                                                     @RequestParam(required = false, defaultValue = "50")
                                                                         @Max(100) int size) {
        setDefaultRequestUnitIfNull(request);
        ForecastPageDTO response = forecastService.getForecastsAggregated(request, PageRequest.of(page, size));
        return ResponseEntity.ok(response);
    }

    private void setDefaultRequestUnitIfNull(ForecastRequest request) {
        if(Objects.isNull(request.getUnit())) {
            request.setUnit(MeasurementUnit.METRIC);
        }
    }
}
