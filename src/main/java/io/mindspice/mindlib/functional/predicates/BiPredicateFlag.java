package io.mindspice.mindlib.functional.predicates;

import io.mindspice.mindlib.data.wrappers.LazyFinalValue;
import io.mindspice.mindlib.data.wrappers.MutableBoolean;

import java.util.function.BiPredicate;
import java.util.function.Predicate;


public class BiPredicateFlag<T, U> implements BiPredicate<T, U> {
    private final MutableBoolean value = MutableBoolean.of(false);
    private final BiPredicate<T, U> predicate;

    public BiPredicateFlag(BiPredicate<T, U> predicate) {
        this.predicate = predicate;
    }

    public static <T, U> BiPredicateFlag<T, U> of(BiPredicate<T, U> predicate) {
        return new BiPredicateFlag<>(predicate);
    }

    public boolean confirmed() {
        return value.get();
    }

    public boolean test(T t, U u) {
        if (predicate.test(t, u)) {
            value.setTrue();
            return true;
        } else {
            return false;
        }
    }

    public void reset() {
        value.setFalse();
    }

}
