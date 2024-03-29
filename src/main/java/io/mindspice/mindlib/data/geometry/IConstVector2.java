package io.mindspice.mindlib.data.geometry;

public record IConstVector2(
        int x,
        int y
) implements IVector2 {

    IConstVector2(float x, float y) {
        this((int) x, (int) y);
    }

    IConstVector2(IVector2 other) {
        this(other.x(), other.y());
    }

    public IMutVector2 asMutable() {
        return new IMutVector2(this);
    }

    @Override
    public IConstVector2 withXInc() {
        return new IConstVector2(x + 1, y);
    }

    @Override
    public IConstVector2 withYInc() {
        return new IConstVector2(x, y + 1);
    }

    @Override
    public IConstVector2 withXDec() {
        return new IConstVector2(x - 1, y);
    }

    @Override
    public IConstVector2 withYDec() {
        return new IConstVector2(x, y - 1);
    }

    @Override
    public IConstVector2 add(IVector2 other) {
        return new IConstVector2(this.x + other.x(), this.y + other.y());
    }

    @Override
    public IVector2 setXY(int x, int y) {
        return IVector2.of(x, y);
    }

    @Override
    public IVector2 setXY(IVector2 other) {
        return IVector2.of(other);
    }

    @Override
    public IConstVector2 add(int x, int y) {
        return new IConstVector2(this.x + x, this.y + y);
    }

    @Override
    public IConstVector2 subtract(IVector2 other) {
        return new IConstVector2(this.x - other.x(), this.y - other.y());
    }

    @Override
    public IVector2 subtract(int x, int y) {
        return new IConstVector2(this.x - x, this.y - y);
    }

    @Override
    public IConstVector2 multiply(IVector2 other) {
        return new IConstVector2(this.x * other.x(), this.y * other.y());
    }

    @Override
    public IVector2 multiply(int x, int y) {
        return new IConstVector2(this.x * x, this.y * y);

    }

    @Override
    public IConstVector2 divide(IVector2 other) {
        return new IConstVector2(this.x / other.x(), this.y / other.y());
    }

    @Override
    public IVector2 divide(int x, int y) {
        return new IConstVector2(this.x / x, this.y / y);

    }

    @Override
    public IConstVector2 scale(int scalar) {
        return new IConstVector2(this.x * scalar, this.y * scalar);
    }

    @Override
    public IVector2 modulo(int divisor) {
        return new IConstVector2(this.x % divisor, this.y % divisor);
    }

    @Override
    public IVector2 normalize() {
        double magnitude = magnitude();
        if (magnitude == 0) {
            return new IConstVector2(0, 0); // Avoid division by zero; return zero vector
        }
        return new IConstVector2((float) (x / magnitude), (float) (y / magnitude));
    }

    @Override
    public int dotProduct(IVector2 other) {
        return (this.x * other.x()) + (this.y * other.y());
    }

    @Override
    public int dotProduct(int x, int y) {
        return (this.x * x) + (this.y * y);
    }

    @Override
    public double magnitude() {
        return Math.sqrt((x * x) + (y * y));
    }

    @Override
    public float distanceTo(IVector2 other) {
        float dx = this.x - other.x();
        float dy = this.y - other.y();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public int distanceToInt(IVector2 other) {
        float dx = this.x - other.x();
        float dy = this.y - other.y();
        return (int) Math.round(Math.sqrt(dx * dx + dy * dy));
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof IVector2 iVector2)) { return false; }
        if (x != iVector2.x()) { return false; }
        return y == iVector2.y();
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}