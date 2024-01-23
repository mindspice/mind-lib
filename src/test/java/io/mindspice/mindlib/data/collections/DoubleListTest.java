package io.mindspice.mindlib.data.collections;


import io.mindspice.mindlib.data.collections.lists.primative.DoubleList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class DoubleListTest {

    @Test
    void size() {
        var d = new DoubleList();
        d.addAll(List.of(0d, 34d, 43d, 2d, 34d, 32d));
        assertEquals(d.size(), 6);
        d.removeAtIndex(0);
        assertEquals(d.size(), 5);
        d.add(23);
        d.add(0.45234);
        assertEquals(d.size(), 7);
        d.clear();
        assertEquals(d.size(), 0);
    }

    @org.junit.jupiter.api.Test
    void isEmpty() {

    }

    @org.junit.jupiter.api.Test
    void contains() {
    }

    @org.junit.jupiter.api.Test
    void toArray() {
    }

    @org.junit.jupiter.api.Test
    void forEach() {
    }

    @org.junit.jupiter.api.Test
    void iterator() {
    }

    @org.junit.jupiter.api.Test
    void add() {
    }

    @org.junit.jupiter.api.Test
    void get() {
    }

    @org.junit.jupiter.api.Test
    void addAll() {
    }

    @org.junit.jupiter.api.Test
    void testAddAll() {
    }

    @org.junit.jupiter.api.Test
    void testAddAll1() {
    }

    @org.junit.jupiter.api.Test
    void remove() {
    }

    @org.junit.jupiter.api.Test
    void clear() {
    }

    @org.junit.jupiter.api.Test
    void testHashCode() {
    }

    @org.junit.jupiter.api.Test
    void testEquals() {
    }
}