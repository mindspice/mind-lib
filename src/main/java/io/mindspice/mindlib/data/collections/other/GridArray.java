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
        return  grid[y * width + x];
    }

    @SuppressWarnings("unchecked")
    public T getFlat(int i) {
        return  grid[i];
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getFlatSize() {
        return grid.length;
    }



}

