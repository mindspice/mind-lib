package io.mindspice.mindlib.data.collections.lists;

import java.util.*;
import java.util.function.Consumer;


public class CyclicList<T> {
    private T[] elements;
    private int currIndex = 0;
    private int currSize = 0;

    @SuppressWarnings("unchecked")
    public CyclicList(List<T> initialElements) {
        this.elements = (T[]) new Object[initialElements.size()];
        for (int i = 0; i < initialElements.size(); i++) {
            this.elements[i] = initialElements.get(i);
        }
        this.currSize = initialElements.size();
    }

    @SuppressWarnings("unchecked")
    public CyclicList(int startingSize) {
        this.elements = (T[]) new Object[startingSize];
    }

    @SuppressWarnings("unchecked")
    public CyclicList() {
        this.elements = (T[]) new Object[10];
    }

    public T getNext() {
        if (currSize == 0) { return null; }
        if (currIndex >= currSize) {
            currIndex = 0;
        }
        return elements[currIndex++];

    }

    public void add(T obj) {
        if (elements.length == currSize) {
            elements = Arrays.copyOf(elements, (int) (currSize * 1.5));
        }
        elements[currSize] = obj;
        currSize++;
    }

    public void addAll(List<T> objList) {
        objList.forEach(this::add);
    }

    public void remove(T obj) {
        for (int i = 0; i < currSize; i++) {
            if (elements[i].equals(obj)) {
                System.arraycopy(elements, i + 1, elements, i, currSize - i - 1);
                elements[currSize - 1] = null;
                currSize--;

                if (currIndex > i) {
                    currIndex--;
                }
                break;
            }
        }
    }

    public void removeAll(List<T> objsToRemove) {
        Set<T> toRemoveSet = new HashSet<>(objsToRemove);
        int newCurrSize = 0;

        for (int i = 0; i < currSize; i++) {
            if (!toRemoveSet.contains(elements[i])) {
                elements[newCurrSize++] = elements[i];
            }
        }

        for (int i = newCurrSize; i < currSize; i++) {
            elements[i] = null;
        }

        currSize = newCurrSize;

        if (currIndex >= currSize) {
            currIndex = currSize > 0 ? currIndex % currSize : 0;
        }
    }

    public boolean contains(T obj) {
        for (int i = 0; i < currSize; i++) {
            if (elements[i].equals(obj)) {
                return true;
            }
        }
        return false;
    }

    public void resetIndex() {
        currIndex = 0;
    }

    public void forAll(Consumer<T> action) {
        for (int i = 0; i < currSize; ++ i) {
            action.accept(elements[i]);
        }
    }

    public void forRemaining (Consumer<T> action) {
        for (int i = currIndex; i < currSize; i++) {
            action.accept(elements[i]);
        }
    }

    public void forN(Consumer<T> action, int itrN) {
        for(int i = 0; i < itrN; i++) {
            action.accept(getNext());
        }
    }


}
