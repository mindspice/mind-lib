package io.mindspice.mindlib.data.wrappers;

public class LazyFinalValue<T> {
    private T value;
    private volatile boolean isSet = false;

    public LazyFinalValue(T initialValue) { value = initialValue; }

    public LazyFinalValue() { this.value = null; }

    public synchronized void set(T value) {
        if (isSet) {
            throw new IllegalStateException("Value is already set");
        }
        this.value = value;
        this.isSet = true;
    }

    public boolean isFinal() { return isSet; }

    public T get() { return value; }

    public T getOrThrow() {
        if (!isSet) { throw new IllegalStateException("Value has not been set"); }
        return value;
    }

    public static <T> LazyFinalValue<T> of(T value) { return new LazyFinalValue<>(value); }
}
