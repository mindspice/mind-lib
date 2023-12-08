package io.mindspice.mindlib.data.geometry;

public class IMutRec2 {
    private IMutVector2 start;
    private IMutVector2 end;
    private IMutVector2 size;

    public IMutRec2(IMutVector2 start, IMutVector2 end, IMutVector2 size) {
        this.start = start;
        this.size = size;
        this.size = size;
    }

    public IMutRec2(IVector2 start, IVector2 end, IVector2 size) {
        this.start = start();
        this.size = end();
        this.size = size();
    }

    public IMutRec2(int x, int y, int width, int height) {
        start = new IMutVector2(x, y);
        end = new IMutVector2(x + width, y + height);
        size = new IMutVector2(width, height);
    }

    public IMutRec2(IMutVector2 start, IMutVector2 size) {
        this.start = start;
        end = new IMutVector2(start.x() + size.x(), start.y() + size.y());
        this.size = size;
    }

    public IMutRec2(IVector2 start, IVector2 size) {
        this.start = start.asIMutVector2();
        new IMutVector2(start.x() + size.x(), start.y() + size.y());
        this.size = size.asIMutVector2();
    }

    public IMutRec2(IRec2 other) {
        this.start = other.start().asIMutVector2();
        this.end = other.end().asIMutVector2();
        this.size = other.size().asIMutVector2();
    }

    public static IMutRec2 of(int x, int y, int width, int height) {
        return new IMutRec2(x, y, width, height);
    }

    public static IMutRec2 of(IMutVector2 start, IMutVector2 size) {
        return new IMutRec2(start, size);
    }

    public static IMutRec2 of(IVector2 start, IVector2 size) {
        return new IMutRec2(start.asIMutVector2(), size.asIMutVector2());
    }

    public IRec2 asIRec2() {
        return new IRec2(this);
    }

    public IMutVector2 start() {
        return start;
    }

    public IMutVector2 end() {
        return end;
    }

    public IMutVector2 size() {
        return size;
    }

    public void setStart(int x, int y) {
        this.start = new IMutVector2(x, y);
    }

    public void setStart(IMutVector2 start) {
        this.start = start;
    }

    public void setStart(IVector2 start) {
        this.start = start.asIMutVector2();
    }

    public void setEnd(int x, int y) {
        this.end = new IMutVector2(x, y);
    }

    public void setEnd(IMutVector2 end) {
        this.end = end;
    }

    public void setEnd(IVector2 end) {
        this.end = end.asIMutVector2();
    }

    public void setSize(int x, int y) {
        this.size = new IMutVector2(x, y);
    }

    public void setSize(IMutVector2 size) {
        this.size = size;
    }

    public void setSize(IVector2 size) {
        this.size = size.asIMutVector2();
    }

    public static IMutRec2 fromCenter(IVector2 center, IVector2 size) {
        return new IMutRec2(
                new IVector2(center.x() - (size.x() / 2), center.y() - (size.y() / 2)),
                size
        );
    }

    public static IMutRec2 fromCenter(IMutVector2 center, IMutVector2 size) {
        return new IMutRec2(
                new IMutVector2(center.x() - (size.x() / 2), center.y() - (size.y() / 2)),
                size
        );
    }

    public static IMutRec2 fromCenter(int x, int y, int width, int height) {
        return new IMutRec2(
                new IMutVector2(x - (width / 2), y - (height / 2)),
                new IMutVector2(width, height)
        );
    }

    public IMutRec2 reCenter(int x, int y) {
        start.setX(x - (size.x() / 2));
        start.setY(y - (size.y() / 2));
        end.setX(start.x() + size.x());
        end.setY(start.y() + size.y());
        return this;
    }

    public IMutRec2 reCenter(IMutVector2 center) {
        start.setX(center.x() - (size.x() / 2));
        start.setY(center.y() - (size.y() / 2));
        end.setX(start.x() + size.x());
        end.setY(start.y() + size.y());
        return this;
    }

    public IMutRec2 reCenter(IVector2 center) {
        start.setX(center.x() - (size.x() / 2));
        start.setY(center.y() - (size.y() / 2));
        end.setX(start.x() + size.x());
        end.setY(start.y() + size.y());
        return this;
    }

    public boolean withinBounds(IVector2 pos) {
        return pos.x() >= start.x() && pos.x() <= end.x() && pos.y() >= start.y() && pos.y() <= end.y();
    }

    public boolean withinBounds(IMutVector2 pos) {
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

        IMutRec2 iMutRec2 = (IMutRec2) o;

        if (!start.equals(iMutRec2.start)) { return false; }
        if (!end.equals(iMutRec2.end)) { return false; }
        return size.equals(iMutRec2.size);
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + size.hashCode();
        return result;
    }
}
