package io.mindspice.mindlib.functional.predicates;

import io.mindspice.mindlib.data.wrappers.LazyFinalValue;


public class QuadPredicateFlag<T, U, V, W> implements QuadPredicate<T, U, V, W> {
    private final LazyFinalValue<Boolean> value = LazyFinalValue.of(false);
    private final QuadPredicate<T, U, V, W> predicate;

    public QuadPredicateFlag(QuadPredicate<T, U, V, W> predicate) {
        this.predicate = predicate;
    }

    public static <T, U, V, W> QuadPredicateFlag<T, U, V, W> of(QuadPredicate<T, U, V, W> predicate) {
        return new QuadPredicateFlag<>(predicate);
    }

    public boolean confirmed() {
        return value.get();
    }

    public boolean test(T t, U u, V v, W w) {
        if (predicate.test(t, u, v, w)) {
            value.setOrIgnore(true);
            return true;
        } else {
            return false;
        }
    }

}
