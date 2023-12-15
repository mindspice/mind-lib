package io.mindspice.mindlib.data.geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.StampedLock;


public class IConcurrentQuadTree<T> {
    private final Node root;

    public IConcurrentQuadTree(IRect2 outerQuadrant, int maxPerQuadrant) {
        this.root = new Node(outerQuadrant, maxPerQuadrant);
    }

    public void insert(IVector2 position, T item) {

        root.insert(new QuadItem<>(position, item));
    }

    public List<QuadItem<T>> query(IRect2 searchArea) {
        List<QuadItem<T>> foundItems = new ArrayList<>();
        root.query(searchArea, foundItems);
        return foundItems;
    }

    public void remove(IVector2 position, T item) {
        root.remove(position, item);
    }

    public QuadItem<T> removeAndGet(IVector2 position, T item) {
        return root.removeAndGet(position, item);
    }

    public boolean update(IVector2 oldPosition, IVector2 newPosition, T item) {
        QuadItem<T> found = root.removeAndGet(oldPosition, item);
        if (found == null) {
            return false;
        } else {
            found.updatePosition(newPosition);
            root.insert(found);
            return true;
        }
    }

    private class Node {
        private final IRect2 quadrant;
        private QuadItem<T>[] items;
        private int capacity;
        private int currCapacity = 0;
        private Node tLeftInnerQuad;
        private Node tRightInnerQuad;
        private Node bLeftInnerQuad;
        private Node bRightInnerQuad;
        private volatile boolean subdivided;
        StampedLock lock = new StampedLock();

        @SuppressWarnings("unchecked")
        public Node(IRect2 quadrant, int capacity) {
            this.quadrant = quadrant;
            this.capacity = capacity;
            items = (QuadItem<T>[]) new QuadItem[capacity];
        }

        boolean insert(QuadItem<T> item) {
            long rStamp = -1;
            boolean contains;
            do {
                rStamp = lock.tryOptimisticRead();
                contains = quadrant.contains(item.position);
            } while (!lock.validate(rStamp));
            if (!contains) { return false; }

            if (subdivided) {
                return insertIntoSubQuad(item);
            }

            long wStamp = lock.writeLock();
            try {


                if (currCapacity < capacity) {
                    items[currCapacity] = item;
                    currCapacity++;
                    return true;
                }
                if (quadrant.size().x() <= 16 || quadrant.size().y() <= 16) {
                    capacity = capacity * 2;
                    items = Arrays.copyOf(items, capacity);
                    items[currCapacity] = item;
                    currCapacity++;
                    return true;
                }

            } finally {
                lock.unlockWrite(wStamp);
            }

            subdivide();
            return insertIntoSubQuad(item);
        }

        boolean remove(IVector2 position, T item) {
            long rStamp = -1;
            boolean contains;
            do {
                rStamp = lock.tryOptimisticRead();
                contains = !quadrant.contains(position);
            } while (!lock.validate(rStamp));
            if (!contains) { return false; }

            if (subdivided) {
                return (tLeftInnerQuad.remove(position, item) || tRightInnerQuad.remove(position, item)
                        || bLeftInnerQuad.remove(position, item) || bRightInnerQuad.remove(position, item));
            }

            long wStamp = lock.writeLock();
            try {
                for (int i = 0; i < currCapacity; i++) {
                    if (items[i].position().equals(position) && items[i].item().equals(item)) {
                        System.arraycopy(items, i + 1, items, i, currCapacity - i - 1);
                        currCapacity--;
                        items[currCapacity] = null;
                        return true;
                    }
                }
            } finally {
                lock.unlockWrite(wStamp);
            }
            return false;
        }

        QuadItem<T> removeAndGet(IVector2 position, T item) {
            long rStamp = -1;
            boolean contains;
            do {
                rStamp = lock.tryOptimisticRead();
                contains = !quadrant.contains(position);
            } while (!lock.validate(rStamp));
            if (!contains) { return null; }

            long wLock = lock.writeLock();
            try {
                if (subdivided) {
                    QuadItem<T> found = tLeftInnerQuad.removeAndGet(position, item);
                    if (found != null) { return found; }
                    found = tRightInnerQuad.removeAndGet(position, item);
                    if (found != null) { return found; }
                    found = bLeftInnerQuad.removeAndGet(position, item);
                    if (found != null) { return found; }
                    return bRightInnerQuad.removeAndGet(position, item);
                }

                for (int i = 0; i < currCapacity; i++) {
                    if (items[i].position().equals(position) && items[i].item().equals(item)) {
                        QuadItem<T> tempItem = items[i];
                        System.arraycopy(items, i + 1, items, i, currCapacity - i - 1);
                        currCapacity--;
                        items[currCapacity] = null;
                        return tempItem;
                    }
                }
                return null;
            } finally {
                lock.unlockWrite(wLock);
            }
        }

        // Write lock is already held during subdivision from the caller
        void subdivide() {
            long stamp = lock.writeLock();
            try {
                int centerX = quadrant.getCenter().x();
                int centerY = quadrant.getCenter().y();
                int halfWidth = quadrant.size().x() / 2;
                int halfHeight = quadrant.size().y() / 2;

                tLeftInnerQuad = new Node(
                        IRect2.of(centerX - halfWidth, centerY - halfHeight, halfWidth, halfHeight), capacity
                );
                tRightInnerQuad = new Node(IRect2.of(centerX, centerY - halfHeight, halfWidth, halfHeight), capacity);
                bLeftInnerQuad = new Node(IRect2.of(centerX - halfWidth, centerY, halfWidth, halfHeight), capacity);
                bRightInnerQuad = new Node(IRect2.of(centerX, centerY, halfWidth, halfHeight), capacity);
                items = null; // Clear items in current node after redistribution
                currCapacity = 0;
                subdivided = true;
            } finally {
                lock.unlockWrite(stamp);
            }

            for (int i = 0; i < currCapacity; i++) {
                QuadItem<T> item = items[i];
                insertIntoSubQuad(item);
            }



        }


        private boolean insertIntoSubQuad(QuadItem<T> item) {
            long stamp = -1;
            boolean contains;
            do {
                stamp = lock.tryOptimisticRead();
                contains = tLeftInnerQuad.quadrant.contains(item.position);
            } while (!lock.validate(stamp));
            if (contains) { return tLeftInnerQuad.insert(item); }

            do {
                stamp = lock.tryOptimisticRead();
                contains = tRightInnerQuad.quadrant.contains(item.position);
            } while (!lock.validate(stamp));
            if (contains) { return tRightInnerQuad.insert(item); }

            do {
                stamp = lock.tryOptimisticRead();
                contains = bLeftInnerQuad.quadrant.contains(item.position);
            } while (!lock.validate(stamp));
            if (contains) { return bLeftInnerQuad.insert(item); }

            do {
                stamp = lock.tryOptimisticRead();
                contains = bRightInnerQuad.quadrant.contains(item.position);
            } while (!lock.validate(stamp));
            if (contains) { return bRightInnerQuad.insert(item); }

            throw new IllegalStateException("Insertion Failed. Position:" + item.position + "\nQuadrants:\n "
                    + tLeftInnerQuad + tRightInnerQuad + bLeftInnerQuad + bRightInnerQuad);

        }

        void query(IRect2 searchArea, List<QuadItem<T>> itemFound) {
            long stamp = lock.readLock();
            boolean intersects;
            do {
                intersects = quadrant.intersects(searchArea);
            } while (!lock.validate(stamp));
            if (!intersects) { return; }

            if (subdivided) {
                tLeftInnerQuad.query(searchArea, itemFound);
                tRightInnerQuad.query(searchArea, itemFound);
                bLeftInnerQuad.query(searchArea, itemFound);
                bRightInnerQuad.query(searchArea, itemFound);
            }

            stamp = lock.readLock();
            for (int i = 0; i < currCapacity; ++i) {
                if (searchArea.contains(items[i].position())) {
                    itemFound.add(items[i]);
                }
            }
            lock.unlockRead(stamp);
        }

        @Override
        public String toString() {
            return toStringHelper("");
        }

        private String toStringHelper(String indent) {
            StringBuilder sb = new StringBuilder();
            sb.append(indent).append("Node: ").append(quadrant).append("\n");

            if (items != null) {
                for (int i = 0; i < currCapacity; i++) {
                    sb.append(indent).append(" - Item: ").append(items[i]).append("\n");
                }
            }

            if (subdivided) {
                String newIndent = indent + "  ";
                sb.append(tLeftInnerQuad.toStringHelper(newIndent));
                sb.append(tRightInnerQuad.toStringHelper(newIndent));
                sb.append(bLeftInnerQuad.toStringHelper(newIndent));
                sb.append(bRightInnerQuad.toStringHelper(newIndent));
            }

            return sb.toString();
        }

    }

    @Override
    public String toString() {
        return root.toString();
    }

    // public record QuadItem<T>(IVector2 position, T item) { }
    public static class QuadItem<T> {
        private final IAtomicVector2 position;
        private final T item;

        public QuadItem(IVector2 position, T item) {
            this.position = IVector2.ofAtomic(position);
            this.item = item;
        }

        public IVector2 position() {
            return position;
        }

        public T item() {
            return item;
        }

        public void updatePosition(IVector2 pos) {
            this.position.setXY(pos);
        }

        @Override
        public String toString() {
            return "Position: " + position + ", Item: " + item;
        }
    }


}
