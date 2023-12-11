package io.mindspice.mindlib.data.geometry;

public interface ILine2 {

    IVector2 start();

    IVector2 end();

    boolean intersects(ILine2 other);

    static IConstLine2 of(int startX, int startY, int endX, int endY) {
        return new IConstLine2(startX, startY, endX, endY);
    }

    static IConstLine2 of(IVector2 start, IVector2 end) {
        return new IConstLine2(start, end);
    }

    static IMutLine2 ofMutable(int startX, int startY, int endX, int endY) {
        return new IMutLine2(startX, startY, endX, endY);
    }

    static IMutLine2 ofMutable(IVector2 start, IVector2 end) {
        return new IMutLine2(start, end);
    }
}
