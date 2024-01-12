//package io.mindspice.mindlib.data.geometry;
//
//import io.mindspice.mindlib.data.collections.lists.LinkedNode;
//import io.mindspice.mindlib.data.collections.lists.SRLinkedList;
//import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class IPolygonQuadTree<T> {
//    private final Node root;
//    private final IntObjectHashMap<LinkedNode<QuadItem<T>>> nodeMap = new IntObjectHashMap<>(20);
//
//    public IPolygonQuadTree(IRect2 outerQuadrant, int maxPerQuadrant) {
//        this.root = new Node(outerQuadrant, maxPerQuadrant, null);
//    }
//
//    public void insert(int id, IVector2 position, T item) {
//        QuadItem<T> quadItem = new QuadItem<>(id, IVector2.ofMutable(position), item);
//        root.insert(quadItem);
//    }
//
//    public List<QuadItem<T>> query(IRect2 searchArea) {
//        List<QuadItem<T>> foundItems = new ArrayList<>();
//        root.query(searchArea, foundItems);
//        return foundItems;
//    }
//
//    public List<QuadItem<T>> query(IRect2 searchArea, List<QuadItem<T>> queryRtnList) {
//        root.query(searchArea, queryRtnList);
//        return queryRtnList;
//    }
//
//    public boolean remove(int id) {
//        var node = nodeMap.get(id);
//        if (node != null) {
//            node.removeSelf();
//            return true;
//        }
//        return false;
//    }
//
//    public boolean update(int id, IVector2 newPosition) {
//        IVector2 oldPosition = nodeMap.get(id).item().position();
//        return root.update(oldPosition, newPosition, id);
//    }
//
////    public void deFragment() {
////        root.deFrag();
////    }
//
//
//    private class Node {
//        private final IRect2 quadrant;
//        private SRLinkedList<QuadItem<T>> items;
//        private int capacity;
//        private Node tLeftInnerQuad;
//        private Node tRightInnerQuad;
//        private Node bLeftInnerQuad;
//        private Node bRightInnerQuad;
//        private boolean subdivided;
//        private final Node parent;
//
//        public Node(IRect2 quadrant, int capacity, Node parent) {
//            this.quadrant = quadrant;
//            this.capacity = capacity;
//            this.parent = parent;
//            items = new SRLinkedList<>();
//
//        }
//
//        boolean insert(QuadItem<T> item) {
//            if (!quadrant.contains(item.position())) {
//                return false;
//            }
//
//            if (subdivided) {
//                return insertIntoSubQuad(item);
//            }
//
//            if (items.size() < capacity) {
//                var node = items.add(item);
//                nodeMap.put(item.id(), node);
//                return true;
//            }
//
//            if (quadrant.size().x() <= 16 || quadrant.size().y() <= 16) {
//                var node = items.add(item);
//                nodeMap.put(item.id(), node);
//                return true;
//            }
//            subdivide();
//            return insertIntoSubQuad(item);
//        }
//
//        public boolean update(IVector2 oldPosition, IVector2 newPosition, int id) {
//            if (!quadrant.contains(oldPosition)) {
//                return false;
//            }
//            if (subdivided) {
//                return tLeftInnerQuad.update(oldPosition, newPosition, id)
//                        || tRightInnerQuad.update(oldPosition, newPosition, id)
//                        || bLeftInnerQuad.update(oldPosition, newPosition, id)
//                        || bRightInnerQuad.update(oldPosition, newPosition, id);
//            }
//            var node = items.getRootNode();
//            while ((node = node.nextNode()) != null) {
//                if (node.item().position().equals(oldPosition) && node.item().id() == id) {
//                    QuadItem<T> foundItem = node.item();
//                    foundItem.position().setXY(newPosition);
//                    if (quadrant.contains(newPosition)) {
//                        return true;
//                    }
//                    node.removeSelf();
//                    backInsert(this, foundItem);
//                }
//            }
//            return false;
//        }
//
//        public boolean backInsert(Node node, QuadItem<T> item) {
//            boolean success = node.insert(item);
//            if (success) {
//                return true;
//            }
//            if (node.parent == null) {
//                return insert(item);
//            }
//            return backInsert(node.parent, item);
//        }
//
//        public boolean remove(IVector2 position, T item) {
//            if (!quadrant.contains(position)) {
//                return false;
//            }
//            if (subdivided) {
//                return (tLeftInnerQuad.remove(position, item)
//                        || tRightInnerQuad.remove(position, item)
//                        || bLeftInnerQuad.remove(position, item)
//                        || bRightInnerQuad.remove(position, item));
//            }
//            var node = items.getRootNode();
//            while ((node = node.nextNode()) != null) {
//                if (node.item().position().equals(position) && node.item().equals(item)) {
//                    node.removeSelf();
//                    return true;
//                }
//            }
//            return false;
//        }
//
//        QuadItem<T> removeAndGet(IVector2 position, T item) {
//            if (!quadrant.contains(position)) {
//                return null;
//            }
//            if (subdivided) {
//                QuadItem<T> found = tLeftInnerQuad.removeAndGet(position, item);
//                if (found != null) { return found; }
//                found = tRightInnerQuad.removeAndGet(position, item);
//                if (found != null) { return found; }
//                found = bLeftInnerQuad.removeAndGet(position, item);
//                if (found != null) { return found; }
//                return bRightInnerQuad.removeAndGet(position, item);
//            }
//            var node = items.getRootNode();
//            while ((node = node.nextNode()) != null) {
//                if (node.item().position().equals(position) && node.item().equals(item)) {
//                    QuadItem<T> foundItem = node.item();
//                    node.removeSelf();
//                    return foundItem;
//                }
//            }
//            return null;
//        }
//
//        void subdivide() {
//            int centerX = quadrant.getCenter().x();
//            int centerY = quadrant.getCenter().y();
//            int halfWidth = quadrant.size().x() / 2;
//            int halfHeight = quadrant.size().y() / 2;
//
//            tLeftInnerQuad = new Node(
//                    IRect2.of(centerX - halfWidth, centerY - halfHeight, halfWidth, halfHeight), capacity, this
//            );
//            tRightInnerQuad = new Node(
//                    IRect2.of(centerX, centerY - halfHeight, halfWidth, halfHeight), capacity, this
//            );
//            bLeftInnerQuad = new Node(
//                    IRect2.of(centerX - halfWidth, centerY, halfWidth, halfHeight), capacity, this
//            );
//            bRightInnerQuad = new Node(
//                    IRect2.of(centerX, centerY, halfWidth, halfHeight), capacity, this
//            );
//            var node = items.getRootNode();
//            while ((node = node.nextNode()) != null) {
//                insertIntoSubQuad(node.item());
//            }
//            items = null;
//            subdivided = true;
//        }
//
//        private boolean insertIntoSubQuad(QuadItem<T> item) {
//
//            // Insert the item into the appropriate sub-quadrant
//            if (tLeftInnerQuad.quadrant.contains(item.position())) {
//                return tLeftInnerQuad.insert(item);
//            }
//            if (tRightInnerQuad.quadrant.contains(item.position())) {
//                return tRightInnerQuad.insert(item);
//            }
//            if (bLeftInnerQuad.quadrant.contains(item.position())) {
//                return bLeftInnerQuad.insert(item);
//            }
//            if (bRightInnerQuad.quadrant.contains(item.position())) {
//                return bRightInnerQuad.insert(item);
//            }
//
//            throw new IllegalStateException("Insertion Failed. Position:" + item.position() + "\nQuadrants:\n "
//                    + tLeftInnerQuad + tRightInnerQuad + bLeftInnerQuad + bRightInnerQuad);
//
//        }
//
//        void query(IRect2 searchArea, List<QuadItem<T>> itemFound) {
//            if (!quadrant.intersects(searchArea)) {
//                return;
//            }
//
//            if (subdivided) {
//                tLeftInnerQuad.query(searchArea, itemFound);
//                tRightInnerQuad.query(searchArea, itemFound);
//                bLeftInnerQuad.query(searchArea, itemFound);
//                bRightInnerQuad.query(searchArea, itemFound);
//            }
//            if (items == null) {
//                return;
//            }
//            var node = items.getRootNode();
//            while ((node = node.nextNode()) != null) {
//                if (searchArea.contains(node.item().position())) {
//                    itemFound.add(node.item());
//                }
//            }
//        }
////
////        private void deFrag() {
////            if (subdivided) {
////                tLeftInnerQuad.deFrag();
////                tRightInnerQuad.deFrag();
////                bLeftInnerQuad.deFrag();
////                bRightInnerQuad.deFrag();
////
////                if (areAllChildrenEmpty()) {
////                    mergeChildren();
////                }
////            } else {
////                sortItems();
////                unSize();
////            }
////        }
//
////        private void sortItems() {
////            if (items == null) { return; }
////            Arrays.sort(items, 0, currCapacity, Comparator.comparingInt(o -> o.position().x()));
////        }
////
////        private void unSize() {
////            if (capacity > root.capacity && currCapacity < (int) (root.capacity * 0.7)) {
////                items = Arrays.copyOf(items, root.capacity);
////                capacity = root.capacity;
////            }
////        }
//
//        private boolean areAllChildrenEmpty() {
//            return tLeftInnerQuad.isEmpty() && tRightInnerQuad.isEmpty() &&
//                    bLeftInnerQuad.isEmpty() && bRightInnerQuad.isEmpty();
//        }
//
//        private void mergeChildren() {
//            tLeftInnerQuad = tRightInnerQuad = bLeftInnerQuad = bRightInnerQuad = null;
//            subdivided = false;
//        }
//
//        private boolean isEmpty() {
//            return !subdivided && items.size() == 0;
//        }
//
//        @Override
//        public String toString() {
//            return toStringHelper("");
//        }
//
//        private String toStringHelper(String indent) {
//            StringBuilder sb = new StringBuilder();
//            sb.append(indent).append("Node: ").append(quadrant).append("\n");
//
//            if (items != null) {
//                items.forEachNode(i -> sb.append(i).append("\n"));
//            }
//
//            if (subdivided) {
//                String newIndent = indent + "  ";
//                sb.append(tLeftInnerQuad.toStringHelper(newIndent));
//                sb.append(tRightInnerQuad.toStringHelper(newIndent));
//                sb.append(bLeftInnerQuad.toStringHelper(newIndent));
//                sb.append(bRightInnerQuad.toStringHelper(newIndent));
//            }
//
//            return sb.toString();
//        }
//
//    }
//
//    @Override
//    public String toString() {
//        return root.toString();
//    }
//}
