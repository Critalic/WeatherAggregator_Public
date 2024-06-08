package com.example.weatheraggregator.collector.provider.tomorrowio;

import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.collector.model.TimeStep;
import com.example.weatheraggregator.dto.business.CityDTO;
import com.example.weatheraggregator.dto.business.ForecastDTO;
import com.example.weatheraggregator.dto.business.ResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class TomorrowIOTest {
    String apiKey = "matnXpdk0AY0HsfH3n3jP5gEULeztEOx";

    @Test
    void verifyRequest() {
        LinkedList<String> apiKeys = new LinkedList<>();
        apiKeys.add(apiKey);
        TomorrowIO tomorrowIO = new TomorrowIO(WebClient.builder(), apiKeys, "TomorrowIO");

        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        LocalDateTime end = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.HOURS);

        RequestAttributes requestAttributes = new RequestAttributes(start, end, TimeStep.DAY,
                new CityDTO("Zocca", 44.3439, 10.9988, "Italy"));

        ResponseDTO response = tomorrowIO.makeForecastRequest(requestAttributes);
        Collection<ForecastDTO> forecasts = tomorrowIO.parseResponse(response);

        assertThat(forecasts.size(), is(2));
        assertThat(forecasts, equalTo(tomorrowIO.parseResponse(response)));
    }
}