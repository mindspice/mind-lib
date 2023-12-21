package io.mindspice.mindlib.data.geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public record IPolygon2(
        IVector2[] points
) {
    public boolean contains(IVector2 point) {
        boolean inside = false;
        int n = points.length;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            IVector2 pi = points[i];
            IVector2 pj = points[j];

            if (((pi.y() > point.y()) != (pj.y() > point.y())) &&
                    (point.x() < (pj.x() - pi.x()) * (point.y() - pi.y()) / (pj.y() - pi.y()) + pi.x())) {
                inside = !inside;
            }
            // Check for point on polygon edge
            if (pointOnLine(point, pi, pj)) {
                return true;
            }
        }
        return inside;
    }

    public static IPolygon2 of(IVector2[] points) {
        return new IPolygon2(points);
    }


    public static IPolygon2 of(List<Integer> points) {
        if (points.size() % 2 != 0) {
            throw new IllegalStateException("Must have an even numbers of x,y points");
        }
        IVector2[] pointArr = new IVector2[points.size() / 2];
        int p = 0;
        for (int i = 0; i < points.size() - 1; i += 2) {
            pointArr[p] = IVector2.of(points.get(i), points.get(i + 1));
            p++;
        }
        return new IPolygon2(pointArr);
    }

    public boolean intersects(int startX, int startY, int endX, int endY) {
        return intersects(ILine2.of(startX, startY, endX, endY));
    }

    public boolean intersects(ILine2 line) {
        IMutLine2 testLine = ILine2.ofMutable(points[0], points[1]);
        if (testLine.intersects(line)) { return true; }
        for (int i = 2; i < points.length; i++) {
            testLine.setStart(points[i - 1]);
            testLine.setEnd(points[i]);
            if (testLine.intersects(line)) { return true; }
        }
        testLine.setStart(points[points.length - 1]);
        testLine.setEnd(points[0]);
        return testLine.intersects(line);
    }

    public IRect2 boundingBox() {
        if (points == null || points.length == 0) {
            return IRect2.of(0, 0, 0, 0); // Or an empty IRect2, depending on your design
        }

        int minX = points[0].x();
        int minY = points[0].y();
        int maxX = minX;
        int maxY = minY;

        for (IVector2 point : points) {
            if (point.x() < minX) {
                minX = point.x();
            }
            if (point.x() > maxX) {
                maxX = point.x();
            }
            if (point.y() < minY) {
                minY = point.y();
            }
            if (point.y() > maxY) {
                maxY = point.y();
            }
        }
        return IRect2.of(minX, minY, maxX - minX, maxY - minY);
    }

    public boolean pointOnLine(IVector2 point, IVector2 lineStart, IVector2 lineEnd) {
        if (lineStart.y() == lineEnd.y() && lineStart.y() == point.y() &&
                (point.x() >= Math.min(lineStart.x(), lineEnd.x()) && point.x() <= Math.max(lineStart.x(), lineEnd.x()))) {
            return true;
        }
        return lineStart.x() == lineEnd.x() && lineStart.x() == point.x() &&
                (point.y() >= Math.min(lineStart.y(), lineEnd.y()) && point.y() <= Math.max(lineStart.y(), lineEnd.y()));
    }

    @Override
    public String toString() {
        if (points == null || points.length < 2) {
            return "[]";
        }
        StringBuilder s = new StringBuilder("{");
        IMutLine2 line = ILine2.ofMutable(points[0], points[1]);
        s.append(line);
        for (int i = 2; i < points.length; i++) {
            line.setStart(points[i - 1]);
            line.setEnd(points[i]);
            s.append(", ").append(line);
        }

        line.setStart(points[points.length - 1]);
        line.setEnd(points[0]);
        s.append(", ").append(line);
        s.append("}");
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        IPolygon2 iPolygon2 = (IPolygon2) o;
        if (iPolygon2.points == points()) { return true; }
        if (iPolygon2.points.length != points.length) { return false; }
        for (int i = 0; i < points.length; ++i) {
            if (!points[i].equals(iPolygon2.points[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(points);
    }

    public static final class Builder {
        List<IVector2> points = new ArrayList<>();

        public Builder addPoint(int x, int y) {
            points.add(IVector2.of(x, y));
            return this;
        }

        public Builder addPoint(IVector2 point) {
            points.add(point);
            return this;
        }

        public IPolygon2 build() {
            return new IPolygon2(points.toArray(IVector2[]::new));
        }


    }
}
