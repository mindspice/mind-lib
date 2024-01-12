package io.mindspice.mindlib.data.wrappers;

public class MutableValue<T> {
    T value;

    public MutableValue(T value) {
        this.value = value;
    }

    public static <T> MutableValue<T> of(T value) {
        return new MutableValue<>(value);
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
