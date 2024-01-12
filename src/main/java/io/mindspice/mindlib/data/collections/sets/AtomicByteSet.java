package io.mindspice.mindlib.data.collections.sets;

import io.mindspice.mindlib.util.MUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;


public class AtomicByteSet {
    private byte[] byteSet;
    private final StampedLock lock = new StampedLock();

    public AtomicByteSet(int size) {
        this.byteSet = new byte[size];
    }

    public int get(int index) {
        long stamp = -1;
        int value = -1;
        do {
            stamp = lock.tryOptimisticRead();
            Objects.checkIndex(index, byteSet.length);
            value = MUtils.byteToUnsignedInt(byteSet[index]);
        } while (!lock.validate(stamp));
        return value;
    }

    public void set(int index, int value) {
        long stamp = lock.writeLock();
        try {
            if (index > byteSet.length - 1) {
                growArray(index);
            }
            byteSet[index] = intCast(value);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public int increment(int index) {
        long stamp = lock.writeLock();
        try {
            if (index > byteSet.length - 1) {
                growArray(index);
                byteSet[index] = intCast(1);
                return 1;
            }
            int currentValue = MUtils.byteToUnsignedInt(byteSet[index]);
            byteSet[index] = intCast(currentValue += 1);
            return currentValue;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public int decrement(int index) {
        long stamp = lock.writeLock();
        try {
            if (index > byteSet.length - 1) {
                growArray(index);
                byteSet[index] = intCast(0);
                return 0;
            }
            int currentValue = MUtils.byteToUnsignedInt(byteSet[index]);
            if (currentValue == 0) { return 0; }

            byteSet[index] = intCast(currentValue -= 1);
            return currentValue;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    private void growArray(int newLength) {
        if (newLength > byteSet.length - 1) {
            byteSet = Arrays.copyOf(byteSet, newLength + 1);
        }
    }

    private byte intCast(int i) {
        if (i < 0 || i > 255) {
            throw new IllegalArgumentException("Value must be between 0 and 255");
        }
        return (byte) i;
    }

    public void clear() {
        long stamp = lock.writeLock();
        try {
            Arrays.fill(byteSet, (byte) 0);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public boolean isNonZero(int index) {
        long stamp = -1;
        boolean value;
        do {
            stamp = lock.tryOptimisticRead();
            value = byteSet[index] != 0;
        } while (!lock.validate(stamp));
        return value;
    }
}
