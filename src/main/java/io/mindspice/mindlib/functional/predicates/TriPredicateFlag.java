package io.mindspice.mindlib.functional.predicates;

import io.mindspice.mindlib.data.wrappers.LazyFinalValue;

import java.util.function.BiPredicate;


public class TriPredicateFlag<T, U, V> implements TriPredicate<T, U, V> {
    private final LazyFinalValue<Boolean> value = LazyFinalValue.of(false);
    private final TriPredicate<T, U, V> predicate;

    public TriPredicateFlag(TriPredicate<T, U, V> predicate) {
        this.predicate = predicate;
    }

    public static <T, U, V> TriPredicateFlag<T, U, V> of(TriPredicate<T, U, V> predicate) {
        return new TriPredicateFlag<>(predicate);
    }

    public boolean confirmed() {
        return value.get();
    }

    public boolean test(T t, U u, V v) {
        if (predicate.test(t, u, v)) {
            value.setOrIgnore(true);
            return true;
        } else {
            return false;
        }
    }

}
