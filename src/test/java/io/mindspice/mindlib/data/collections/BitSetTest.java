package io.mindspice.mindlib.data.collections;

import io.mindspice.mindlib.data.collections.sets.AtomicBitSet;
import org.junit.jupiter.api.Test;


public class BitSetTest {


    @Test
    void atomicBitsetTest() {
        var abs = new AtomicBitSet(10_000);

        for (int i = 0; i < 10_000; ++i) {
            if (i % 2 == 0) {
                abs.set(i);
            }
        }

        for (int i = 1; i < 10_000; i +=2){
            assert (!abs.get(i));
        }

        for (int i = 0; i < 10_000; i +=2){
            assert (abs.get(i));
        }

        for (int i = 0; i < 10_000; ++i) {
            if (i % 2 == 0) {
                abs.set(i, false);
            }
        }


        for (int i = 0; i < 10_000; ++i) {

            assert (!abs.get(i));
        }
    }
}
