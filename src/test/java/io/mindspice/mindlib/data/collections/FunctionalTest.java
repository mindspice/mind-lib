package io.mindspice.mindlib.data.collections;

import io.mindspice.mindlib.functional.predicates.BiPredicateFlag;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.BiPredicate;


public class FunctionalTest {
    @Test
    public void predicateFlagTest() {
        BiPredicate<Integer, Integer> pred = (i1, i2) -> Objects.equals(i1, i2);
        BiPredicateFlag<Integer, Integer> bf = BiPredicateFlag.of(pred);
        bf.test(2,3);
        assert !bf.confirmed();

        bf.test(2,2);
        assert bf.confirmed();
    }

}
