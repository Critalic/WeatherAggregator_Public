package com.example.weatheraggregator.api.batch.util;

import com.example.weatheraggregator.collector.model.RequestAttributes;
import com.example.weatheraggregator.collector.model.TimeStep;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;


class PropertyParserTest {
    @Test
    void parseEndtPositiveInterval() {
        RequestAttributes first = new RequestAttributes(LocalDateTime.now(), LocalDateTime.now().plusDays(5),
                TimeStep.HOUR, null);
        RequestAttributes second = new RequestAttributes(LocalDateTime.now(), LocalDateTime.now().plusDays(10),
                TimeStep.DAY, null);

        List<RequestAttributes> parsed = PropertyParser.parseIntervals("5d, h; 10d, d");

        Assertions.assertEquals(parsed, List.of(first, second));
    }

    @Test
    void parseEndStartPositiveInterval() {
        RequestAttributes first = new RequestAttributes(LocalDateTime.now().minusDays(5),
                LocalDateTime.now().plusDays(10), TimeStep.HOUR, null);
        RequestAttributes second = new RequestAttributes(LocalDateTime.now().minusDays(10),
                LocalDateTime.now().plusHours(2), TimeStep.DAY, null);

        List<RequestAttributes> parsed = PropertyParser.parseIntervals("5d, 10d, h; 10d, 2h, d;");

        Assertions.assertEquals(parsed, List.of(first, second));
    }

    @Test
    void parseEndStartNegativeInterval() {
        Throwable throwable = assertThrows(IllegalArgumentException.class,
                () -> PropertyParser.parseIntervals("5d, -10d, h; 10d, 2d, d;"));
        Assertions.assertEquals("Provided interval parameters can't be negative", throwable.getMessage());
    }

    @Test
    void parseWrongOrderInterval() {
        Throwable throwable = assertThrows(IllegalArgumentException.class,
                () -> PropertyParser.parseIntervals("5d, h, 5d; 10d, 2d, d;"));
        Assertions.assertEquals("Unsupported timeStep format provided: 5d", throwable.getMessage());
    }

    @Test
    void parseIncorrectInterval() {
        Throwable throwable = assertThrows(IllegalArgumentException.class,
                () -> PropertyParser.parseIntervals("5d, m; 10d, 2d, d"));
        Assertions.assertEquals("Unsupported timeStep format provided: m", throwable.getMessage());
    }

    @Test
    void parseIncorrectLengthInterval() {
        Throwable throwable = assertThrows(IllegalArgumentException.class,
                () -> PropertyParser.parseIntervals("5d, d; 10d, 2d, d, d"));
        Assertions.assertEquals("Unexpected number of parameters provided: 4", throwable.getMessage());
    }

    @Test
    void parseIncorrectLength() {
        Throwable throwable = assertThrows(IllegalArgumentException.class,
                () -> PropertyParser.parseIntervals(" "));
        Assertions.assertEquals("Unexpected input parameter length: 1", throwable.getMessage());
    }
}
