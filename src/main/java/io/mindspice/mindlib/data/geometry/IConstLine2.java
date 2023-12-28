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
    public double distance() {
        double deltaX = end.x() - start.x();
        double deltaY = end.y() - start.y();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    @Override
    public boolean isPointLine() {
        return start.equals(end);
    }

//    @Override
//    public boolean intersects(ILine2 otherLine) {
//        // Find the four orientations needed for general and
//        // special cases
//        int o1 = orientation(start(), end(), otherLine.start());
//        int o2 = orientation(start(), end(), otherLine.end());
//        int o3 = orientation(otherLine.start(), otherLine.end(), start());
//        int o4 = orientation(otherLine.start(), otherLine.end(), end());
//
//        // General case
//        if (o1 != o2 && o3 != o4) { return true; }
//
//        // Special Cases
//        // start(), end() and otherLine.start() are collinear and otherLine.start() lies on segment start()end()
//        if (o1 == 0 && onSegment(start(), otherLine.start(), end())) { return true; }
//
//        // start(), end() and otherLine.end() are collinear and otherLine.end() lies on segment start()end()
//        if (o2 == 0 && onSegment(start(), otherLine.end(), end())) { return true; }
//
//        // otherLine.start(), otherLine.end() and start() are collinear and start() lies on segment otherLine.start()otherLine.end()
//        if (o3 == 0 && onSegment(otherLine.start(), start(), otherLine.end())) { return true; }
//
//        // otherLine.start(), otherLine.end() and end() are collinear and end() lies on segment otherLine.start()otherLine.end()
//        if (o4 == 0 && onSegment(otherLine.start(), end(), otherLine.end())) { return true; }
//
//        return false; // Doesn't fall in any of the above cases
//    }

    @Override
    public boolean intersects(ILine2 otherLine) {
        int x1 = this.start().x();
        int y1 = this.start().y();
        int x2 = this.end().x();
        int y2 = this.end().y();
        int x3 = otherLine.start().x();
        int y3 = otherLine.start().y();
        int x4 = otherLine.end().x();
        int y4 = otherLine.end().y();

        int dx1 = x2 - x1;
        int dy1 = y2 - y1;
        int dx2 = x4 - x3;
        int dy2 = y4 - y3;

        int delta = dx1 * dy2 - dx2 * dy1;
        if (delta == 0) {
            // Segments are on parallel lines. Check if they are collinear and overlapping
            boolean segmentsCollinear = (y3 - y1) * dx1 == dy1 * (x3 - x1);
            boolean collisionOx = Math.max(x1, x2) >= Math.min(x3, x4) && Math.min(x1, x2) <= Math.max(x3, x4);
            boolean collisionOy = Math.max(y1, y2) >= Math.min(y3, y4) && Math.min(y1, y2) <= Math.max(y3, y4);
            return segmentsCollinear && collisionOx && collisionOy;
        }

        double s = (double) (dx1 * (y1 - y3) - dy1 * (x1 - x3)) / delta;
        double t = (double) (dx2 * (y1 - y3) - dy2 * (x1 - x3)) / delta;

        return 0 <= s && s <= 1 && 0 <= t && t <= 1;
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
