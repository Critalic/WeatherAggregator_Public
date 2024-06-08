package com.example.weatheraggregator.collector.provider;

import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.dto.business.ResponseDTO;

public interface HistoricalWeatherDataProvider extends WeatherForecastDataProvider {
    ResponseDTO makeHistoricalRequest(RequestAttributes requestAttributes);

}
