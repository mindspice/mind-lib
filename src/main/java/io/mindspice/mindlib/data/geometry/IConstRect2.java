package io.mindspice.mindlib.data.geometry;

public record IConstRect2(
        IVector2 start,
        IVector2 end,
        IVector2 size,
        IVector2 topRight,
        IVector2 bottomLeft
) implements IRect2 {

    IConstRect2(int x, int y, int width, int height) {
        this(
                new IConstVector2(x, y),
                new IConstVector2(x + width, y + height),
                new IConstVector2(width, height),
                new IConstVector2(x + width, y),
                new IConstVector2(x, y + height)
        );
    }

    IConstRect2(IVector2 start, IVector2 size) {
        this(
                start,
                new IConstVector2(start.x() + size.x(), start.y() + size.y()),
                size,
                new IConstVector2(start.x() + size.x(), start.y()),
                new IConstVector2(start.x(), start.y() + size.y())
        );
    }

    IConstRect2(IRect2 other) {
        this(
                IVector2.of(other.start()),
                IVector2.of(other.end()),
                IVector2.of(other.size()),
                IVector2.of(other.topRight()),
                IVector2.of(other.bottomLeft())
        );
    }

    public IMutRect2 asIMutRect() {
        return new IMutRect2(this);
    }

    @Override
    public IVector2 getCenter() {
        return new IConstVector2((start.x() + end.x()) / 2, (start.y() + end.y()) / 2);
    }

    @Override
    public boolean contains(IVector2 pos) {
        return pos.x() >= start.x() && pos.x() <= end.x() && pos.y() >= start.y() && pos.y() <= end.y();
    }

    @Override
    public boolean contains(int x, int y) {
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
    public IConstRect2 combine(IRect2 other) {
        IMutVector2 newStart = new IMutVector2(
                Math.min(start.x(), other.start().x()),
                Math.min(start.y(), other.start().y())
        );
        IMutVector2 newEnd = new IMutVector2(
                Math.max(end.x(), other.end().x()),
                Math.max(end.y(), other.end().y())
        );
        IMutVector2 newSize = new IMutVector2(newEnd.x() - newStart.x(), newEnd.y() - newStart.y());
        IMutVector2 newTopRight = new IMutVector2(newStart.x() + newSize.x(), newStart.y());
        IMutVector2 newBottomLeft = new IMutVector2(newStart.x(), newStart.y() + newSize.y());

        return new IConstRect2(newStart, newEnd, newSize, newTopRight, newBottomLeft);
    }

    @Override
    public IVector2 topLeft() {
        return start;
    }

    @Override
    public IVector2 topRight() {
        return topRight;
    }

    @Override
    public IVector2 bottomLeft() {
        return bottomLeft;
    }

    @Override
    public IVector2 bottomRight() {
        return end;
    }

    @Override
    public ILine2 leftEdge() {
        return new IConstLine2(start.x(), start.y(), start.x(), start.y() + size().y());
    }

    @Override
    public ILine2 rightEdge() {
        return new IConstLine2(end.x(), start.y(), end.x(), start.y() + size().y());    }

    @Override
    public ILine2 topEdge() {
        return new IConstLine2(start.x(), start.y(), end.x(), start.y());
    }
    @Override
    public ILine2 bottomEdge() {
        return new IConstLine2(start.x(), start.y() + size.y(), end.x(), start.y() + size.y());
    }

    @Override
    public String toString() {
        return "(" + start + ", " + size + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof IRect2 iRect2)) { return false; }
        if (!start.equals(iRect2.start())) { return false; }
        if (!end.equals(iRect2.end())) { return false; }
        return size.equals(iRect2.size());
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + size.hashCode();
        return result;
    }
}
