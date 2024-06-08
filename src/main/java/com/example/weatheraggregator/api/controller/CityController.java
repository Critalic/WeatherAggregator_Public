package com.example.weatheraggregator.api.controller;

import com.example.weatheraggregator.api.service.CityService;
import com.example.weatheraggregator.dto.business.CityDTO;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/city")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/name")
    public ResponseEntity<List<CityDTO>> getCitiesByName(@RequestParam @NotEmpty(message = "City name can't be empty")
                                                                     String cityName) {
        return ResponseEntity.ok(cityService.getCityByName(cityName));
    }

    @GetMapping("/coordinates")
    public ResponseEntity<CityDTO> getCityByCoordinates(@RequestParam double lat, @RequestParam double lng) {
        return ResponseEntity.ok(cityService.getCityByCoordinates(lat, lng));
    }
}
