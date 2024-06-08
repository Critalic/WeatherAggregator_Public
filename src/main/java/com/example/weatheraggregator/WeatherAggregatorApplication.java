package com.example.weatheraggregator;

import com.example.weatheraggregator.api.persistence.entity.Forecast;
import com.example.weatheraggregator.api.persistence.repository.CityRepository;
import com.example.weatheraggregator.api.persistence.repository.ForecastRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@EnableTransactionManagement
@PropertySource("classpath:properties/persistence.properties")
public class WeatherAggregatorApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(WeatherAggregatorApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
