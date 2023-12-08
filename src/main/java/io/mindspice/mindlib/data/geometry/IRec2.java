package io.mindspice.mindlib.data.geometry;

public record IRec2(
        IVector2 start,
        IVector2 end,
        IVector2 size
) {
    public IRec2(IMutVector2 start, IMutVector2 end, IMutVector2 size) {
        this(
                start.asIVector2(),
                end.asIVector2(),
                size.asIVector2()
        );
    }

    public IRec2(int x, int y, int width, int height) {
        this(
                new IVector2(x, y),
                new IVector2(x + width, y + height),
                new IVector2(width, height)
        );
    }

    public IRec2(IVector2 start, IVector2 size) {
        this(
                start,
                new IVector2(start.x() + size.x(), start.y() + size.y()),
                size
        );
    }

    public IRec2(IMutVector2 start, IMutVector2 size) {
        this(
                start.asIVector2(),
                new IVector2(start.x() + size.x(), start.y() + size.y()),
                size.asIVector2()
        );
    }

    public IRec2(IMutRec2 other) {
        this(
                other.start().asIVector2(),
                other.end().asIVector2(),
                other.size().asIVector2()
        );
    }

    public static IRec2 of(int x, int y, int width, int height) {
        return new IRec2(new IVector2(x, y), new IVector2(width, height));
    }

    public static IRec2 of(IVector2 start, IVector2 size) {
        return new IRec2(start, size);
    }

    public static IRec2 of(IMutVector2 start, IMutVector2 size) {
        return new IRec2(start.asIVector2(), size.asIVector2());
    }

    public IMutRec2 asIMutRect() {
        return new IMutRec2(this);
    }

    public IRec2 fromCenter(IVector2 center, IVector2 size) {
        return new IRec2(
                new IVector2(center.x() - (size.x() / 2), center.y() - (size.y() / 2)),
                size
        );
    }

    public IRec2 fromCenter(IMutVector2 center, IMutVector2 size) {
        return new IRec2(
                new IVector2(center.x() - (size.x() / 2), center.y() - (size.y() / 2)),
                size.asIVector2()
        );
    }

    public IRec2 fromCenter(int x, int y, int width, int height) {
        return new IRec2(new IVector2(x - (width / 2), y + (height / 2)), new IVector2(width, height));
    }

    public boolean withinBounds(IMutVector2 pos) {
        return pos.x() >= start.x() && pos.x() <= end.x() && pos.y() >= start.y() && pos.y() <= end.y();
    }

    public boolean withinBounds(IVector2 pos) {
        return pos.x() >= start.x() && pos.x() <= end.x() && pos.y() >= start.y() && pos.y() <= end.y();
    }

    public boolean withinBounds(int x, int y) {
        return x >= start.x() && x <= end.x() && y >= start.y() && y <= end.y();
    }

    @Override
    public String toString() {
        return "(" + start + "), (" + size + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        IRec2 iRec2 = (IRec2) o;

        if (!start.equals(iRec2.start)) { return false; }
        if (!end.equals(iRec2.end)) { return false; }
        return size.equals(iRec2.size);
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + size.hashCode();
        return result;
    }
}
