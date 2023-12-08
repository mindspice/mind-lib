package io.mindspice.mindlib.data.geometry;

public class IMutVector2 implements Vector2 {
    private int x;
    private int y;

    public IMutVector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public IMutVector2(float x, float y) {
        x = (int) x;
        y = (int) y;
    }

    public IMutVector2(IVector2 other) {
        this.x = other.x();
        this.y = other.y();
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public IVector2 asIVector2() {
        return new IVector2(this);
    }

    public static IMutVector2 of(int x, int y) {
        return new IMutVector2(x, y);
    }

    public static IMutVector2 of(float x, float y) {
        return new IMutVector2(x, y);
    }

    @Override
    public IMutVector2 withXInc() {
        x += 1;
        return this;
    }

    @Override
    public IMutVector2 withYInc() {
        y += 1;
        return this;
    }

    @Override
    public IMutVector2 withXDec() {
        x -= 1;
        return this;
    }

    @Override
    public IMutVector2 withYDec() {
        y -= 1;
        return this;
    }

    @Override
    public IMutVector2 add(IMutVector2 other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    @Override
    public IMutVector2 add(IVector2 other) {
        this.x += other.x();
        this.y += other.y();
        return this;
    }

    @Override
    public IMutVector2 subtract(IMutVector2 other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    @Override
    public IMutVector2 subtract(IVector2 other) {
        this.x -= other.x();
        this.y -= other.y();
        return this;
    }

    @Override
    public IMutVector2 multiply(IMutVector2 other) {
        this.x *= other.x;
        this.y *= other.y;
        return this;
    }

    @Override
    public IMutVector2 multiply(IVector2 other) {
        this.x *= other.x();
        this.y *= other.y();
        return this;
    }

    @Override
    public IMutVector2 divide(IMutVector2 other) {
        this.x /= other.x;
        this.y /= other.y;
        return this;
    }

    @Override
    public IMutVector2 divide(IVector2 other) {
        this.x /= other.x();
        this.y /= other.y();
        return this;
    }

    @Override
    public IMutVector2 scalarMultiplication(int scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    @Override
    public int dotProduct(IMutVector2 other) {
        return (this.x * other.x) + (this.y * other.y);
    }

    @Override
    public int dotProduct(IVector2 other) {
        return (this.x * other.x()) + (this.y * other.y());
    }

    @Override
    public double magnitude() {
        return Math.sqrt((x * x) + (y * y));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        IMutVector2 iVector2 = (IMutVector2) o;
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