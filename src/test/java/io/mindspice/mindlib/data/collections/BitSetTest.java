package io.mindspice.mindlib.data.collections;

import io.mindspice.mindlib.data.collections.sets.AtomicBitSet;
import io.mindspice.mindlib.data.collections.sets.ByteSet;
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

        for (int i = 1; i < 10_000; i += 2) {
            assert (!abs.get(i));
        }

        for (int i = 0; i < 10_000; i += 2) {
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

    @Test
    void byteSet() {
        var byteSet = new ByteSet(100);

        for (int i = 0; i < 100; ++i) {
            assert byteSet.get(i) == 0;
        }

        for (int i = 0; i < 100; ++i) {
            byteSet.increment(i);
        }

        for (int i = 0; i < 100; ++i) {
            assert byteSet.get(i) == 1;
        }

        for (int i = 0; i < 100; ++i) {
            byteSet.decrement(i);
        }

        for (int i = 0; i < 100; ++i) {
            assert byteSet.get(i) == 0;
        }

        for (int i = 1; i < 256; ++i) {
            for (int j = 0; j < 100; ++j) {
                byteSet.increment(j);
            }
            for (int j = 0; j < 100; ++j) {
                assert byteSet.get(j) == i;
            }
        }

        for (int i = 254; i >= 0; --i) {
            for (int j = 0; j < 100; ++j) {
                byteSet.decrement(j);
            }
            for (int j = 0; j < 100; ++j) {

                assert byteSet.get(j) == i;
            }
        }

        for (int i = 0; i < 256; ++i) {
            byteSet.set(i, i);
        }
        for (int i = 0; i < 256; ++i) {
            assert byteSet.get(i) == i;
        }

        for (int i = 254; i >= 0; --i) {
            byteSet.set(i, i);
        }
        for (int i = 254; i >= 0; --i) {
            assert byteSet.get(i) == i;
        }


    }


}
