package io.mindspice.mindlib.data.cache;

import io.mindspice.mindlib.data.collections.maps.IndexMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;


public class ConcurrentIndexCache<T> {
    private final IndexMap<T> elements;
    private final int skipFactor;
    private int size;
    private int nextIndex;
    private final StampedLock lock = new StampedLock();

    // Extra field for timestamping/invalidating
    private long[] timeStamps;
    private final boolean isTimeStamped;
    private final List<Integer> openIndexes;

    public ConcurrentIndexCache(int initialSize, boolean isTimeStamped) {
        this.isTimeStamped = isTimeStamped;
        elements = new IndexMap<>(initialSize);
        if (isTimeStamped) {
            timeStamps = new long[initialSize];

        } else {
            timeStamps = null;
        }
        openIndexes = new ArrayList<>(initialSize / 20);
        size = initialSize;
        skipFactor = 1;
    }

    public ConcurrentIndexCache(int initialSize, int skipFactor, boolean isTimeStamped) {
        this.isTimeStamped = isTimeStamped;
        elements = new IndexMap<>(initialSize);
        if (isTimeStamped) {
            timeStamps = new long[initialSize];
        } else {
            timeStamps = null;
        }
        openIndexes = new ArrayList<>(initialSize / 20);
        size = initialSize;
        this.skipFactor = skipFactor;
    }

    public int getNextIndex() {
        long stamp = lock.writeLock();
        try {
            return nextIndex;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public int put(T item) {
        long stamp = lock.writeLock();
        int insertIndex = nextIndex;
        try {
            if (!openIndexes.isEmpty()) {
                insertIndex = openIndexes.removeLast();
            }
            boolean wasResized = elements.put(insertIndex, item);
            if (isTimeStamped) {
                if (wasResized) {
                    timeStamps = Arrays.copyOf(timeStamps, elements.size());
                }
                timeStamps[insertIndex] = System.currentTimeMillis();
            }
            if (insertIndex == nextIndex) {
                nextIndex += skipFactor;
            }
        } finally {
            lock.unlockWrite(stamp);
        }
        return insertIndex;
    }

    public void remove(int index) {
        long stamp = lock.writeLock();
        try {
            elements.remove(index);
            openIndexes.add(index);
            if (isTimeStamped) {
                timeStamps[index] = Long.MAX_VALUE;
            }
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public T removeAndGet(int index) {
        long stamp = lock.writeLock();
        T item = null;
        try {
            item = elements.removeAndGet(index);
            openIndexes.add(index);
            if (isTimeStamped) {
                timeStamps[index] = Long.MAX_VALUE;
            }
        } finally {
            lock.unlockWrite(stamp);
        }
        return item;
    }

    public void remove(T item) {
        long stamp = lock.writeLock();
        try {
            int rIndex = elements.remove(item);
            if (rIndex != -1) {
                openIndexes.add(rIndex);
            }
            if (isTimeStamped) {
                timeStamps[rIndex] = Long.MAX_VALUE;
            }
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public T get(int index) {
        long stamp = lock.tryOptimisticRead();
        T element = elements.get(index);
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                element = elements.get(index);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return element;
    }

    public int getSize() {
        long stamp = lock.tryOptimisticRead();
        int currSize = size;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                currSize = size;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return currSize;
    }

    public List<T> invalidateAndGet(long ageDelta) {
        List<T> removals = new ArrayList<>(size / 20);
        if (!isTimeStamped) {
            throw new UnsupportedOperationException("Cache was not initialized as timestamped");
        }
        long cutOffTime = System.currentTimeMillis() - ageDelta;
        long stamp = lock.writeLock();
        try {
            for (int i = 0; i < size; ++i) {
                if (timeStamps[i] < cutOffTime) {
                    removals.add(elements.removeAndGet(i));
                    timeStamps[i] = Long.MAX_VALUE;
                    openIndexes.add(i);
                }
            }
        } finally {
            lock.unlockWrite(stamp);
        }
        return removals;
    }

    public void invalidate(long ageDelta) {
        if (!isTimeStamped) {
            throw new UnsupportedOperationException("Cache was not initialized as timestamped");
        }
        long cutOffTime = System.currentTimeMillis() - ageDelta;
        long stamp = lock.writeLock();
        try {
            for (int i = 0; i < size; ++i) {
                if (timeStamps[i] < cutOffTime) {
                    timeStamps[i] = Long.MAX_VALUE;
                    openIndexes.add(i);
                }
            }
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public void removeIf(Predicate<T> predicate) {
        long stamp = lock.writeLock();
        try {
            elements.removeIf(predicate);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * Performs a consumer operation on all non-null elements of the array
     * WARNING: No visibility, or thread safety guarantees can be made,
     * thread safety is dependent on the objects own guarantees.
     */
    public void forEach(Consumer<T> consumer) {
        long stamp = lock.readLock();
        try {
            elements.forEach(consumer);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * Returns a stream backed by the internal array.
     * WARNING: A concurrent removal from another thread during streaming
     * may produce a null pointer exception.
     */
    public Stream<T> stream() {
        long stamp = lock.tryOptimisticRead();
        Stream<T> stream = elements.stream();
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                stream = elements.stream();
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return stream;
    }
}
