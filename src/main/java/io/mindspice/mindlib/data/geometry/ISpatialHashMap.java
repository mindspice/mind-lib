package io.mindspice.mindlib.data.geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


// Pretty simple, could be more optimized
public class ISpatialHashMap<T> {
    private final Map<Integer, HashItem<T>[]> hashMap;
    private final int cellSize;
    private final IMutVector2 keyVector = IVector2.ofMutable(0, 0);

    public ISpatialHashMap(int cellSize) {
        this.hashMap = new ConcurrentHashMap<>();
        this.cellSize = cellSize;
    }

    public void insert(IVector2 position, T item) {
        int hash = getHash(position);
        HashItem<T>[] itemsInCell = hashMap.computeIfAbsent(hash, k -> createArray());
        // Add to array or expand it if necessary
        int index = 0;
        while (index < itemsInCell.length && itemsInCell[index] != null) {
            index++;
        }
        if (index < itemsInCell.length) {
            itemsInCell[index] = new HashItem<>(position, item);
        } else {
            // Resize array and add new item
            HashItem<T>[] newArray = createArray(itemsInCell.length * 2);
            System.arraycopy(itemsInCell, 0, newArray, 0, itemsInCell.length);
            newArray[itemsInCell.length] = new HashItem<>(position, item);
            hashMap.put(hash, newArray);
        }
    }

    public List<HashItem<T>> query(IRect2 searchArea) {
        List<HashItem<T>> foundItems = new ArrayList<>();
        for (int x = searchArea.start().x(); x <= searchArea.end().x(); x += cellSize) {
            for (int y = searchArea.start().y(); y <= searchArea.end().y(); y += cellSize) {
                keyVector.setXY(x, y);
                int hash = getHash(keyVector);
                HashItem<T>[] itemsInCell = hashMap.get(hash);
                if (itemsInCell != null) {
                    for (HashItem<T> item : itemsInCell) {
                        if (item != null && searchArea.contains(item.position)) {
                            foundItems.add(item);
                        }
                    }
                }
            }
        }
        return foundItems;
    }

    public void remove(IVector2 position, T item) {
        int hash = getHash(position);
        HashItem<T>[] itemsInCell = hashMap.get(hash);
        if (itemsInCell != null) {
            for (int i = 0; i < itemsInCell.length; i++) {
                if (itemsInCell[i] != null && itemsInCell[i].position.equals(position) && itemsInCell[i].item.equals(item)) {
                    itemsInCell[i] = null; // Remove item
                    break;
                }
            }
        }
    }

    private int getHash(IVector2 position) {
        int xHash = position.x() / cellSize;
        int yHash = position.y() / cellSize;
        return (xHash * 31 + yHash);
    }

    @SuppressWarnings("unchecked")
    private HashItem<T>[] createArray(int size) {
        return (HashItem<T>[]) new HashItem<?>[size];
    }

    private HashItem<T>[] createArray() {
        return createArray(10); // Default initial size
    }

    private static class HashItem<T> {
        IVector2 position;
        T item;

        HashItem(IVector2 position, T item) {
            this.position = position;
            this.item = item;
        }
    }
}
