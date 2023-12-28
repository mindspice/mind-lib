package io.mindspice.mindlib.data.collections.lists;

import java.util.function.IntConsumer;


public class LinkedNode<T> {
    private T item;
    private LinkedNode<T> prevLinkedNode;
    private LinkedNode<T> nextLinkedNode;
    private final IntConsumer onChangeConsumer;

    public LinkedNode(IntConsumer countConsumer, T item, LinkedNode<T> prevLinkedNode) {
        this.item = item;
        this.prevLinkedNode = prevLinkedNode;
        this.onChangeConsumer = countConsumer;
    }

    public T item() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public void removeSelf() {
        if (prevLinkedNode != null) { prevLinkedNode.nextLinkedNode = this.nextLinkedNode; }
        if (nextLinkedNode != null) { nextLinkedNode.prevLinkedNode = this.prevLinkedNode; }
        onChangeConsumer.accept(-1);
    }

    public LinkedNode<T> insertAfter(T item) {
        LinkedNode<T> newLinkedNode = new LinkedNode<>(onChangeConsumer, item, this);
        newLinkedNode.nextLinkedNode = this.nextLinkedNode;
        this.nextLinkedNode = newLinkedNode;
        newLinkedNode.prevLinkedNode = this;
        onChangeConsumer.accept(1);
        return newLinkedNode;
    }

    public LinkedNode<T> prevNode() {
        return prevLinkedNode;
    }

    public LinkedNode<T> nextNode() {
        return nextLinkedNode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LinkedNode: ");
        sb.append("\n  item: ").append(item);
//        sb.append(",\n  prevLinkedNode: ").append(prevLinkedNode.hashCode());
//        sb.append(",\n  nextLinkedNode: ").append(nextLinkedNode.hashCode());
        sb.append(",\n  onChangeConsumer: ").append(onChangeConsumer);
        sb.append("\n");
        return sb.toString();
    }
}