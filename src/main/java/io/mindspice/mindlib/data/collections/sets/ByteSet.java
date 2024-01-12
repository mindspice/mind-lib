package io.mindspice.mindlib.data.collections.sets;

import io.mindspice.mindlib.util.MUtils;

import java.util.Arrays;
import java.util.Objects;


public class ByteSet {
    private byte[] byteSet;

    public ByteSet(int size) {
        this.byteSet = new byte[size];
    }

    public int get(int index) {
        Objects.checkIndex(index, byteSet.length);
        return MUtils.byteToUnsignedInt(byteSet[index]);
    }

    public void set(int index, int value) {
        if (index > byteSet.length - 1) {
            growArray(index);
        }
        byteSet[index] = intCast(value);
    }

    public int increment(int index) {
        if (index > byteSet.length - 1) {
            growArray(index);
            byteSet[index] = intCast(1);
            return 1;
        }
        int currentValue = MUtils.byteToUnsignedInt(byteSet[index]);
        byteSet[index] = intCast(currentValue += 1);
        return currentValue;
    }

    public int decrement(int index) {
        if (index > byteSet.length - 1) {
            growArray(index);
            byteSet[index] = intCast(0);
            return 0;
        }
        int currentValue = MUtils.byteToUnsignedInt(byteSet[index]);
        if (currentValue == 0) { return 0; }

        byteSet[index] = intCast(currentValue -= 1);
        return currentValue;
    }

    public void growArray(int newLength) {
        ;
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
        Arrays.fill(byteSet, (byte) 0);
    }

    public boolean isNonZero(int index) {
        return byteSet[index] != 0;
    }
}
