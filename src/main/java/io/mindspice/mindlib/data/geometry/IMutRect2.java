package io.mindspice.mindlib.data.geometry;

public class IMutRect2 implements IRect2 {
    private IMutVector2 start;
    private IMutVector2 end;
    private IMutVector2 size;
    private IMutVector2 bottomLeft;
    private IMutVector2 bottomRight;

    IMutRect2(IVector2 start, IVector2 end, IVector2 size) {
        this.start = IVector2.ofMutable(start);
        this.end = IVector2.ofMutable(end);
        this.size = IVector2.ofMutable(size);
        this.bottomLeft = IVector2.ofMutable(start.x(), start.y() + size.y());
        this.bottomRight = IVector2.ofMutable(end.x(), end.y() + size.y());
    }

    IMutRect2(int x, int y, int width, int height) {
        start = new IMutVector2(x, y);
        end = new IMutVector2(x + width, y + height);
        size = new IMutVector2(width, height);
        bottomLeft = IVector2.ofMutable(start.x(), start.y() + size.y());
        bottomRight = IVector2.ofMutable(end.x(), end.y() + size.y());
    }

    IMutRect2(IVector2 start, IVector2 size) {
        this.start = new IMutVector2(start);
        end = new IMutVector2(start.x() + size.x(), start.y() + size.y());
        this.size = new IMutVector2(size);
        bottomLeft = IVector2.ofMutable(start.x(), start.y() + size.y());
        bottomRight = IVector2.ofMutable(end.x(), end.y() + size.y());
    }

    IMutRect2(IRect2 other) {
        this.start = new IMutVector2(other.start());
        this.end = new IMutVector2(other.end());
        this.size = new IMutVector2(other.size());
        bottomLeft = IVector2.ofMutable(start.x(), start.y() + size.y());
        bottomRight = IVector2.ofMutable(end.x(), end.y() + size.y());
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

    public IMutRect2 reCenter(int x, int y) {
        start.setX(x - (size.x() / 2));
        start.setY(y - (size.y() / 2));
        end.setX(start.x() + size.x());
        end.setY(start.y() + size.y());
        reCalcBottomCorners();
        return this;
    }

    public IMutRect2 reCenter(IVector2 center) {
        start.setX(center.x() - (size.x() / 2));
        start.setY(center.y() - (size.y() / 2));
        end.setX(start.x() + size.x());
        end.setY(start.y() + size.y());
        reCalcBottomCorners();
        return this;
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

    public IMutRect2 combineMutable(IRect2 other) {
        return (IMutRect2) this.combine(other);
    }

    private void reCalcBottomCorners() {
        bottomLeft.setXY(start.x(), start.y() + size.y());
        bottomRight.setXY(end.x(), end.y() + size.y());
    }

    @Override
    public IVector2 topLeft() {
        return start;
    }

    @Override
    public IVector2 topRight() {
        return end;
    }

    @Override
    public IVector2 bottomLeft() {
        return bottomLeft;
    }

    @Override
    public IVector2 bottomRight() {
        return bottomRight;
    }

    @Override
    public ILine2 leftEdge() {
        return new IConstLine2(start.x(), start.y(), start.x(), start.y() + size().y());
    }

    @Override
    public ILine2 rightEdge() {
        return new IConstLine2(end.x(), end.y(), end.x(), end.y() + size().y());
    }

    @Override
    public ILine2 topEdge() {
        return new IConstLine2(start.x(), start.y(), end.x(), end.y());
    }

    @Override
    public ILine2 bottomEdge() {
        return new IConstLine2(start.x(), start.y() + size().y(), end.x(), end.y() + size().y());
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
