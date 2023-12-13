package io.mindspice.mindlib.data.geometry;

public class IVolRec2 implements IRect2 {
    private IMutVector2 start;
    private IMutVector2 end;
    private IMutVector2 size;

    IVolRec2(IVector2 start, IVector2 end, IVector2 size) {
        this.start = IVector2.ofMutable(start);
        this.end = IVector2.ofMutable(end);
        this.size = IVector2.ofMutable(size);
    }

    IVolRec2(int x, int y, int width, int height) {
        start = new IMutVector2(x, y);
        end = new IMutVector2(x + width, y + height);
        size = new IMutVector2(width, height);
    }

    IVolRec2(IVector2 start, IVector2 size) {
        this.start = new IMutVector2(start);
        end = new IMutVector2(start.x() + size.x(), start.y() + size.y());
        this.size = new IMutVector2(size);
    }

    IVolRec2(IRect2 other) {
        this.start = new IMutVector2(other.start());
        this.end = new IMutVector2(other.end());
        this.size = new IMutVector2(other.size());
    }

    public IConstRect2 asIRec2() {
        return new IConstRect2(this);
    }

    @Override
    public IMutVector2 start() {
        return start;
    }

    @Override
    public IMutVector2 end() {
        return end;
    }

    @Override
    public IMutVector2 size() {
        return size;
    }

    public void setStart(int x, int y) {
        this.start.setXY(x, y);
    }

    public void setStart(IVector2 start) {
        this.start.setXY(start.x(), start.y());
    }

    public void setEnd(int x, int y) {
        this.end.setXY(x, y);
    }

    public void setEnd(IVector2 end) {
        this.end.setXY(end.x(), end.y());
    }

    public void setSize(int x, int y) {
        this.size.setXY(x, y);
    }

    public void setSize(IMutVector2 size) {
        this.size.setXY(size.x(), size.y());
    }

    public IVolRec2 reCenter(int x, int y) {
        start.setX(x - (size.x() / 2));
        start.setY(y - (size.y() / 2));
        end.setX(start.x() + size.x());
        end.setY(start.y() + size.y());
        return this;
    }

    public IVolRec2 reCenter(IVector2 center) {
        start.setX(center.x() - (size.x() / 2));
        start.setY(center.y() - (size.y() / 2));
        end.setX(start.x() + size.x());
        end.setY(start.y() + size.y());
        return this;
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
    public IRect2 combine(IRect2 other) {
        start.setXY(
                Math.min(start.x(), other.start().x()),
                Math.min(start.y(), other.start().y())
        );
        end.setXY(
                Math.max(end.x(), other.end().x()),
                Math.max(end.y(), other.end().y())
        );
        size.setXY(end.x() - start.x(), end.y() - start.y());
        return this;
    }

    public IVolRec2 combineMutable(IRect2 other) {
        return (IVolRec2) this.combine(other);
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
