package io.mindspice.mindlib.data.geometry;

public record QuadItemId<T>(int id, IMutVector2 position, T item) {
    @Override
    public String toString() {
        return "Id: " + id + ", Position: " + position + ", Item: " + item;
    }
}