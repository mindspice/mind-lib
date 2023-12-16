package io.mindspice.mindlib.util;

import io.mindspice.mindlib.data.collections.functional.LazyZipList;
import io.mindspice.mindlib.data.tuples.Pair;

import java.util.*;
import java.util.function.BiFunction;


public class FuncUtils {
    public static <A, B, C> List<C> zipOf(List<A> listA, List<B> listB, BiFunction<A, B, C> zipFunction) {
        int size = Math.min(listA.size(), listB.size());
        List<C> result = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            result.add(zipFunction.apply(listA.get(i), listB.get(i)));
        }
        return Collections.unmodifiableList(result);
    }

    public static <A, B, C> LazyZipList<A, B, C> lazyZipOf(List<A> listA, List<B> listB, BiFunction<A, B, C> zipFunction) {
        return new LazyZipList<A, B, C>(listA, listB, zipFunction);
    }

    public static <A, B> List<Pair<A, B>> crossJoinOf(List<A> listA, List<B> listB) {
        List<Pair<A, B>> result = new ArrayList<>();
        for (A a : listA) {
            for (B b : listB) {
                result.add(new Pair<>(a, b));
            }
        }
        return result;
    }

    public static <T, U> List<U> scanOf(List<T> list, U initial, BiFunction<U, T, U> accumulator) {
        List<U> result = new ArrayList<>();
        U acc = initial;
        result.add(acc);
        for (T element : list) {
            acc = accumulator.apply(acc, element);
            result.add(acc);
        }
        return result;
    }

    public static <T> List<Set<T>> dualCombinationSetOf(Set<T> originalSet) {
        List<Set<T>> combinations = new ArrayList<>();
        List<T> originalList = new ArrayList<>(originalSet);

        for (int i = 0; i < originalList.size(); i++) {
            for (int j = i + 1; j < originalList.size(); j++) {
                Set<T> newSet = new HashSet<>();
                newSet.add(originalList.get(i));
                newSet.add(originalList.get(j));
                combinations.add(newSet);
            }
        }
        return combinations;
    }
    public static <T> Set<Set<T>> powerSetOf(Set<T> originalSet) {
        Set<Set<T>> sets = new HashSet<>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<>());
            return sets;
        }
        List<T> list = new ArrayList<>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<>(list.subList(1, list.size()));
        for (Set<T> set : powerSetOf(rest)) {
            Set<T> newSet = new HashSet<>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

    @FunctionalInterface
    public interface SupplierWithExceptions<T> {
        T get() throws Exception;
    }

    public static <T> T defaultOnExcept(SupplierWithExceptions<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static <T> T nullOnExcept(SupplierWithExceptions<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return null;
        }
    }

    public static class UtilityFunc {
    }
}
