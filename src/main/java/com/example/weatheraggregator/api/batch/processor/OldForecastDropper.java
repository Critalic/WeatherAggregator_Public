package com.example.weatheraggregator.api.batch.processor;

import com.example.weatheraggregator.api.persistence.entity.City;
import com.example.weatheraggregator.api.persistence.entity.Forecast;
import com.example.weatheraggregator.api.persistence.repository.ForecastRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OldForecastDropper implements ItemWriter<City> {
    private final LocalDateTime currentTimestamp;
    private final String providerCredential;
    private final ForecastRepository forecastRepository;

    public  OldForecastDropper(LocalDateTime currentTimestamp,
                              String providerCredential, ForecastRepository forecastRepository) {
        this.currentTimestamp = currentTimestamp;
        this.providerCredential = providerCredential;
        this.forecastRepository = forecastRepository;
    }

    @Override
    public void write(Chunk<? extends City> chunk) {
        chunk.forEach(city -> {
            List<Forecast> newForecasts = forecastRepository
                    .findByTimestampAndCityAndProvider(currentTimestamp, city, providerCredential);
            if(!newForecasts.isEmpty()) {
                List<LocalDateTime> forecastedTimes = newForecasts.stream().map(Forecast::getTime).sorted().toList();

                List<Forecast> oldForecasts = forecastRepository
                        .findByCityAndProviderInRangeExcluding(city, providerCredential, forecastedTimes.get(0),
                                forecastedTimes.get(forecastedTimes.size() - 1), currentTimestamp);

                Set<Long> toDelete = new HashSet<>();
                for (var oldForecast : oldForecasts) {
                    boolean shouldBeDeleted = newForecasts.stream().anyMatch(newForecast ->
                            newForecast.getForecastType().equals(oldForecast.getForecastType()) &&
                                    newForecast.getTime().equals(oldForecast.getTime()));
                    if (shouldBeDeleted) {
                        toDelete.add(oldForecast.getId());
                    }
                }
                forecastRepository.deleteAllById(toDelete);
            }
        });
    }
}
