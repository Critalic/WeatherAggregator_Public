package com.example.weatheraggregator.collector;

import com.example.weatheraggregator.collector.provider.aggregator.AggregatorAPI;
import com.example.weatheraggregator.collector.provider.aggregator.TokenService;
import com.example.weatheraggregator.collector.provider.openmeteo.OpenMeteo;
import com.example.weatheraggregator.collector.provider.tomorrowio.TomorrowIO;
import com.example.weatheraggregator.collector.provider.visualcrossing.VisualCrossing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedList;

@Configuration
@EnableScheduling
@PropertySource("classpath:properties/collector.properties")
@ConditionalOnProperty(name = "scheduling.enabled", matchIfMissing = true)
public class CollectorBeanConfig {
    private final WebClient.Builder builder;

    @Value("${aggregator.domain}")
    private String aggregatorDomain;
    @Value("${aggregator.request.timestamp.format}")
    private String timeStampFormat;
    @Value("#{'${tomorrowIo.api.keys}'.split(',')}")
    private LinkedList<String> tomorrowioApiKeys;
    @Value("#{'${visualCrossing.api.keys}'.split(',')}")
    private LinkedList<String> visualCrossingApiKeys;

    private static final String OPEN_METEO = "OpenMeteo";
    private static final String VISUAL_CROSSING = "VisualCrossing";
    private static final String AGGREGATOR = "Aggregator";
    private static final String TOMORROW_IO = "TomorrowIO";

    public CollectorBeanConfig(WebClient.Builder builder) {
        this.builder = builder;
    }

    @Bean("TomorrowIO")
    public TomorrowIO tomorrowIO() {
        return new TomorrowIO(builder, tomorrowioApiKeys, TOMORROW_IO);
    }

    @Bean("VisualCrossing")
    public VisualCrossing visualCrossing() {
        return new VisualCrossing(builder, visualCrossingApiKeys, VISUAL_CROSSING);
    }

    @Bean("Aggregator")
    public AggregatorAPI aggregatorAPI(TokenService tokenService) {
        return new AggregatorAPI(aggregatorDomain, timeStampFormat, tokenService, builder, AGGREGATOR);
    }

    @Bean("OpenMeteo")
    public OpenMeteo openMeteo() {
        return new OpenMeteo(builder, OPEN_METEO);
    }
}
