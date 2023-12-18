package io.mindspice.mindlib.data.collections.other;

import java.util.Objects;


public class GridArray<T> {
    private final T[] grid;
    private final int width;
    private final int height;
    private final int size;

    @SuppressWarnings("unchecked")
    public GridArray(int width, int height) {
        this.width = width;
        this.height = height;
        size = width * height;
        grid = (T[]) new Object[width * height];
    }

    @SuppressWarnings("unchecked")
    public T get(int x, int y) {
        return (T) grid[y * width + x];
    }

    public void set(int x, int y, T value) {
        grid[y * width + x] = value;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    public T[] backingArray() {
        return grid;
    }
}

