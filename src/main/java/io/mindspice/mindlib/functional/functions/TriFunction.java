package io.mindspice.mindlib.functional.functions;

import java.util.function.Function;


public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}