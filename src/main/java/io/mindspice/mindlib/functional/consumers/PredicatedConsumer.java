package io.mindspice.mindlib.functional.consumers;

import java.util.function.Consumer;
import java.util.function.Predicate;


public record PredicatedConsumer<T>(
        Predicate<T> predicate,
        Consumer<T> consumer
) implements Consumer<T> {

    public static <T> PredicatedConsumer<T> of(Predicate<T> predicate, Consumer<T> consumer) {
        return new PredicatedConsumer<>(predicate, consumer);
    }

    public void accept(T event) {
        if (predicate == null || predicate.test(event)) {
            consumer.accept(event);
        }
    }
}
