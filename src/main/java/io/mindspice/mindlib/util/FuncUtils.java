package io.mindspice.mindlib.util;

import io.mindspice.mindlib.data.collections.functional.LazyZipList;

import java.util.*;
import java.util.function.BiFunction;


public class FuncUtils {
    public static <A, B, C> List<C> zipOf(List<A> listA, List<B> listB, BiFunction<A, B, C> biFunction) {
        int size = Math.min(listA.size(), listB.size());
        List<C> result = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            result.add(biFunction.apply(listA.get(i), listB.get(i)));
        }
        return Collections.unmodifiableList(result);
    }

    public static <A, B, C> LazyZipList<A, B, C> lazyZipOf(List<A> listA, List<B> listB, BiFunction<A, B, C> biFunction) {
        return new LazyZipList<A, B, C>(listA, listB, biFunction);
    }
}
