package io.mindspice.mindlib.functional.consumers;

import java.util.Objects;
import java.util.function.Consumer;


public class RunnableQuadConsumer<T> implements Runnable {
    private final Consumer<T> consumer;
    private final T dataObj;

    public RunnableQuadConsumer(Consumer<T> consumer, T dataObj) {
        this.consumer = Objects.requireNonNull(consumer, "Consumer cannot be null");
        this.dataObj = Objects.requireNonNull(dataObj, "Data object cannot be null");
    }

    @Override
    public void run() {
        consumer.accept(dataObj);
    }
}
