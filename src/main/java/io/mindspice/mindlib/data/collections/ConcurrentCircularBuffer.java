package io.mindspice.mindlib.data.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class ConcurrentCircularBuffer<T> implements Iterable<T> {
    private final ArrayList<T> buffer;
    private final int capacity;
    private int head;
    private int size;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public ConcurrentCircularBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new ArrayList<>(capacity);
        this.head = 0;
        this.size = 0;
    }

    public boolean isEmpty() {
        rwLock.readLock().lock();
        try {
            return size == 0;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public boolean isFull() {
        rwLock.readLock().lock();
        try {
            return size == capacity;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void add(T value) {
        rwLock.writeLock().lock();
        try {
            if (isFull()) {
                buffer.set(head, value);
                head = (head + 1) % capacity;
            } else {
                buffer.add(value);
                size++;
            }
        } finally {
            rwLock.writeLock().unlock();

        }
    }

    public T getFromOldest(int index) {
        rwLock.readLock().lock();
        try {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index out of bounds");
            }
            int actualIndex = (head + index) % capacity;
            return buffer.get(actualIndex);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public T getFromNewest(int index) {
        rwLock.readLock().lock();
        try {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index out of bounds");
            }
            int actualIndex = (head + size - 1 - index) % capacity;
            return buffer.get(actualIndex);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public int size() {
        rwLock.readLock().lock();
        try {
            return size;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public Iterator<T> iterator() {
        int currentHead;
        int currentSize;
        rwLock.readLock().lock();
        try {
            currentHead = head;
            currentSize = size;
        } finally {
            rwLock.readLock().unlock();
        }
        return new RollOffArrayIterator(buffer, currentHead, currentSize);
    }

    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    private class RollOffArrayIterator implements Iterator<T> {
        private final ArrayList<T> buffer;
        private final int currentHead;
        private final int currentSize;
        private int currentIdx;
        private int traversed;

        public RollOffArrayIterator(ArrayList<T> buffer, int currentHead, int currentSize) {
            this.buffer = buffer;
            this.currentHead = currentHead;
            this.currentSize = currentSize;
            this.currentIdx = currentHead;
            this.traversed = 0;
        }

        @Override
        public boolean hasNext() {
            return traversed < currentSize;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T value = buffer.get(currentIdx);
            currentIdx = (currentIdx + 1) % capacity;
            traversed++;
            return value;
        }
    }

    @Override
    public String toString() { return buffer.toString(); }


}


