package io.mindspice.mindlib.data.collections;

import io.mindspice.mindlib.data.geometry.ILine2;
import io.mindspice.mindlib.data.geometry.IPolygon2;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.geom.Line2D;


public class geometry {
    int[] x = new int[]{20, 70, 40, 20};
    int[] y = new int[]{10, 10, 40, 50};
    Polygon p = new Polygon(x, y, 4);
    Line2D l = new Line2D.Double(70, 40, 60, 15);

    @Test
    void polygonTest() {
        IPolygon2 poly = new IPolygon2.Builder()
                .addPoint(20, 10)
                .addPoint(70, 10)
                .addPoint(40, 20)
                .addPoint(40, 40)
                .addPoint(20, 50)
                .build();
        System.out.println(poly);
        ILine2 line = ILine2.of(30,20,30,60);
        ILine2 line2 = ILine2.of(40,40,20,50);

        var inst = poly.intersects(line);
        var inst2 = line2.intersects(line);
        System.out.println(inst);
        System.out.println(inst2);

    }

    static int orientation(Point p, Point q, Point r) {
        // See https://www.geeksforgeeks.org/orientation-3-ordered-points/
        // for details of below formula.
        int val = (q.y - p.y) * (r.x - q.x) -
                (q.x - p.x) * (r.y - q.y);

        if (val == 0) {
            return 0; // collinear
        }

        return (val > 0) ? 1 : 2; // clock or counterclock wise
    }

    static boolean onSegment(Point p, Point q, Point r) {
        if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
                q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y)) { return true; }

        return false;
    }

    static boolean doIntersect(Point p1, Point q1, Point p2, Point q2) {
        // Find the four orientations needed for general and
        // special cases
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        // General case
        if (o1 != o2 && o3 != o4) { return true; }

        // Special Cases
        // p1, q1 and p2 are collinear and p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) { return true; }

        // p1, q1 and q2 are collinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) { return true; }

        // p2, q2 and p1 are collinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) { return true; }

        // p2, q2 and q1 are collinear and q1 lies on segment p2q2
        if (o4 == 0 && onSegment(p2, q1, q2)) { return true; }

        return false; // Doesn't fall in any of the above cases
    }

    // Driver code


}
