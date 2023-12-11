package io.mindspice.mindlib.data.geometry;

import java.awt.*;
import java.util.Objects;
import java.util.Vector;


public record IConstLine2(
        IVector2 start,
        IVector2 end
) implements ILine2 {

    IConstLine2(int startX, int startY, int endX, int endY) {
        this(IVector2.of(startX, startY), IVector2.of(endX, endY));
    }

    @Override
    public IVector2 start() {
        return start;
    }

    @Override
    public IVector2 end() {
        return end;
    }

    @Override
    public boolean intersects(ILine2 otherLine) {
        // Find the four orientations needed for general and
        // special cases
        int o1 = orientation(start(), end(), otherLine.start());
        int o2 = orientation(start(), end(), otherLine.end());
        int o3 = orientation(otherLine.start(), otherLine.end(), start());
        int o4 = orientation(otherLine.start(), otherLine.end(), end());

        // General case
        if (o1 != o2 && o3 != o4) { return true; }

        // Special Cases
        // start(), end() and otherLine.start() are collinear and otherLine.start() lies on segment start()end()
        if (o1 == 0 && onSegment(start(), otherLine.start(), end())) { return true; }

        // start(), end() and otherLine.end() are collinear and otherLine.end() lies on segment start()end()
        if (o2 == 0 && onSegment(start(), otherLine.end(), end())) { return true; }

        // otherLine.start(), otherLine.end() and start() are collinear and start() lies on segment otherLine.start()otherLine.end()
        if (o3 == 0 && onSegment(otherLine.start(), start(), otherLine.end())) { return true; }

        // otherLine.start(), otherLine.end() and end() are collinear and end() lies on segment otherLine.start()otherLine.end()
        if (o4 == 0 && onSegment(otherLine.start(), end(), otherLine.end())) { return true; }

        return false; // Doesn't fall in any of the above cases
    }

    private int orientation(IVector2 p, IVector2 q, IVector2 r) {
        int val = (q.y() - p.y()) * (r.x() - q.x()) -
                (q.x() - p.x()) * (r.y() - q.y());

        if (val == 0) {
            return 0; // collinear
        }

        return (val > 0) ? 1 : 2; // clock or counterclock wise
    }

    private boolean onSegment(IVector2 p, IVector2 q, IVector2 r) {
        if (q.x() <= Math.max(p.x(), r.x()) && q.x() >= Math.min(p.x(), r.x()) &&
                q.y() <= Math.max(p.y(), r.y()) && q.y() >= Math.min(p.y(), r.y())) { return true; }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof ILine2 iLine2)) { return false; }
        if (!start.equals(iLine2.start())) { return false; }
        return end.equals(iLine2.end());
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "[" + start + ", " + end + "]";
    }
}
