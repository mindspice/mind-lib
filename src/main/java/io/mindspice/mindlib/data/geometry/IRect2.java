package io.mindspice.mindlib.data.geometry;

public interface IRect2 {

    IVector2 getCenter();

    boolean contains(IVector2 pos);

    boolean contains(int x, int y);

    boolean intersects(IRect2 other);

    boolean intersectsLine(IVector2 lineStart, IVector2 lineEnd);

    IRect2 combine(IRect2 other);

    IVector2 start();

    IVector2 end();

    IVector2 size();

    IVector2 topLeft();

    IVector2 topRight();

    IVector2 bottomLeft();

    IVector2 bottomRight();

    ILine2 leftEdge();

    ILine2 rightEdge();

    ILine2 topEdge();

    ILine2 bottomEdge();

    static IRect2 of(int x, int y, int width, int height) {
        return new IConstRect2(x, y, width, height);
    }

    static IRect2 of(IVector2 start, IVector2 size) {
        return new IConstRect2(start, size);
    }

    static IRect2 of(IRect2 rect) {
        return new IConstRect2(rect);
    }

    static IMutRect2 ofMutable(int x, int y, int width, int height) {
        return new IMutRect2(x, y, width, height);
    }

    static IMutRect2 ofMutable(IVector2 start, IVector2 size) {
        return new IMutRect2(start, size);
    }

    static IAtomicRect2 ofAtomic(int x, int y, int width, int height) {
        return new IAtomicRect2(x, y, width, height);
    }

    static IAtomicRect2 ofAtomic(IVector2 start, IVector2 size) {
        return new IAtomicRect2(start, size);
    }

    static IRect2 fromCenter(IVector2 center, IVector2 size) {
        return new IConstRect2(center, size);
    }

    static IRect2 fromCenter(int x, int y, int width, int height) {
        return new IConstRect2(x, y, width, height);
    }

    static IAtomicRect2 fromCenterAtomic(IVector2 center, IVector2 size) {
        return new IAtomicRect2(center, size);
    }

    static IAtomicRect2 fromCenterAtomic(int x, int y, int width, int height) {
        return new IAtomicRect2(x, y, width, height);
    }

    static IMutRect2 fromCenterMutable(IVector2 center, IVector2 size) {
        return new IMutRect2(center, size);
    }

    static IMutRect2 fromCenterMutable(int x, int y, int width, int height) {
        return new IMutRect2(x, y, width, height);
    }


}
