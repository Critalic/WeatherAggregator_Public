package com.example.weatheraggregator.api.batch.jpa.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class RepositoryCollectionWriter<T> implements ItemWriter<Collection<T>> {
    private final Consumer<Collection<T>> consumer;

    public RepositoryCollectionWriter(Consumer<Collection<T>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void write(Chunk<? extends Collection<T>> chunk) {
        chunk.forEach(consumer);
    }
}
