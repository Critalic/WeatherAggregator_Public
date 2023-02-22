package com.example.weatheraggregator.collector.provider;

import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.dto.business.ForecastDTO;
import com.example.weatheraggregator.dto.business.ResponseDTO;


import java.util.Collection;

public interface WeatherDataProvider {
    ResponseDTO makeRequest(RequestAttributes requestAttributes);
    String getCredential();
    Collection<ForecastDTO> parseResponse(ResponseDTO responseDTO);
}
