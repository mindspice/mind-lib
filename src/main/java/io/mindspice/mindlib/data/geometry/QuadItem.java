package io.mindspice.mindlib.data.geometry;

public record QuadItem<T>(IMutVector2 position, T item) {
    @Override
    public String toString() {
        return "Position: " + position + ", Item: " + item;
    }
}