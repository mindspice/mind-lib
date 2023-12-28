package io.mindspice.mindlib.data.collections.sets;

import java.util.concurrent.atomic.AtomicIntegerArray;


public class AtomicBitSet {
    private final AtomicIntegerArray array;

    public AtomicBitSet(int length) {
        int intLength = (length + 31) / 32; // round up to nearest 32-bit boundary
        this.array = new AtomicIntegerArray(intLength);
    }

    private int index(long n) {
        return (int)(n / 32);
    }

    private int bit(long n) {
        return 1 << (n % 32);
    }

    public void set(long n, boolean value) {
        if (value) {
            set(n);
        } else {
            clear(n);
        }
    }

    public void set(long n) {
        int idx = index(n);
        int bit = bit(n);
        int oldValue, newValue;
        do {
            oldValue = this.array.get(idx);
            newValue = oldValue | bit;
        } while(!this.array.compareAndSet(idx, oldValue, newValue));
    }

    public void clear(long n) {
        int idx = index(n);
        int bit = bit(n);
        int oldValue, newValue;
        do {
            oldValue = this.array.get(idx);
            newValue = oldValue & ~bit;
        } while(!this.array.compareAndSet(idx, oldValue, newValue));
    }

    public boolean get(long n) {
        int idx = index(n);
        int bit = bit(n);
        return (this.array.get(idx) & bit) != 0;
    }
}
