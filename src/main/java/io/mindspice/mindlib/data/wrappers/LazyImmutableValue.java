package io.mindspice.mindlib.data.wrappers;

public class LazyImmutableValue<T> {
    private T value;
    private volatile boolean isSet = false;

    public LazyImmutableValue(T initialValue) { value = initialValue; }

    public LazyImmutableValue() { this.value = null; }

    public synchronized void set(T value) {
        if (isSet) {
            throw new IllegalStateException("Value is already set");
        }
        this.value = value;
        this.isSet = true;
    }

    public boolean isSet() { return isSet; }

    public T get() { return value; }

    public T orElseThrowIfUnset() {
        if (!isSet) { throw new IllegalStateException("Value has not been set"); }
        return value;
    }

    public static <T> LazyImmutableValue<T> of(T value) { return new LazyImmutableValue<>(value); }
}
