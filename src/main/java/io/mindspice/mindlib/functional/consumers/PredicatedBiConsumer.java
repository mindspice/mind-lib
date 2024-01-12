package io.mindspice.mindlib.functional.consumers;

import java.util.function.BiConsumer;
import java.util.function.Predicate;


public record PredicatedBiConsumer<U, T>(
        Predicate<T> predicate,
        BiConsumer<U, T> consumer
) implements BiConsumer<U, T> {

    public static <U, T> PredicatedBiConsumer<U, T> of(Predicate<T> predicate, BiConsumer<U, T> consumer) {
        return new PredicatedBiConsumer<>(predicate, consumer);
    }

    public void accept(U selfReference, T event) {
        if (predicate == null || predicate.test(event)) {
            consumer.accept(selfReference, event);
        }
    }
}
