package io.mindspice.mindlib.functional.predicates;

import io.mindspice.mindlib.data.wrappers.LazyFinalValue;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;


public class BiPredicateBiConsumerFlag<T, U> implements BiPredicate<T, U> {
    private final LazyFinalValue<Boolean> value = LazyFinalValue.of(false);
    private final BiPredicate<T, U> predicate;
    private final BiConsumer<T, U> consumer;

    public BiPredicateBiConsumerFlag(BiPredicate<T, U> predicate, BiConsumer<T, U> consumer) {
        this.predicate = predicate;
        this.consumer = consumer;
    }

    public static <T, U> BiPredicateBiConsumerFlag<T, U> of(BiPredicate<T, U> predicate, BiConsumer<T, U> consumer) {
        return new BiPredicateBiConsumerFlag<>(predicate, consumer);
    }

    public boolean confirmed() {
        return value.get();
    }

    public boolean test(T t, U u) {
        if (value.isFinal()) { return true; }

        if (predicate.test(t, u)) {
            value.setOrIgnore(true);
            return true;
        } else {
            return false;
        }
    }

}
