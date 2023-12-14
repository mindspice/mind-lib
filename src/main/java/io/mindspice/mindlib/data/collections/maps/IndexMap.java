package io.mindspice.mindlib.data.collections.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Consumer;
import java.util.stream.Stream;


public class IndexMap<T> {
    private T[] elements;
    private int size;

    @SuppressWarnings("unchecked")
    public IndexMap(int initialSize) {
        elements = (T[]) new Object[initialSize];
        this.size = initialSize;
    }

    public boolean put(int idx, T item) {
        boolean reSized = false;
        if (idx > size - 1) {
            size = (int) (idx * 1.1) + 1;
            elements = Arrays.copyOf(elements, size);
        }
        elements[idx] = item;
        return reSized;
    }

    public T get(int idx) {
        if (idx > size || idx < 0) {
            return null;
        }
        return elements[idx];
    }

    public void clear() {
        Arrays.fill(elements, null);
    }

    public void clearAndResize(int newSize) {
        size = newSize;
        elements = Arrays.copyOf(elements, newSize);
        Arrays.fill(elements, null);
    }

    public void remove(int idx) {
        if (idx >= size || idx < 0) {
            return;
        }
        elements[idx] = null;
    }

    public T removeAndGet(int idx) {
        if (idx >= size || idx < 0) {
            return null;
        }
        T item = elements[idx];
        elements[idx] = null;
        return item;
    }

    public int remove(T item) {
        for (int i = 0; i < size; ++i) {
            if (Objects.equals(item, elements[i])) {
                elements[i] = null;
                return i;
            }
        }
        return -1;
    }

    public int size() {
        return size;
    }

    public void resize(int newSize) {
        size = newSize;
        elements = Arrays.copyOf(elements, newSize);
    }

    public boolean contains(T item) {
        for (T element : elements) {
            if (Objects.equals(item, element)) {
                return true;
            }
        }
        return false;
    }

    public int indexOf(T item) {
        for (int i = 0; i < size; i++) {
            if (item == null ? elements[i] == null : item.equals(elements[i])) {
                return i;
            }
        }
        return -1;
    }

    public T[] toArray() {
        return Arrays.copyOf(elements, size);
    }

    /**
     * Returns the internal backing array.
     * WARNING: Modifying this array directly can lead to unpredictable behavior.
     */
    public T[] getBackingArray() {
        return elements;
    }

    public List<Integer> getOpenIndexes() {
        List<Integer> openIndexes = new ArrayList<>(size / 10);
        for (int i = 0; i < size; i++) {
            if (elements[i] == null) {
                openIndexes.add(i);
            }
        }
        return openIndexes;
    }

    public void removeIf(Predicate<T> predicate) {
        for (int i = 0; i < size; i++) {
            if (elements[i] != null && predicate.test(elements[i])) {
                elements[i] = null;
            }
        }
    }

    public List<Integer> removeIfWithIndexes(Predicate<T> predicate) {
        List<Integer> openIndexes = new ArrayList<>(size / 10);
        for (int i = 0; i < size; i++) {
            if (elements[i] != null && predicate.test(elements[i])) {
                elements[i] = null;
                openIndexes.add(i);
            }
        }
        return openIndexes;
    }

    public void forEach(Consumer<T> action) {
        for (T element : elements) {
            if (element != null) {
                action.accept(element);
            }
        }
    }

    public Stream<T> stream() {
        return Arrays.stream(elements, 0, size)
                .filter(Objects::nonNull);
    }
}
