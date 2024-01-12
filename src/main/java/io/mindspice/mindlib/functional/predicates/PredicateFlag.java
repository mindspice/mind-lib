package io.mindspice.mindlib.functional.predicates;

import io.mindspice.mindlib.data.wrappers.LazyFinalValue;

import java.util.function.BiPredicate;
import java.util.function.Predicate;


public class PredicateFlag<T> implements Predicate<T> {
    private final LazyFinalValue<Boolean> value = LazyFinalValue.of(false);
    private final Predicate<T> predicate;

    public PredicateFlag(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    public static <T> PredicateFlag<T> of(Predicate<T> predicate) {
        return new PredicateFlag<>(predicate);
    }

    public boolean confirmed() {
        return value.get();
    }

    public boolean test(T t) {
        if (predicate.test(t)) {
            value.setOrIgnore(true);
            return true;
        } else {
            return false;
        }
    }

}
