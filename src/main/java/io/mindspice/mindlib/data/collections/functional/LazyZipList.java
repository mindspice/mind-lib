package io.mindspice.mindlib.data.collections.functional;

import io.mindspice.mindlib.util.FuncUtils;

import java.util.*;
import java.util.function.BiFunction;


public class LazyZipList<A, B, C> implements List<C>, Collection<C> {
    private final List<A> listA;
    private final List<B> listB;
    private final int size;
    private final BiFunction<A, B, C> biFunction;

    public LazyZipList(List<A> listA, List<B> listB, BiFunction<A, B, C> biFunction) {
        this.listA = listA;
        this.listB = listB;
        size = Math.min(listA.size(), listB.size());
        this.biFunction = biFunction;
    }

    public int size() { return size; }

    @Override
    public boolean isEmpty() {
        return listA.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return listA.contains(o) || listB.contains(o);
    }

    @Override
    public Iterator<C> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return FuncUtils.zipOf(listA, listB, biFunction).toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(C c) {
        throw new UnsupportedOperationException("Mutation not supported for zip list");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Mutation not supported for zip list");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object elem : c) {
            boolean found = false;
            for (int i = 0; i < size; i++) {
                if (biFunction.apply(listA.get(i), listB.get(i)).equals(elem)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends C> c) {
        throw new UnsupportedOperationException("Mutation not supported for zip list");
    }

    @Override
    public boolean addAll(int index, Collection<? extends C> c) {
        throw new UnsupportedOperationException("Mutation not supported for zip list");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Mutation not supported for zip list");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Mutation not supported for zip list");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Mutation not supported for zip list");

    }

    public C get(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return biFunction.apply(listA.get(index), listB.get(index));
    }

    @Override
    public C set(int index, C element) {
        throw new UnsupportedOperationException("Mutation not supported for zip list");
    }

    @Override
    public void add(int index, C element) {
        throw new UnsupportedOperationException("Mutation not supported for zip list");

    }

    @Override
    public C remove(int index) {
        throw new UnsupportedOperationException("Mutation not supported for zip list");
    }

    @Override
    public int indexOf(Object o) {
        int a = listA.indexOf(o);
        int b = listB.indexOf(o);
        if (a != -1) { return a; }
        return b;
    }

    @Override
    public int lastIndexOf(Object o) {
        int a = listA.lastIndexOf(o);
        int b = listB.lastIndexOf(o);
        if (a != -1) { return a; }
        return b;
    }

    @Override
    public ListIterator<C> listIterator() {
        throw new UnsupportedOperationException("Not implemented");

    }

    @Override
    public ListIterator<C> listIterator(int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<C> subList(int fromIndex, int toIndex) {
        return FuncUtils.zipOf(listA.subList(fromIndex, toIndex), listB.subList(fromIndex, toIndex), biFunction);
    }

    public BiFunction<A, B, C> getFunction() { return biFunction; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        LazyZipList<?, ?, ?> lazyZipList = (LazyZipList<?, ?, ?>) o;
        return size == lazyZipList.size
                && listA.equals(lazyZipList.listA)
                && listB.equals(lazyZipList.listB)
                && biFunction.equals(lazyZipList.biFunction
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(listA, listB, size, biFunction);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LazyZip: ");
        sb.append("\n  listAClone: ").append(listA);
        sb.append(",\n  listBClone: ").append(listB);
        sb.append(",\n  size: ").append(size);
        sb.append(",\n  biFunction: ").append(biFunction);
        sb.append("\n");
        return sb.toString();
    }
}