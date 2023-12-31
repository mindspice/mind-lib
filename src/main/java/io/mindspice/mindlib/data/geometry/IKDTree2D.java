package io.mindspice.mindlib.data.geometry;

import java.util.ArrayList;
import java.util.List;


// WIP
public class IKDTree2D<T> {
    private KDNode<T> root;

    public IKDTree2D(IRect2 outerBounds) {
        this.root = new KDNode<>(outerBounds, 0);
    }

    public void insert(IVector2 position, T item) {
        if (root == null) {
            root = new KDNode<>(position, item, 0);
        } else {
            root.insert(new KDItem<>(position, item));
        }
    }

    public List<KDItem<T>> query(IRect2 searchArea) {
        List<KDItem<T>> foundItems = new ArrayList<>();
        if (root != null) {
            root.query(searchArea, foundItems);
        }
        return foundItems;
    }

    private static class KDNode<T> {
        private KDItem<T> item;
        private KDNode<T> leftChild, rightChild;
        private int level;
        private IRect2 bounds;

        public KDNode(IRect2 bounds, int level) {
            this.bounds = bounds;
            this.level = level;
        }

        public KDNode(IVector2 position, T item, int level) {
            this.item = new KDItem<>(position, item);
            this.level = level;
        }

        public void insert(KDItem<T> newItem) {
            if (item == null) {
                item = newItem;
                return;
            }

            if (isLessThan(newItem.position, item.position, level)) {
                if (leftChild == null) {
                    IRect2 newBounds = (level % 2 == 0)
                            ? IRect2.of(bounds.start(), IVector2.of(item.position.x(), bounds.end().y()))
                            : IRect2.of(bounds.start(), IVector2.of(bounds.end().x(), item.position.y()));
                    leftChild = new KDNode<>(newBounds, (level + 1) % 2);
                }
                leftChild.insert(newItem);
            } else {
                if (rightChild == null) {
                    IRect2 newBounds = (level % 2 == 0)
                            ?  IRect2.of( IVector2.of(item.position.x(), bounds.start().y()), bounds.end())
                            :  IRect2.of( IVector2.of(bounds.start().x(), item.position.y()), bounds.end());
                    rightChild = new KDNode<>(newBounds, (level + 1) % 2);
                }
                rightChild.insert(newItem);
            }
        }

        public void query(IRect2 searchArea, List<KDItem<T>> foundItems) {
            if (item != null && searchArea.contains(item.position)) {
                foundItems.add(item);
            }

            if (leftChild != null && searchArea.intersects(leftChild.bounds)) {
                leftChild.query(searchArea, foundItems);
            }
            if (rightChild != null && searchArea.intersects(rightChild.bounds)) {
                rightChild.query(searchArea, foundItems);
            }
        }

        private boolean isLessThan(IVector2 p1, IVector2 p2, int level) {
            return (level % 2 == 0) ? p1.x() < p2.x() : p1.y() < p2.y();
        }
    }


    public record KDItem<T>(IVector2 position, T item) {
    }
}
