package io.mindspice.mindlib.functional.consumers;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class RunnableTriConsumer<T, U, V> implements Runnable {
    private final TriConsumer<T, U, V> consumer;
    private final T firstObj;
    private final U secondObj;
    private final V thirdObj;

    public RunnableTriConsumer(TriConsumer<T, U, V> consumer, T firstObj, U secondObj, V thirdObj) {
        this.consumer = Objects.requireNonNull(consumer, "Consumer cannot be null");
        this.firstObj = Objects.requireNonNull(firstObj, "First object cannot be null");
        this.secondObj = Objects.requireNonNull(secondObj, "Second object cannot be null");
        this.thirdObj = Objects.requireNonNull(thirdObj, "Third object cannot be null");
    }

    @Override
    public void run() {
        consumer.accept(firstObj, secondObj, thirdObj);
    }
}
