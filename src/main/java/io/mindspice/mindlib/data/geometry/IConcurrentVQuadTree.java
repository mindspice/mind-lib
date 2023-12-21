package io.mindspice.mindlib.data.geometry;

import io.mindspice.mindlib.util.Utils;

import java.util.*;
import java.util.concurrent.locks.StampedLock;


public class IConcurrentVQuadTree<T> {
    private final Node root;

    public IConcurrentVQuadTree(IRect2 outerQuadrant, int maxPerQuadrant) {
        this.root = new Node(outerQuadrant, maxPerQuadrant, null);
    }

    public void insert(IVector2 position, T item) {
        root.insert(new QuadItem<>(IVector2.ofMutable(position), item));
    }

    public List<QuadItem<T>> query(IRect2 searchArea) {
        List<QuadItem<T>> foundItems = new ArrayList<>();
        root.query(searchArea, foundItems);
        return foundItems;
    }

    public List<QuadItem<T>> query(IRect2 searchArea, List<QuadItem<T>> foundList) {
        root.query(searchArea, foundList);
        return foundList;
    }

    public void remove(IVector2 position, T item) {
        root.remove(position, item);
    }

    public QuadItem<T> removeAndGet(IVector2 position, T item) {
        return root.removeAndGet(position, item);
    }

    public boolean update(IVector2 oldPosition, IVector2 newPosition, T item) {
        return root.update(oldPosition, newPosition, item);
    }

    public void deFragment() {
        root.deFrag();
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
        private final Node parent;
        StampedLock lock = new StampedLock();

        @SuppressWarnings("unchecked")
        public Node(IRect2 quadrant, int capacity, Node parent) {
            this.quadrant = quadrant;
            this.capacity = capacity;
            this.parent = parent;
            items = (QuadItem<T>[]) new QuadItem[capacity];

        }

        boolean insert(QuadItem<T> item) {
            long rStamp = -1;
            boolean contains;
            do {
                rStamp = lock.tryOptimisticRead();
                contains = quadrant.contains(item.position());
            } while (!lock.validate(rStamp));

            if (!contains) {
                return false;
            }

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
                    capacity = (int) (capacity * 1.5);
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

        public boolean update(IVector2 position, IVector2 newPosition, T item) {
            long stamp = -1;
            boolean contains;

            do {
                stamp = lock.tryOptimisticRead();
                contains = quadrant.contains(position);
            } while (!lock.validate(stamp));
            if (!contains) { return false; }

            if (subdivided) {
                return tLeftInnerQuad.update(position, newPosition, item)
                        || tRightInnerQuad.update(position, newPosition, item)
                        || bLeftInnerQuad.update(position, newPosition, item)
                        || bRightInnerQuad.update(position, newPosition, item);
            }

            stamp = lock.writeLock();
            try {
                for (int i = 0; i < currCapacity; i++) {
                    if (items[i].position().equals(position) && items[i].item().equals(item)) {
                        QuadItem<T> foundItem = items[i];
                        foundItem.position().setXY(newPosition);
                        if (quadrant.contains(newPosition)) {
                            return true;
                        }
                        System.arraycopy(items, i + 1, items, i, currCapacity - i - 1);
                        currCapacity--;
                        items[currCapacity] = null;
                        lock.unlockWrite(stamp);
                        stamp = -1;
                        return backInsert(this, foundItem);
                    }
                }
                return false;
            } finally {
                if (stamp != -1) { lock.unlockWrite(stamp); }
            }
        }

        public boolean backInsert(Node node, QuadItem<T> item) {
            boolean success = node.insert(item);
            if (success) {
                return true;
            }
            if (node.parent == null) {
                return insert(item);
            }
            return backInsert(node.parent, item);
        }

        boolean remove(IVector2 position, T item) {
            long rStamp = -1;
            boolean contains;
            do {
                rStamp = lock.tryOptimisticRead();
                contains = quadrant.contains(position);
            } while (!lock.validate(rStamp));
            if (!contains) { return false; }

            if (subdivided) {
                return (tLeftInnerQuad.remove(position, item)
                        || tRightInnerQuad.remove(position, item)
                        || bLeftInnerQuad.remove(position, item)
                        || bRightInnerQuad.remove(position, item));
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
                contains = quadrant.contains(position);
            } while (!lock.validate(rStamp));
            if (!contains) { return null; }

            if (subdivided) {
                QuadItem<T> found = tLeftInnerQuad.removeAndGet(position, item);
                if (found != null) { return found; }
                found = tRightInnerQuad.removeAndGet(position, item);
                if (found != null) { return found; }
                found = bLeftInnerQuad.removeAndGet(position, item);
                if (found != null) { return found; }
                return bRightInnerQuad.removeAndGet(position, item);
            }
            long wLock = lock.writeLock();
            try {
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
                        IRect2.of(centerX - halfWidth, centerY - halfHeight, halfWidth, halfHeight), capacity, this
                );
                tRightInnerQuad = new Node(
                        IRect2.of(centerX, centerY - halfHeight, halfWidth, halfHeight), capacity, this
                );
                bLeftInnerQuad = new Node(
                        IRect2.of(centerX - halfWidth, centerY, halfWidth, halfHeight), capacity, this
                );
                bRightInnerQuad = new Node(
                        IRect2.of(centerX, centerY, halfWidth, halfHeight), capacity, this
                );


            } finally {
                lock.unlockWrite(stamp);
            }

            // Releasing here to avoid reentrancy, can allow for a short moment where changes will not be seen
            for (int i = 0; i < currCapacity; i++) {
                QuadItem<T> item = items[i];
                insertIntoSubQuad(item);
            }
            items = null;
            currCapacity = 0;
            subdivided = true;


        }

        private boolean insertIntoSubQuad(QuadItem<T> item) {
            long stamp = -1;
            boolean contains;
            do {
                stamp = lock.tryOptimisticRead();
                contains = tLeftInnerQuad.quadrant.contains(item.position());
            } while (!lock.validate(stamp));
            if (contains) { return tLeftInnerQuad.insert(item); }

            do {
                stamp = lock.tryOptimisticRead();
                contains = tRightInnerQuad.quadrant.contains(item.position());
            } while (!lock.validate(stamp));
            if (contains) { return tRightInnerQuad.insert(item); }

            do {
                stamp = lock.tryOptimisticRead();
                contains = bLeftInnerQuad.quadrant.contains(item.position());
            } while (!lock.validate(stamp));
            if (contains) { return bLeftInnerQuad.insert(item); }

            do {
                stamp = lock.tryOptimisticRead();
                contains = bRightInnerQuad.quadrant.contains(item.position());
            } while (!lock.validate(stamp));
            if (contains) { return bRightInnerQuad.insert(item); }

            throw new IllegalStateException("Insertion Failed. Position:" + item.position() + "\nQuadrants:\n "
                    + tLeftInnerQuad + tRightInnerQuad + bLeftInnerQuad + bRightInnerQuad);

        }

        void query(IRect2 searchArea, List<QuadItem<T>> itemFound) {
            long stamp;
            boolean intersects;
            do {
                stamp = lock.tryOptimisticRead();
                intersects = quadrant.intersects(searchArea);
            } while (!lock.validate(stamp));
            if (!intersects) {
                return;
            }

            if (subdivided) {
                tLeftInnerQuad.query(searchArea, itemFound);
                tRightInnerQuad.query(searchArea, itemFound);
                bLeftInnerQuad.query(searchArea, itemFound);
                bRightInnerQuad.query(searchArea, itemFound);
            }

            stamp = lock.writeLock();
            try {
                for (int i = 0; i < currCapacity; ++i) {
                    if (searchArea.contains(items[i].position())) {
                        itemFound.add(items[i]);
                    }
                }
            } finally {
                lock.unlockWrite(stamp);
            }

        }

        private void deFrag() {
            if (subdivided) {
                tLeftInnerQuad.deFrag();
                tRightInnerQuad.deFrag();
                bLeftInnerQuad.deFrag();
                bRightInnerQuad.deFrag();

                if (areAllChildrenEmpty()) {
                    mergeChildren();
                }
            } else {
                sortItems();
                unSize();
            }
        }

        private void sortItems() {
            long stamp = -1;
            boolean notNull;
            do {
                stamp = lock.tryOptimisticRead();
                notNull = items != null;
            } while (!lock.validate(stamp));
            if (notNull) {
                stamp = lock.writeLock();
                try {
                    Arrays.sort(items, 0, currCapacity, Comparator.comparingInt(o -> o.position().x()));
                } finally {
                    lock.unlockWrite(stamp);
                }
            }
        }

        private void unSize() {
            long stamp = -1;
            boolean unSize;
            do {
                stamp = lock.tryOptimisticRead();
                unSize = capacity > root.capacity && currCapacity < (int) (root.capacity * 0.7);
            } while (!lock.validate(stamp));

            if (unSize) {
                stamp = lock.writeLock();
                try {
                    items = Arrays.copyOf(items, root.capacity);
                    capacity = root.capacity;
                } finally {
                    lock.unlockWrite(stamp);
                }
            }
        }

        private boolean areAllChildrenEmpty() {
            long stamp = -1;
            boolean empty;
            do {
                stamp = lock.tryOptimisticRead();
                empty = tLeftInnerQuad.isEmpty() && tRightInnerQuad.isEmpty() &&
                        bLeftInnerQuad.isEmpty() && bRightInnerQuad.isEmpty();
            } while (!lock.validate(stamp));
            return empty;
        }

        private void mergeChildren() {
            long stamp = -1;
            try {
                stamp = lock.tryOptimisticRead();
                tLeftInnerQuad = tRightInnerQuad = bLeftInnerQuad = bRightInnerQuad = null;
                subdivided = false;
            } finally {
                lock.unlockWrite(stamp);
            }
        }

        private boolean isEmpty() {
            return !subdivided && currCapacity == 0;
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


}