package io.mindspice.mindlib.functional.consumers;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;


public record BiPredicatedBiConsumer<T, U>(
        BiPredicate<T, U> predicate,
        BiConsumer<T, U> consumer
) implements BiConsumer<T, U> {

    public static <T, U> BiPredicatedBiConsumer<T, U> of(BiPredicate<T, U> predicate, BiConsumer<T, U> consumer) {
        return new BiPredicatedBiConsumer<>(predicate, consumer);
    }

    @Override
    public void accept(T input1, U input2) {
        if (predicate == null || predicate.test(input1, input2)) {
            consumer.accept(input1, input2);
        }
    }
}