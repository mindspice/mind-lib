package io.mindspice.mindlib.data.collections.lists.primative;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.DoubleConsumer;


public class DoubleList {
    private double[] dataElements;
    private int size = 0;

    public DoubleList(int initialSize) {
        dataElements = new double[initialSize];
    }

    public DoubleList(List<Double> doubleList) {
        size = doubleList.size();
        dataElements = new double[size];
        for (int i = 0; i < size; ++i) {
            dataElements[i] = doubleList.get(i);
        }
    }

    public DoubleList(double[] doubleList) {
        size = doubleList.length;
        dataElements = Arrays.copyOf(doubleList, size * 2); // Initialize with double capacity
    }

    public DoubleList() {
        dataElements = new double[10];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(double val) {
        for (int i = 0; i < size; ++i) {
            if (dataElements[i] == val) return true;
        }
        return false;
    }

    public double[] toArray() {
        return Arrays.copyOf(dataElements, size);
    }

    public void forEach(DoubleConsumer action) {
        for (int i = 0; i < size; i++) {
            action.accept(dataElements[i]);
        }
    }

    public DoubleIterator iterator() {
        return new DoubleIterator();
    }

    private class DoubleIterator {
        private int index = 0;

        public boolean hasNext() {
            return index < size;
        }

        public double next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return dataElements[index++];
        }
    }

    public boolean add(double i) {
        if (dataElements.length == size) {
            int newCapacity = dataElements.length * 2;  // Doubling the current capacity
            dataElements = Arrays.copyOf(dataElements, newCapacity);
        }
        dataElements[size++] = i;
        return true;
    }

    public double get(int index) {
        if (index < 0 || index > size - 1) { throw new ArrayIndexOutOfBoundsException(); }
        return dataElements[index];
    }

    public boolean addAll(List<Double> list) {
        int requiredCapacity = size + list.size();
        if (dataElements.length < requiredCapacity) {
            int newCapacity = Math.max(dataElements.length * 2, requiredCapacity);
            dataElements = Arrays.copyOf(dataElements, newCapacity);
        }
        for (double val : list) {
            dataElements[size++] = val;
        }
        return true;
    }

    public boolean addAll(DoubleList list) {
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

    public boolean addAll(double[] arr) {
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

    public boolean remove(int index) {
        if (index >= size || index < 0) return false;
        System.arraycopy(dataElements, index + 1, dataElements, index, size - index - 1);
        size--;
        return true;
    }

    public void clear() {
        dataElements = new double[10];
        size = 0;
    }

    @Override
    public int hashCode() {
        long result = 1L;
        for (int i = 0; i < size; i++) {
            double val = dataElements[i];
            long bits = Double.doubleToLongBits(val);
            result = 31L * result + bits;
        }
        return (int) (result ^ (result >>> 32));
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        DoubleList other = (DoubleList) obj;
        if (size != other.size) return false;
        double epsilon = 1E-5f;

        for (int i = 0; i < size; i++) {
            if (Math.abs(dataElements[i] - other.dataElements[i]) > epsilon) {
                return false;
            }
        }
        return true;
    }


}
