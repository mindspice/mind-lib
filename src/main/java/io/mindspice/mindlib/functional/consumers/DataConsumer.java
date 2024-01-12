package io.mindspice.mindlib.functional.consumers;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


public record DataConsumer<T, U>(
        U data,
        BiConsumer<T, U> consumer
) implements Consumer<T> {
    @Override
    public void accept(T u) {
        consumer.accept(u, data);
    }
}

