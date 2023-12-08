package io.mindspice.mindlib.data.geometry;

public interface Vector2 {
    Vector2 withXInc();

    Vector2 withYInc();

    Vector2 withXDec();

    Vector2 withYDec();

    Vector2 add(IVector2 other);

    Vector2 add(IMutVector2 other);

    Vector2 subtract(IVector2 other);

    Vector2 subtract(IMutVector2 other);

    Vector2 multiply(IVector2 other);

    Vector2 multiply(IMutVector2 other);

    Vector2 divide(IVector2 other);

    Vector2 divide(IMutVector2 other);

    Vector2 scalarMultiplication(int scalar);

    int dotProduct(IVector2 other);

    int dotProduct(IMutVector2 other);

    double magnitude();

    int x();

    int y();
}
