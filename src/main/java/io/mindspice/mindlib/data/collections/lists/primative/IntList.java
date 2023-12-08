package io.mindspice.mindlib.data.collections.lists.primative;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.IntConsumer;


public class IntList {
    private int[] dataElements;
    private int size = 0;

    public IntList(int initialSize) {
        dataElements = new int[initialSize];
    }

    public IntList(List<Integer> integerList) {
        size = integerList.size();
        dataElements = new int[size];
        for (int i = 0; i < size; ++i) {
            dataElements[i] = integerList.get(i);
        }
    }

    public IntList(int[] intList) {
        size = intList.length;
        dataElements = Arrays.copyOf(intList, size * 2); // Initialize with double capacity
    }

    public IntList() {
        dataElements = new int[10];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(int val) {
        for (int i = 0; i < size; ++i) {
            if (dataElements[i] == val) return true;
        }
        return false;
    }

    public int[] toArray() {
        return Arrays.copyOf(dataElements, size);
    }

    public void forEach(IntConsumer action) {
        for (int i = 0; i < size; i++) {
            action.accept(dataElements[i]);
        }
    }

    public IntIterator iterator() {
        return new IntIterator();
    }

    private class IntIterator {
        private int index = 0;

        public boolean hasNext() {
            return index < size;
        }

        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return dataElements[index++];
        }
    }

    public boolean add(int i) {
        if (dataElements.length == size) {
            int newCapacity = dataElements.length * 2;  // Doubling the current capacity
            dataElements = Arrays.copyOf(dataElements, newCapacity);
        }
        dataElements[size++] = i;
        return true;
    }

    public int get(int index) {
        if (index < 0 || index > size -1) { throw new ArrayIndexOutOfBoundsException(); }
        return dataElements[index];
    }

    public boolean addAll(List<Integer> list) {
        int requiredCapacity = size + list.size();
        if (dataElements.length < requiredCapacity) {
            int newCapacity = Math.max(dataElements.length * 2, requiredCapacity);
            dataElements = Arrays.copyOf(dataElements, newCapacity);
        }
        for (int val : list) {
            dataElements[size++] = val;
        }
        return true;
    }


    public boolean addAll(IntList list) {
        int requiredCapacity = size + list.size();
        if (dataElements.length < requiredCapacity) {
            int newCapacity = Math.max(dataElements.length * 2, requiredCapacity);
            dataElements = Arrays.copyOf(dataElements, newCapacity);
        }
        for(int i = 0; i < list.size; ++i) {
            dataElements[size++] = list.dataElements[i];
        }
        return true;
    }

    public boolean addAll(int[] arr) {
        int requiredCapacity = size + arr.length;
        if (dataElements.length < requiredCapacity) {
            int newCapacity = Math.max(dataElements.length * 2, requiredCapacity);
            dataElements = Arrays.copyOf(dataElements, newCapacity);
        }

        for(int i = 0; i < arr.length; ++i) {
            dataElements[size++] = arr[i];
        }

        return true;
    }

    public boolean remove(int index) {
        if (index >= size || index < 0) return false;
        System.arraycopy(dataElements, index + 1, dataElements, index, size - index - 1);
        size--;
        return true;
    }

    public void clear() {
        dataElements = new int[10];
        size = 0;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < size; i++) {
            result = 31 * result + dataElements[i];
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        IntList other = (IntList) obj;
        if (size != other.size) return false;

        for (int i = 0; i < size; i++) {
            if (dataElements[i] != other.dataElements[i]) {
                return false;
            }
        }
        return true;
    }
}
