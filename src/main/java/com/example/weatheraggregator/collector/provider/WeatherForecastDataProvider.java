package com.example.weatheraggregator.collector.provider;

import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.dto.business.ForecastDTO;
import com.example.weatheraggregator.dto.business.ResponseDTO;


import java.util.Collection;
import java.util.LinkedList;

public interface WeatherForecastDataProvider {
    ResponseDTO makeForecastRequest(RequestAttributes requestAttributes);

    String getCredential();

    Collection<ForecastDTO> parseResponse(ResponseDTO responseDTO);

    default String swapApiKeys(LinkedList<String> apiKeys) {
        synchronized (this) {
            String moved = apiKeys.removeFirst();
            apiKeys.addLast(moved);
            return moved;
        }
    }
}
