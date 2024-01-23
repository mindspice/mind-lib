package io.mindspice.mindlib.data.collections.lists.primative;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.LongConsumer;


public class LongList {
    private long[] dataElements;
    private int size = 0;

    public LongList(int initialSize) {
        dataElements = new long[initialSize];
    }

    public LongList(List<Long> longList) {
        size = longList.size();
        dataElements = new long[size];
        for (int i = 0; i < size; ++i) {
            dataElements[i] = longList.get(i);
        }
    }

    public LongList(long[] longList) {
        size = longList.length;
        dataElements = Arrays.copyOf(longList, size * 2); // Initialize with double capacity
    }

    public LongList() {
        dataElements = new long[10];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(long val) {
        for (int i = 0; i < size; ++i) {
            if (dataElements[i] == val) { return true; }
        }
        return false;
    }

    public long[] toArray() {
        return Arrays.copyOf(dataElements, size);
    }

    public void forEach(LongConsumer action) {
        for (int i = 0; i < size; i++) {
            action.accept(dataElements[i]);
        }
    }

    public LongIterator iterator() {
        return new LongIterator();
    }

    private class LongIterator {
        private int index = 0;

        public boolean hasNext() {
            return index < size;
        }

        public long next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return dataElements[index++];
        }
    }

    public boolean add(long i) {
        if (dataElements.length == size) {
            int newCapacity = dataElements.length * 2;  // Doubling the current capacity
            dataElements = Arrays.copyOf(dataElements, newCapacity);
        }
        dataElements[size++] = i;
        return true;
    }

    public long get(int index) {
        if (index < 0 || index > size - 1) { throw new ArrayIndexOutOfBoundsException(); }
        return dataElements[index];
    }

    public boolean addAll(List<Long> list) {
        int requiredCapacity = size + list.size();
        if (dataElements.length < requiredCapacity) {
            int newCapacity = Math.max(dataElements.length * 2, requiredCapacity);
            dataElements = Arrays.copyOf(dataElements, newCapacity);
        }
        for (long val : list) {
            dataElements[size++] = val;
        }
        return true;
    }

    public boolean addAll(LongList list) {
        int requiredCapacity = size + list.size();
        if (dataElements.length < requiredCapacity) {
            int newCapacity = Math.max(dataElements.length * 2, requiredCapacity);
            dataElements = Arrays.copyOf(dataElements, newCapacity);
        }
        for (int i = 0; i < list.size; ++i) {
            dataElements[size++] = list.dataElements[i];
        }
        return true;
    }

    public boolean addAll(long[] arr) {
        int requiredCapacity = size + arr.length;
        if (dataElements.length < requiredCapacity) {
            int newCapacity = Math.max(dataElements.length * 2, requiredCapacity);
            dataElements = Arrays.copyOf(dataElements, newCapacity);
        }

        for (int i = 0; i < arr.length; ++i) {
            dataElements[size++] = arr[i];
        }
        return true;
    }

    public boolean removeAllValuesOf(long value) {
        boolean found = false;
        for (int i = 0; i < size; ++i) {
            if (dataElements[i] == value) {
                found = true;
                removeAtIndex(i);
            }
        }
        return found;
    }

    public boolean removeFirstValueOf(long value) {
        for (int i = 0; i < size; ++i) {
            if (dataElements[i] == value) {
                removeAtIndex(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeAtIndex(int index) {
        if (index >= size || index < 0) { return false; }
        System.arraycopy(dataElements, index + 1, dataElements, index, size - index - 1);
        size--;
        return true;
    }

    public void clear() {
        dataElements = new long[10];
        size = 0;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < size; i++) {
            long val = dataElements[i];
            result = 31 * result + (int) (val ^ (val >>> 32));
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null || getClass() != obj.getClass()) { return false; }

        LongList other = (LongList) obj;
        if (size != other.size) { return false; }

        for (int i = 0; i < size; i++) {
            if (dataElements[i] != other.dataElements[i]) {
                return false;
            }
        }
        return true;
    }

}
