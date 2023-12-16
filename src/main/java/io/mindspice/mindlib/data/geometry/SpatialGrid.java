package io.mindspice.mindlib.data.geometry;

import io.mindspice.mindlib.data.collections.other.GridArray;
import io.mindspice.mindlib.data.geometry.IMutRect2;
import io.mindspice.mindlib.data.geometry.IVector2;
import java.util.ArrayList;
import java.util.List;


public class SpatialGrid<T> {
    private final GridArray<List<T>> grid;
    private final int bucketWidth;
    private final int bucketHeight;

    public SpatialGrid(int width, int height, int bucketWidth, int bucketHeight) {
        this.bucketWidth = bucketWidth;
        this.bucketHeight = bucketHeight;
        int cols = (int) Math.ceil((double) width / bucketWidth);
        int rows = (int) Math.ceil((double) height / bucketHeight);
        this.grid = new GridArray<>(cols, rows);

        // Initialize each bucket with an empty list
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid.set(x, y, new ArrayList<T>());
            }
        }
    }

    public void insert(IVector2 point, T item) {
        int bucketX = point.x() / bucketWidth;
        int bucketY = point.y() / bucketHeight;
        grid.get(bucketX, bucketY).add(item);
    }

    public List<T> query(IMutRect2 rect) {
        List<T> result = new ArrayList<>();
        int startX = rect.topLeft().x() / bucketWidth;
        int endX = rect.topRight().x() / bucketWidth;
        int startY = rect.topLeft().y() / bucketHeight;
        int endY = rect.bottomLeft().y() / bucketHeight;

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                result.addAll(grid.get(x, y));
            }
        }

        return result;
    }

}
