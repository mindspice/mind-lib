package io.mindspice.mindlib.data.geometry;

public record IVector2(
        int x,
        int y
) implements Vector2 {

    public IVector2(float x, float y) {
        this((int) x, (int) y);
    }

    public IVector2(IMutVector2 other) {
        this(other.x(), other.y());
    }

    public static IVector2 of(int x, int y) {
        return new IVector2(x, y);
    }

    public static IVector2 of(float x, float y) {
        return new IVector2(x, y);
    }

    public IMutVector2 asIMutVector2() {
        return new IMutVector2(this);
    }

    @Override
    public IVector2 withXInc() {
        return new IVector2(x + 1, y);
    }

    @Override
    public IVector2 withYInc() {
        return new IVector2(x, y + 1);
    }

    @Override
    public IVector2 withXDec() {
        return new IVector2(x - 1, y);
    }

    @Override
    public IVector2 withYDec() {
        return new IVector2(x, y - 1);
    }

    @Override
    public IVector2 add(IVector2 other) {
        return new IVector2(this.x + other.x, this.y + other.y);
    }

    @Override
    public IVector2 add(IMutVector2 other) {
        return new IVector2(this.x + other.x(), this.y + other.y());
    }

    @Override
    public IVector2 subtract(IVector2 other) {
        return new IVector2(this.x - other.x, this.y - other.y);
    }

    @Override
    public IVector2 subtract(IMutVector2 other) {
        return new IVector2(this.x - other.x(), this.y - other.y());
    }

    @Override
    public IVector2 multiply(IVector2 other) {
        return new IVector2(this.x * other.x, this.y * other.y);
    }

    @Override
    public IVector2 multiply(IMutVector2 other) {
        return new IVector2(this.x * other.x(), this.y * other.y());
    }

    @Override
    public IVector2 divide(IVector2 other) {
        return new IVector2(this.x / other.x, this.y / other.y);
    }

    @Override
    public IVector2 divide(IMutVector2 other) {
        return new IVector2(this.x / other.x(), this.y / other.y());
    }

    @Override
    public IVector2 scalarMultiplication(int scalar) {
        return new IVector2(this.x * scalar, this.y * scalar);
    }

    @Override
    public int dotProduct(IVector2 other) {
        return (this.x * other.x) + (this.y * other.y);
    }

    @Override
    public int dotProduct(IMutVector2 other) {
        return (this.x * other.x()) + (this.y * other.y());
    }

    @Override
    public double magnitude() {
        return Math.sqrt((x * x) + (y * y));
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        IVector2 iVector2 = (IVector2) o;
        if (x != iVector2.x) { return false; }
        return y == iVector2.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}