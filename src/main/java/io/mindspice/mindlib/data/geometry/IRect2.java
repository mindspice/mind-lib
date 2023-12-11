package io.mindspice.mindlib.data.geometry;

public interface IRect2 {

    IVector2 getCenter();

    boolean withinBounds(IVector2 pos);

    boolean withinBounds(int x, int y);

    boolean intersects(IRect2 other);

    boolean intersectsLine(IVector2 lineStart, IVector2 lineEnd);

    IRect2 combine(IRect2 other);

    IVector2 start();

    IVector2 end();

    IVector2 size();

    static IRect2 of(int x, int y, int width, int height) {
        return new IConstRect2(new IConstVector2(x, y), new IConstVector2(width, height));
    }

    static IRect2 of(IVector2 start, IVector2 size) {
        return new IConstRect2(start, size);
    }

    static IMutRec2 ofMutable(int x, int y, int width, int height) {
        return new IMutRec2(new IConstVector2(x, y), new IConstVector2(width, height));
    }

    static IMutRec2 ofMutable(IVector2 start, IVector2 size) {
        return new IMutRec2(start, size);
    }

    static IRect2 fromCenter(IVector2 center, IVector2 size) {
        return new IConstRect2(
                new IConstVector2(center.x() - (size.x() / 2), center.y() - (size.y() / 2)),
                size
        );
    }

    static IRect2 fromCenter(int x, int y, int width, int height) {
        return new IConstRect2(new IConstVector2(x - (width / 2), y + (height / 2)), new IConstVector2(width, height));
    }

    static IMutRec2 fromCenterMutable(IVector2 center, IVector2 size) {
        return new IMutRec2(
                new IConstVector2(center.x() - (size.x() / 2), center.y() - (size.y() / 2)),
                size
        );
    }

    static IMutRec2 fromCenterMutable(int x, int y, int width, int height) {
        return new IMutRec2(new IConstVector2(x - (width / 2), y + (height / 2)), new IConstVector2(width, height));
    }


}
