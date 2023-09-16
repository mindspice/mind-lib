package io.mindspice.mindlib.functional.consumers;

@FunctionalInterface
public interface QuadConsumer<T, U, V, X> {
    void accept(T t, U u, V v, X x);
}

