package io.mindspice.mindlib.data.geometry;

import java.util.ArrayList;
import java.util.List;


public class IConcurrentQuadTree<T> {
    private final Node root;

    public IConcurrentQuadTree(IRect2 outerQuadrant, int maxPerQuadrant) {
        this.root = new Node(outerQuadrant, maxPerQuadrant);
    }

    public void insert(IVector2 position, T item) {
        root.insert(position, item);
    }

    public List<QuadItem<T>> query(IRect2 searchArea) {
        List<QuadItem<T>> foundItems = new ArrayList<>();
        root.query(searchArea, foundItems);
        return foundItems;
    }

    private class Node {
        private final IRect2 quadrant;
        private QuadItem<T>[] items;
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
                return insertIntoSubQuad(position, item);
            }

            if (currCapacity < capacity) {
                items[currCapacity] = new QuadItem<>(position, item);
                currCapacity++;
                return true;
            }

            subdivide();
            return insertIntoSubQuad(position, item);
        }


        boolean remove(IVector2 position, T item) {
            if (!quadrant.contains(position)) {
                return false;
            }

            if (subdivided) {
                return (tLeftInnerQuad.remove(position, item) || tRightInnerQuad.remove(position, item)
                        || bLeftInnerQuad.remove(position, item) || bRightInnerQuad.remove(position, item));
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


        void subdivide() {
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


            for (int i = 0; i < currCapacity; i++) {
                QuadItem<T> item = items[i];
                insertIntoSubQuad(item.position(), item.item());
            }

            items = null; // Clear items in current node after redistribution
            currCapacity = 0;
            subdivided = true;
        }

        private boolean insertIntoSubQuad(IVector2 position, T item) {
            // Insert the item into the appropriate sub-quadrant
            if (tLeftInnerQuad.quadrant.contains(position)) {
                return tLeftInnerQuad.insert(position, item);
            } else if (tRightInnerQuad.quadrant.contains(position)) {
                return tRightInnerQuad.insert(position, item);
            } else if (bLeftInnerQuad.quadrant.contains(position)) {
                return bLeftInnerQuad.insert(position, item);
            } else if (bRightInnerQuad.quadrant.contains(position)) {
                return bRightInnerQuad.insert(position, item);
            }

            throw new IllegalStateException("Insertion Failed. Position:" + position + "\nQuadrants:\n "
                    + tLeftInnerQuad + tRightInnerQuad + bLeftInnerQuad  + bRightInnerQuad);

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


    public record QuadItem<T>(IVector2 position, T item) { }



}
