package io.mindspice.mindlib.data.wrappers;

public class ImmutableAfterSet<T> {
    private T value;
    private boolean isSet = false;

    public ImmutableAfterSet(T initialValue) { value = initialValue; }

    public synchronized void set(T value) {
        if (isSet) {
            throw new IllegalStateException("Value is already set");
        }
        this.value = value;
        this.isSet = true;
    }

    public T getValue() {
        return value;
    }
}
