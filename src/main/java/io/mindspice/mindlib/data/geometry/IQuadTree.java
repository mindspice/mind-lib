package io.mindspice.mindlib.data.geometry;

import java.util.ArrayList;
import java.util.List;


public class IQuadTree<T> {
    private final Node root;

    public IQuadTree(IRect2 outerQuadrant, int maxPerQuadrant) {
        this.root = new Node(outerQuadrant, maxPerQuadrant);
    }

    public void insert(IVector2 position, T item) {
        root.insert(position, item);
    }

    List<QuadItem<T>> query(IRect2 searchArea) {
        List<QuadItem<T>> foundItems = new ArrayList<>();
        root.query(searchArea, foundItems);
        return foundItems;
    }

    private class Node {
        private final IRect2 quadrant;
        private final QuadItem<T>[] items;
        private final int capacity;
        private int currCapacity = 0;
        private Node tLeftInnerQuad;
        private Node tRightInnerQuad;
        private Node bLeftInnerQuad;
        private Node bRightInnerQuad;
        private boolean subdivided;

        @SuppressWarnings("unchecked")
        public Node(IRect2 quadrant, int capacity) {
            this.quadrant = quadrant;
            this.capacity = capacity;
            items = (QuadItem<T>[]) new QuadItem[capacity];
        }

        boolean insert(IVector2 position, T item) {
            if (!quadrant.contains(position)) {
                return false;
            }

            if (subdivided) {
                if (tLeftInnerQuad.insert(position, item) || tRightInnerQuad.insert(position, item)
                        || bLeftInnerQuad.insert(position, item) || bRightInnerQuad.insert(position, item)) {
                    return true;
                }
            }

            if (currCapacity < capacity) {
                items[currCapacity] = new QuadItem<>(position, item);
                currCapacity++;
                return true;
            }

            if (!subdivided) {
                subdivide();
                return insert(position, item);
            }

            return (tLeftInnerQuad.insert(position, item) || tRightInnerQuad.insert(position, item)
                    || bLeftInnerQuad.insert(position, item) || bRightInnerQuad.insert(position, item));

        }

        boolean remove(IVector2 position, T item) {
            if (!quadrant.contains(position)) {
                return false;
            }

            for (int i = 0; i < currCapacity; i++) {
                if (items[i].position().equals(position) && items[i].item().equals(item)) {
                    // Move the elements to fill the gap
                    if (currCapacity - 1 - i > 0) {
                        System.arraycopy(items, i + 1, items, i, currCapacity - 1 - i);
                    }

                    currCapacity--;
                    items[currCapacity] = null;
                    return true;
                }
            }

            if (subdivided) {
                // Attempt to remove from a subdivided node
                return (tLeftInnerQuad.remove(position, item) || tRightInnerQuad.remove(position, item)
                        || bLeftInnerQuad.remove(position, item) || bRightInnerQuad.remove(position, item));
            }

            return false;
        }

        void subdivide() {
            int x = quadrant.getCenter().x();
            int y = quadrant.getCenter().y();
            int w = quadrant.size().x() / 2;
            int h = quadrant.size().y() / 2;

            tLeftInnerQuad = new Node(IRect2.of(x - w / 2, y - h / 2, w, h), capacity);
            tRightInnerQuad = new Node(IRect2.of(x + w / 2, y - h / 2, w, h), capacity);
            bLeftInnerQuad = new Node(IRect2.of(x - w / 2, y + h / 2, w, h), capacity);
            bRightInnerQuad = new Node(IRect2.of(x + w / 2, y + h / 2, w, h), capacity);
            subdivided = true;
        }

        void query(IRect2 searchArea, List<QuadItem<T>> itemFound) {
            if (!quadrant.intersects(searchArea)) { return; }

            for (int i = 0; i < currCapacity; ++i) {
                if (searchArea.contains(items[i].position())) {
                    itemFound.add(items[i]);
                }
            }
            if (subdivided) {
                tLeftInnerQuad.query(searchArea, itemFound);
                tRightInnerQuad.query(searchArea, itemFound);
                bLeftInnerQuad.query(searchArea, itemFound);
                bRightInnerQuad.query(searchArea, itemFound);
            }
        }
    }


    private record QuadItem<T>(IVector2 position, T item) { }


}
