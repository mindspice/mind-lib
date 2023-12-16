package io.mindspice.mindlib.data.collections.other;

import java.util.Objects;


public class GridArray<T> {
    private final Object[] grid;
    private final int width;
    private final int height;
    private final int size;

    @SuppressWarnings("unchecked")
    public GridArray(int width, int height) {
        this.width = width;
        this.height = height;
        size = width * height;
        grid = new Object[width * height];
    }

    @SuppressWarnings("unchecked")
    public T get(int x, int y) {
        Objects.checkIndex(y * width + x, size);
        return (T) grid[y * width + x];
    }

    public void set(int x, int y, T value) {
        Objects.checkIndex(y * width + x, size);
        grid[y * width + x] = value;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

