package io.mindspice.mindlib.data.collections.lists.primative;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.DoubleConsumer;



public class FloatList {
    private float[] dataElements;
    private int size = 0;

    public FloatList(int initialSize) {
        dataElements = new float[initialSize];
    }

    public FloatList(List<Float> floatList) {
        size = floatList.size();
        dataElements = new float[size];
        for (int i = 0; i < size; ++i) {
            dataElements[i] = floatList.get(i);
        }
    }

    public FloatList(float[] floatList) {
        size = floatList.length;
        dataElements = Arrays.copyOf(floatList, size * 2); // Initialize with double capacity
    }

    public FloatList() {
        dataElements = new float[10];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(float val) {
        for (int i = 0; i < size; ++i) {
            if (dataElements[i] == val) return true;
        }
        return false;
    }

    public float[] toArray() {
        return Arrays.copyOf(dataElements, size);
    }

    public void forEach(DoubleConsumer action) {
        for (int i = 0; i < size; i++) {
            action.accept(dataElements[i]);
        }
    }

    public FloatIterator iterator() {
        return new FloatIterator();
    }

    private class FloatIterator {
        private int index = 0;

        public boolean hasNext() {
            return index < size;
        }

        public float next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return dataElements[index++];
        }
    }

    public boolean add(float i) {
        if (dataElements.length == size) {
            int newCapacity = dataElements.length * 2;  // Doubling the current capacity
            dataElements = Arrays.copyOf(dataElements, newCapacity);
        }
        dataElements[size++] = i;
        return true;
    }

    public float get(int index) {
        if (index < 0 || index > size -1) { throw new ArrayIndexOutOfBoundsException(); }
        return dataElements[index];
    }


    public boolean addAll(List<Float> list) {
        int requiredCapacity = size + list.size();
        if (dataElements.length < requiredCapacity) {
            int newCapacity = Math.max(dataElements.length * 2, requiredCapacity);
            dataElements = Arrays.copyOf(dataElements, newCapacity);
        }
        for (float val : list) {
            dataElements[size++] = val;
        }
        return true;
    }

    public boolean addAll(FloatList list) {
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

    public boolean addAll(float[] arr) {
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
        dataElements = new float[10];
        size = 0;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < size; i++) {
            float val = dataElements[i];
            result = 31 * result + Float.floatToIntBits(val);
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        FloatList other = (FloatList) obj;
        if (size != other.size) return false;
        float epsilon = 1E-5f;

        for (int i = 0; i < size; i++) {
            if (Math.abs(dataElements[i] - other.dataElements[i]) > epsilon) {
                return false;
            }
        }
        return true;
    }


}
