package io.mindspice.mindlib.data;

public record Triple<U, V, W>(U first, V second, W third) {
    public static <U, V, W> Triple<U, V, W> of(U obj1, V obj2, W obj3) {
        return new Triple<>(obj1, obj2, obj3);
    }
}
