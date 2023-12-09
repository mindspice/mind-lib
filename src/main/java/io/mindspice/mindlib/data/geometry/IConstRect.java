package io.mindspice.mindlib.data.geometry;

public record IConstRect(
        IVector2 start,
        IVector2 end,
        IVector2 size
) implements IRect2 {

    IConstRect(int x, int y, int width, int height) {
        this(
                new IConstVector2(x, y),
                new IConstVector2(x + width, y + height),
                new IConstVector2(width, height)
        );
    }

    IConstRect(IVector2 start, IVector2 size) {
        this(
                start,
                new IConstVector2(start.x() + size.x(), start.y() + size.y()),
                size
        );
    }

    IConstRect(IMutRec2 other) {
        this(
                other.start().asImmutable(),
                other.end().asImmutable(),
                other.size().asImmutable()
        );
    }

    public IMutRec2 asIMutRect() {
        return new IMutRec2(this);
    }

    @Override
    public IVector2 getCenter() {
        return new IConstVector2((start.x() + end.x()) / 2, (start.y() + end.y()) / 2);
    }

    @Override
    public boolean withinBounds(IVector2 pos) {
        return pos.x() >= start.x() && pos.x() <= end.x() && pos.y() >= start.y() && pos.y() <= end.y();
    }

    @Override
    public boolean withinBounds(int x, int y) {
        return x >= start.x() && x <= end.x() && y >= start.y() && y <= end.y();
    }

    @Override
    public boolean intersects(IRect2 other) {
        return !(other.end().x() < start.x()
                || other.start().x() > end.x()
                || other.end().y() < start.y()
                || other.start().y() > end.y()
        );
    }

    @Override
    public boolean intersectsLine(IVector2 lineStart, IVector2 lineEnd) {
        return lineStart.x() <= end.x() && lineEnd.x() >= start.x() && lineStart.y() <= end.y() && lineEnd.y() >= start.y();
    }

    @Override
    public IConstRect combine(IRect2 other) {
        IConstVector2 newStart = new IConstVector2(
                Math.min(start.x(), other.start().x()),
                Math.min(start.y(), other.start().y())
        );
        IConstVector2 newEnd = new IConstVector2(
                Math.max(end.x(), other.end().x()),
                Math.max(end.y(), other.end().y())
        );
        return new IConstRect(
                newStart,
                newEnd,
                new IConstVector2(newEnd.x() - newStart.x(), newEnd.y() - newStart.y())
        );
    }

    @Override
    public String toString() {
        return "(" + start + ", " + size + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        IConstRect iConstRect = (IConstRect) o;

        if (!start.equals(iConstRect.start)) { return false; }
        if (!end.equals(iConstRect.end)) { return false; }
        return size.equals(iConstRect.size);
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + size.hashCode();
        return result;
    }
}
