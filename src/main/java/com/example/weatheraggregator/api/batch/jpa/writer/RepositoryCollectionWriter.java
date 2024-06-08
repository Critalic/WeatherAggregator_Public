package com.example.weatheraggregator.api.batch.jpa.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

public class RepositoryCollectionWriter<T> implements ItemWriter<Collection<T>> {
    private final Consumer<Collection<T>> consumer;

    public RepositoryCollectionWriter(Consumer<Collection<T>> consumer) {
        this.consumer = consumer;
    }

    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public void write(Chunk<? extends Collection<T>> chunk) {
        chunk.forEach(consumer);
    }
}
