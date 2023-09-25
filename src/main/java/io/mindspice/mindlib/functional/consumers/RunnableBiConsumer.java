package io.mindspice.mindlib.functional.consumers;

import java.util.Objects;
import java.util.function.BiConsumer;


public class RunnableBiConsumer<T, U> implements Runnable {
    private final BiConsumer<T, U> consumer;
    private final T firstObj;
    private final U secondObj;

    public RunnableBiConsumer(BiConsumer<T, U> consumer, T firstObj, U secondObj) {
        this.consumer = Objects.requireNonNull(consumer, "Consumer cannot be null");
        this.firstObj = Objects.requireNonNull(firstObj, "First object cannot be null");
        this.secondObj = Objects.requireNonNull(secondObj, "Second object cannot be null");
    }

    @Override
    public void run() {
        consumer.accept(firstObj, secondObj);
    }
}
