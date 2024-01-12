package io.mindspice.mindlib.data.collections.lists;

import java.util.function.Consumer;


public class SRLinkedList<T> {
    private final LinkedNode<T> rootLinkedNode = new LinkedNode<>(this::onSizeChange, null, null);
    private int size = 0;

    public int size() {
        return size;
    }

    public LinkedNode<T> add(T item) {
        LinkedNode<T> current = rootLinkedNode;
        while (current.nextNode() != null) {
            current = current.nextNode();
        }
        return current.insertAfter(item);
    }

    private void onSizeChange(int value) {
        size += value;
    }

    public void remove(T item) {
        LinkedNode<T> current = rootLinkedNode.nextNode();
        while (current != null) {
            if (current.item() != null && current.item().equals(item)) {
                LinkedNode<T> next = current.nextNode();
                current.removeSelf();
                current = next; // Advance to the next node
            } else {
                current = current.nextNode();
            }
        }
    }

    public LinkedNode<T> getFirstNode() {
        return rootLinkedNode.nextNode();
    }

    public LinkedNode<T> getRootNode() {
        return rootLinkedNode;
    }

    public T getFirst() {
        return rootLinkedNode.nextNode().item();
    }

    public void forEach(Consumer<T> action) {
        LinkedNode<T> current = rootLinkedNode.nextNode();
        while (current != null) {
            action.accept(current.item());
            current = current.nextNode();
        }
    }

    public void forEachNode(Consumer<LinkedNode<T>> action) {
        LinkedNode<T> current = rootLinkedNode.nextNode();
        while (current != null) {
            action.accept(current);
            current = current.nextNode();
        }
    }


}
