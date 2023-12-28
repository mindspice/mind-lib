package io.mindspice.mindlib.data.geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class IVectorQuadTreeOld<T> {
    private final Node root;

    public IVectorQuadTreeOld(IRect2 outerQuadrant, int maxPerQuadrant) {
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

    public List<QuadItem<T>> query(IRect2 searchArea, List<QuadItem<T>> queryRtnList) {
        root.query(searchArea, queryRtnList);
        return queryRtnList;
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
        private boolean subdivided;
        private final Node parent;

        @SuppressWarnings("unchecked")
        public Node(IRect2 quadrant, int capacity, Node parent) {
            this.quadrant = quadrant;
            this.capacity = capacity;
            this.parent = parent;
            items = (QuadItem<T>[]) new QuadItem[capacity];

        }

        boolean insert(QuadItem<T> item) {
            if (!quadrant.contains(item.position())) {
                return false;
            }

            if (subdivided) {
                return insertIntoSubQuad(item);
            }

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
            subdivide();
            return insertIntoSubQuad(item);
        }

        public boolean update(IVector2 position, IVector2 newPosition, T item) {
            if (!quadrant.contains(position)) {
                return false;
            }

            if (subdivided) {
                return tLeftInnerQuad.update(position, newPosition, item)
                        || tRightInnerQuad.update(position, newPosition, item)
                        || bLeftInnerQuad.update(position, newPosition, item)
                        || bRightInnerQuad.update(position, newPosition, item);
            }

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

                    return backInsert(this, foundItem);
                }
            }
            return false;
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

        public boolean remove(IVector2 position, T item) {
            if (!quadrant.contains(position)) {
                return false;
            }

            if (subdivided) {
                return (tLeftInnerQuad.remove(position, item)
                        || tRightInnerQuad.remove(position, item)
                        || bLeftInnerQuad.remove(position, item)
                        || bRightInnerQuad.remove(position, item));
            }

            for (int i = 0; i < currCapacity; i++) {
                if (items[i].position().equals(position) && items[i].item().equals(item)) {
                    System.arraycopy(items, i + 1, items, i, currCapacity - i - 1);
                    currCapacity--;
                    items[currCapacity] = null;
                    return true;
                }
            }
            return false;
        }

        QuadItem<T> removeAndGet(IVector2 position, T item) {
            if (!quadrant.contains(position)) {
                return null;
            }

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
        }

        void subdivide() {

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

            for (int i = 0; i < currCapacity; i++) {
                QuadItem<T> item = items[i];
                insertIntoSubQuad(item);
            }

            items = null;
            currCapacity = 0;
            subdivided = true;
        }

        private boolean insertIntoSubQuad(QuadItem<T> item) {
            // Insert the item into the appropriate sub-quadrant
            if (tLeftInnerQuad.quadrant.contains(item.position())) {
                return tLeftInnerQuad.insert(item);
            }
            if (tRightInnerQuad.quadrant.contains(item.position())) {
                return tRightInnerQuad.insert(item);
            }
            if (bLeftInnerQuad.quadrant.contains(item.position())) {
                return bLeftInnerQuad.insert(item);
            }
            if (bRightInnerQuad.quadrant.contains(item.position())) {
                return bRightInnerQuad.insert(item);
            }

            throw new IllegalStateException("Insertion Failed. Position:" + item.position() + "\nQuadrants:\n "
                    + tLeftInnerQuad + tRightInnerQuad + bLeftInnerQuad + bRightInnerQuad);

        }

        void query(IRect2 searchArea, List<QuadItem<T>> itemFound) {
            if (!quadrant.intersects(searchArea)) {
                return;
            }

            if (subdivided) {
                tLeftInnerQuad.query(searchArea, itemFound);
                tRightInnerQuad.query(searchArea, itemFound);
                bLeftInnerQuad.query(searchArea, itemFound);
                bRightInnerQuad.query(searchArea, itemFound);
            }

            for (int i = 0; i < currCapacity; ++i) {
                if (searchArea.contains(items[i].position())) {
                    itemFound.add(items[i]);
                }
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
            if (items == null) { return; }
            Arrays.sort(items, 0, currCapacity, Comparator.comparingInt(o -> o.position().x()));
        }

        private void unSize() {
            if (capacity > root.capacity && currCapacity < (int) (root.capacity * 0.7)) {
                items = Arrays.copyOf(items, root.capacity);
                capacity = root.capacity;
            }
        }

        private boolean areAllChildrenEmpty() {
            return tLeftInnerQuad.isEmpty() && tRightInnerQuad.isEmpty() &&
                    bLeftInnerQuad.isEmpty() && bRightInnerQuad.isEmpty();
        }

        private void mergeChildren() {
            tLeftInnerQuad = tRightInnerQuad = bLeftInnerQuad = bRightInnerQuad = null;
            subdivided = false;
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
