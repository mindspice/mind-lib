package io.mindspice.mindlib.functional.consumers;

import java.util.Objects;
import java.util.function.Consumer;


public class RunnableQuadConsumer<T, U, V , X> implements Runnable {
    private final QuadConsumer<T, U, V, X> consumer;
    private final T firstObj;
    private final U secondObj;
    private final V thirdObj;
    private final X forthObj;

    public RunnableQuadConsumer(QuadConsumer<T, U, V, X> consumer, T firstObj, U secondObj, V thirdObj, X forthObj) {
        this.consumer = Objects.requireNonNull(consumer, "Consumer cannot be null");
        this.firstObj = Objects.requireNonNull(firstObj, "First object cannot be null");
        this.secondObj = Objects.requireNonNull(secondObj, "Second object cannot be null");
        this.thirdObj = Objects.requireNonNull(thirdObj, "Second object cannot be null");
        this.forthObj = Objects.requireNonNull(forthObj, "Second object cannot be null");
    }

    @Override
    public void run() {
        consumer.accept(firstObj, secondObj, thirdObj, forthObj);
    }
}
