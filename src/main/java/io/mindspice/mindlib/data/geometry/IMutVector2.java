package io.mindspice.mindlib.data.geometry;

public class IMutVector2 implements IVector2 {
    private int x;
    private int y;

    IMutVector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    IMutVector2(float x, float y) {
        this.x = (int) x;
        this.y = (int) y;
    }

    IMutVector2(IVector2 other) {
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


    public IMutVector2 setXY(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public IConstVector2 asImmutable() {
        return new IConstVector2(this);
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
    public IMutVector2 add(IVector2 other) {
        this.x += other.x();
        this.y += other.y();
        return this;
    }

    @Override
    public IVector2 add(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    public IMutVector2 subtract(IVector2 other) {
        this.x -= other.x();
        this.y -= other.y();
        return this;
    }

    @Override
    public IVector2 subtract(int x, int y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    @Override
    public IMutVector2 multiply(IVector2 other) {
        this.x *= other.x();
        this.y *= other.y();
        return this;
    }

    @Override
    public IVector2 multiply(int x, int y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    @Override
    public IMutVector2 divide(IVector2 other) {
        this.x /= other.x();
        this.y /= other.y();
        return this;
    }

    @Override
    public IVector2 divide(int x, int y) {
        this.x /= x;
        this.y /= y;
        return this;
    }

    @Override
    public IMutVector2 scale(int scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    @Override
    public IVector2 modulo(int divisor) {
        this.x %= divisor;
        this.y %= divisor;
        return this;
    }

    @Override
    public IVector2 normalize() {
        double magnitude = magnitude();
        if (magnitude == 0) {
            x = 0;
            y = 0;
            return this;
        }
        x = (int) (x / magnitude);
        y = (int) (y / magnitude);
        return this;
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