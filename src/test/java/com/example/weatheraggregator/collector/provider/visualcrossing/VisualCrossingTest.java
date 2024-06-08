package com.example.weatheraggregator.collector.provider.visualcrossing;

import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.collector.model.TimeStep;
import com.example.weatheraggregator.dto.business.CityDTO;
import com.example.weatheraggregator.dto.business.ForecastDTO;
import com.example.weatheraggregator.dto.business.ResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class VisualCrossingTest {

    String apiKey = "EDK793CFQSEEFUSU7PKCSFAPQ";

    @Test
    void verifyRequest() {
        LinkedList<String> apiKeys = new LinkedList<>();
        apiKeys.add(apiKey);
        VisualCrossing visualCrossing = new VisualCrossing(WebClient.builder(), apiKeys, "VisualCrossing");

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setData(exampleData());
        responseDTO.setProviderCredential("VisualCrossing");
        responseDTO.setTimeStep(TimeStep.DAY);
        RequestAttributes requestAttributes = new RequestAttributes(LocalDate.of(2021, 6, 22)
                .atStartOfDay(), LocalDate.of(2021, 6, 23).atStartOfDay(), TimeStep.DAY,
                new CityDTO("Zocca", 44.3439, 10.9988, "Italy"));
        ResponseDTO response = visualCrossing.makeForecastRequest(requestAttributes);
        Collection<ForecastDTO> forecasts = visualCrossing.parseResponse(response);

        assertThat(forecasts, equalTo(visualCrossing.parseResponse(responseDTO)));
        assertThat(forecasts, is(visualCrossing.parseResponse(responseDTO)));
    }

    private String exampleData() {
        return "{\n" +
                "    \"queryCost\": 2,\n" +
                "    \"latitude\": 44.3439,\n" +
                "    \"longitude\": 10.9988,\n" +
                "    \"resolvedAddress\": \"Zocca, Emilia Romagna, Italia\",\n" +
                "    \"address\": \"Zocca\",\n" +
                "    \"timezone\": \"Europe/Rome\",\n" +
                "    \"tzoffset\": 2,\n" +
                "    \"days\": [\n" +
                "        {\n" +
                "            \"datetime\": \"2021-06-22\",\n" +
                "            \"datetimeEpoch\": 1624312800,\n" +
                "            \"tempmax\": 33.7,\n" +
                "            \"tempmin\": 20.7,\n" +
                "            \"temp\": 27.6,\n" +
                "            \"feelslikemax\": 32.9,\n" +
                "            \"feelslikemin\": 20.7,\n" +
                "            \"feelslike\": 27.4,\n" +
                "            \"dew\": 15.5,\n" +
                "            \"humidity\": 51,\n" +
                "            \"precip\": 0,\n" +
                "            \"precipprob\": 0,\n" +
                "            \"precipcover\": 0,\n" +
                "            \"preciptype\": null,\n" +
                "            \"snow\": 0,\n" +
                "            \"snowdepth\": 0,\n" +
                "            \"windgust\": null,\n" +
                "            \"windspeed\": 14.4,\n" +
                "            \"winddir\": 199.2,\n" +
                "            \"pressure\": 1010.1,\n" +
                "            \"cloudcover\": 43.1,\n" +
                "            \"visibility\": 10,\n" +
                "            \"solarradiation\": 330.3,\n" +
                "            \"solarenergy\": 28.8,\n" +
                "            \"uvindex\": 9,\n" +
                "            \"sunrise\": \"05:32:12\",\n" +
                "            \"sunriseEpoch\": 1624332732,\n" +
                "            \"sunset\": \"21:03:58\",\n" +
                "            \"sunsetEpoch\": 1624388638,\n" +
                "            \"moonphase\": 0.46,\n" +
                "            \"conditions\": \"Partially cloudy\",\n" +
                "            \"description\": \"Clearing in the afternoon.\",\n" +
                "            \"icon\": \"partly-cloudy-day\",\n" +
                "            \"stations\": [\n" +
                "                \"16170099999\",\n" +
                "                \"LIPE\",\n" +
                "                \"C4352\",\n" +
                "                \"16171099999\",\n" +
                "                \"16140099999\",\n" +
                "                \"F2094\"\n" +
                "            ],\n" +
                "            \"source\": \"obs\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"datetime\": \"2021-06-23\",\n" +
                "            \"datetimeEpoch\": 1624399200,\n" +
                "            \"tempmax\": 33.2,\n" +
                "            \"tempmin\": 19.9,\n" +
                "            \"temp\": 26.5,\n" +
                "            \"feelslikemax\": 32.6,\n" +
                "            \"feelslikemin\": 19.9,\n" +
                "            \"feelslike\": 26.3,\n" +
                "            \"dew\": 15.2,\n" +
                "            \"humidity\": 52,\n" +
                "            \"precip\": 0,\n" +
                "            \"precipprob\": 0,\n" +
                "            \"precipcover\": 0,\n" +
                "            \"preciptype\": null,\n" +
                "            \"snow\": 0,\n" +
                "            \"snowdepth\": 0,\n" +
                "            \"windgust\": null,\n" +
                "            \"windspeed\": 12,\n" +
                "            \"winddir\": 258.3,\n" +
                "            \"pressure\": 1013.7,\n" +
                "            \"cloudcover\": 32.6,\n" +
                "            \"visibility\": 10,\n" +
                "            \"solarradiation\": 286.8,\n" +
                "            \"solarenergy\": 24.9,\n" +
                "            \"uvindex\": 9,\n" +
                "            \"sunrise\": \"05:32:28\",\n" +
                "            \"sunriseEpoch\": 1624419148,\n" +
                "            \"sunset\": \"21:04:06\",\n" +
                "            \"sunsetEpoch\": 1624475046,\n" +
                "            \"moonphase\": 0.49,\n" +
                "            \"conditions\": \"Partially cloudy\",\n" +
                "            \"description\": \"Partly cloudy throughout the day.\",\n" +
                "            \"icon\": \"partly-cloudy-day\",\n" +
                "            \"stations\": [\n" +
                "                \"16170099999\",\n" +
                "                \"LIPE\",\n" +
                "                \"C4352\",\n" +
                "                \"16171099999\",\n" +
                "                \"16140099999\",\n" +
                "                \"F2094\"\n" +
                "            ],\n" +
                "            \"source\": \"obs\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"stations\": {\n" +
                "        \"16170099999\": {\n" +
                "            \"distance\": 61681,\n" +
                "            \"latitude\": 43.81,\n" +
                "            \"longitude\": 11.205,\n" +
                "            \"useCount\": 0,\n" +
                "            \"id\": \"16170099999\",\n" +
                "            \"name\": \"FIRENZE, IT\",\n" +
                "            \"quality\": 98,\n" +
                "            \"contribution\": 0\n" +
                "        },\n" +
                "        \"LIPE\": {\n" +
                "            \"distance\": 31660,\n" +
                "            \"latitude\": 44.53,\n" +
                "            \"longitude\": 11.3,\n" +
                "            \"useCount\": 0,\n" +
                "            \"id\": \"LIPE\",\n" +
                "            \"name\": \"LIPE\",\n" +
                "            \"quality\": 49,\n" +
                "            \"contribution\": 0\n" +
                "        },\n" +
                "        \"C4352\": {\n" +
                "            \"distance\": 29695,\n" +
                "            \"latitude\": 44.54,\n" +
                "            \"longitude\": 10.746,\n" +
                "            \"useCount\": 0,\n" +
                "            \"id\": \"C4352\",\n" +
                "            \"name\": \"CW4352 Castellarano IT\",\n" +
                "            \"quality\": 0,\n" +
                "            \"contribution\": 0\n" +
                "        },\n" +
                "        \"16171099999\": {\n" +
                "            \"distance\": 64794,\n" +
                "            \"latitude\": 43.783,\n" +
                "            \"longitude\": 11.217,\n" +
                "            \"useCount\": 0,\n" +
                "            \"id\": \"16171099999\",\n" +
                "            \"name\": \"FIRENZE, IT\",\n" +
                "            \"quality\": 99,\n" +
                "            \"contribution\": 0\n" +
                "        },\n" +
                "        \"16140099999\": {\n" +
                "            \"distance\": 31390,\n" +
                "            \"latitude\": 44.535,\n" +
                "            \"longitude\": 11.289,\n" +
                "            \"useCount\": 0,\n" +
                "            \"id\": \"16140099999\",\n" +
                "            \"name\": \"BOLOGNA, IT\",\n" +
                "            \"quality\": 99,\n" +
                "            \"contribution\": 0\n" +
                "        },\n" +
                "        \"F2094\": {\n" +
                "            \"distance\": 12485,\n" +
                "            \"latitude\": 44.39,\n" +
                "            \"longitude\": 11.142,\n" +
                "            \"useCount\": 0,\n" +
                "            \"id\": \"F2094\",\n" +
                "            \"name\": \"FW2094 San Chierlo IT\",\n" +
                "            \"quality\": 0,\n" +
                "            \"contribution\": 0\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }
}