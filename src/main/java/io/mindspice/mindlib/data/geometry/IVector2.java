package io.mindspice.mindlib.data.geometry;

public interface IVector2 {
    IVector2 withXInc();

    IVector2 withYInc();

    IVector2 withXDec();

    IVector2 withYDec();

    IVector2 add(IVector2 other);

    IVector2 add(int x, int y);

    IVector2 setXY(int x, int y);

    IVector2 setXY(IVector2 other);

    IVector2 subtract(IVector2 other);

    IVector2 subtract(int x, int y);

    IVector2 multiply(IVector2 other);

    IVector2 multiply(int x, int y);

    IVector2 divide(IVector2 other);

    IVector2 divide(int x, int y);

    IVector2 scale(int scalar);

    IVector2 modulo(int divisor);

    IVector2 normalize();

    int dotProduct(IVector2 other);

    int dotProduct(int x, int y);

    double magnitude();

    float distanceTo(IVector2 other);

    int distanceToInt(IVector2 other);

    int x();

    int y();

    static IVector2 of(int x, int y) {
        return new IConstVector2(x, y);
    }

    static IVector2 of(float x, float y) {
        return new IConstVector2(x, y);
    }

    static IVector2 of(IVector2 other) {
        return new IConstVector2(other);
    }

    static IMutVector2 ofMutable(int x, int y) {
        return new IMutVector2(x, y);
    }

    static IMutVector2 ofMutable(float x, float y) {
        return new IMutVector2(x, y);
    }

    static IMutVector2 ofMutable(IVector2 other) {
        return new IMutVector2(other);
    }

    static IAtomicVector2 ofAtomic(int x, int y) {
        return new IAtomicVector2(x, y);
    }

    static IAtomicVector2 ofAtomic(float x, float y) {
        return new IAtomicVector2(x, y);
    }

    static IAtomicVector2 ofAtomic(IVector2 other) {
        return new IAtomicVector2(other);
    }

    static IVector2 lerp(IVector2 start, IVector2 end, float t) {
        float x = start.x() + t * (end.x() - start.x());
        float y = start.y() + t * (end.y() - start.y());
        return IVector2.of(x, y);
    }

    static IMutVector2 mutZero() {
        return new IMutVector2(0, 0);
    }

    static IConstVector2 zero() {
        return new IConstVector2(0, 0);
    }

    static IMutVector2 mutNegOne() {
        return new IMutVector2(-1, -1);
    }

    static IConstVector2 negOne() {
        return new IConstVector2(-1, -1);
    }


}
