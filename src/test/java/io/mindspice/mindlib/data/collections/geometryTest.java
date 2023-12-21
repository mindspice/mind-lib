package io.mindspice.mindlib.data.collections;

import io.mindspice.mindlib.data.geometry.*;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;


public class geometryTest {
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
        ILine2 line = ILine2.of(30, 20, 30, 60);
        ILine2 line2 = ILine2.of(40, 40, 20, 50);

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

    @Test
    void rectTest() {
        var rect = IRect2.of(IVector2.of(100, 200), IVector2.of(300, 300));
        assert (rect.start().x() == 100);
        assert (rect.start().y() == 200);
        assert (rect.end().x() == 400);
        assert (rect.end().y() == 500);

        var rect2 = IRect2.of(IVector2.of(0, 0), IVector2.of(200, 200));

        for (int x = 0; x < 202; ++x) {
            for (int y = 0; y < 202; ++y) {
                if (x < 201 && y < 201) {
                    assert (rect2.contains(IVector2.of(x, y)));
                } else {
                    assert (!rect2.contains(IVector2.of(x, y)));
                }
            }
        }

        for (int x = 0; x < 202; x++) {
            for (int y = 0; y < 202; y++) {
                if (x < 201 && y < 201) {
                    assert (rect2.intersects(IRect2.of(IVector2.of(x, y), IVector2.of(x + 10, y + 10))));
                } else {
                    assert (!rect2.intersects(IRect2.of(IVector2.of(x, y), IVector2.of(x + 10, y + 10))));

                }
            }
        }
    }

    @Test
    void quadMapTest() {
        int boundX = 1920;
        int boundY = 1920;
        IRect2 bounds = IRect2.of(0, 0, boundX, boundY);
        IConcurrentVQuadTree<Object> quadTree = new IConcurrentVQuadTree<>(bounds, 4); // 4 is the capacity per quadrant
        for (int i = 0; i < 100; i++) {
            int x = ThreadLocalRandom.current().nextInt(boundX); // Random x-coordinate within bounds
            int y = ThreadLocalRandom.current().nextInt(boundY); // Random y-coordinate within bounds
            IVector2 position = IVector2.of(x, y);
            Object obj = new Object();
            quadTree.insert(position, obj);
        }
        var tVec = IVector2.of(500, 500);
        Object obj = new Object();
        System.out.println(obj);
        quadTree.insert(tVec, obj);
        System.out.println(quadTree);
        System.out.println("\n\n");
        quadTree.update(tVec, IVector2.of(510, 510), obj);
        System.out.println(quadTree);
        System.out.println("\n\n");
        quadTree.update(IVector2.of(510, 510), IVector2.of(900, 900), obj);
        System.out.println("\n\n");
        System.out.println(quadTree);
        quadTree.update(IVector2.of(900, 900), IVector2.of(1900, 1900), obj);
        System.out.println("\n\n");
        System.out.println(quadTree);

        for (int i = 0; i < 100; ++i) {
            quadTree.query(IRect2.of(ThreadLocalRandom.current().nextInt(1920), ThreadLocalRandom.current().nextInt(1920), 1200, 900 ));
        }

    }

    @Test
    void randomTest() throws InterruptedException {
        int boundX = 81920;
        int boundY = 81920;
        IRect2 bounds = IRect2.of(0, 0, boundX, boundY);
        IVectorQuadTree<Object> quadTree = new IVectorQuadTree<>(bounds, 6); // 4 is the capacity per quadrant
        IKDTree2D<Object> spacMap = new IKDTree2D<>(bounds);
        // Random number generator
        Random rand = new Random();

        // Generate and insert 100 random objects
        for (int i = 0; i < 5000000; i++) {
            int x = rand.nextInt(boundX); // Random x-coordinate within bounds
            int y = rand.nextInt(boundY); // Random y-coordinate within bounds
            IVector2 position = IVector2.of(x, y);
            Object obj = new Object();
            quadTree.insert(position, obj);
            //spacMap.insert(position, obj);
        }

        int foundCount = 0;
        IMutRect2 mutRec = IRect2.ofMutable(0, 0, 384, 384);
        IMutLine2 line = ILine2.ofMutable(0, 0, 0, 0);
        IMutVector2 mmm = IVector2.ofMutable(0, 0);
        var tt = System.nanoTime();
        int conts = 0;
        for (int i = 0; i < 100_000; ++i) {
            for (int j = 0; j < 400; ++j) {
                // mutRec.reCenter(ThreadLocalRandom.current().nextInt(1920), ThreadLocalRandom.current().nextInt(1920));
                // mmm.setXY(ThreadLocalRandom.current().nextInt(1920), ThreadLocalRandom.current().nextInt(1920));
                if (mutRec.contains(mmm)) {
                    conts++;
                }
            }
        }
        System.out.println("Contains time:" + (System.nanoTime() - tt) / 100_000);
        System.out.println(conts);

        var pArr = new IVector2[20];
        for (int i = 0; i < 20; i++) {
            int sides = 20;
            double angleStep = 2 * Math.PI / sides;
            double angle = i * angleStep;
            int radius = 540;
            double x = 250 + radius * Math.cos(angle);
            double y = 250 + radius * Math.sin(angle);
            pArr[i] = IVector2.of((int) x, (int) y);
        }
        //  System.out.println(quadTree);

        ;

        ExecutorService exec = Executors.newFixedThreadPool(4);

//        exec.submit(() -> {
//            IPolygon2 poly = IPolygon2.of(pArr);
//            IMutVector2 mVec = IVector2.ofMutable(0, 0);
//            IMutVector2 lastMVec = IVector2.ofMutable(0, 0);
//            Object obj = new Object();
//            quadTree.insert(mVec, obj);
//            int colCount = 0;
//            var t = System.nanoTime();
//            for (int i = 0; i < 1000000; ++i) {
//                mVec.setXY(rand.nextInt(1900), rand.nextInt(1900));
//                LockSupport.parkNanos(1000);
//                quadTree.update(lastMVec, mVec, obj);
//              //  quadTree.deFragment();
//                lastMVec.setXY(mVec);
////                var found = quadTree.query(mutRec.reCenter(960, 640));
////                line.setEnd(rand.nextInt(100), rand.nextInt(100));
////                for (var f : found) {
////                    if (poly.intersects(line)) {
////                        colCount++;
////                    }
////                }
//            }
////            var e = ((System.nanoTime() - t) /  1000_0);
////            System.out.println("update time:" + e);
////            System.out.println(foundCount);
//        });

        exec.submit(() -> {
            IPolygon2 poly = IPolygon2.of(pArr);
            IMutVector2 mVec = IVector2.ofMutable(0, 0);
            IMutVector2 lastMVec = IVector2.ofMutable(0, 0);
            var nVec = IVector2.of(1100, 800);
            Object obj = new Object();
            quadTree.insert(IVector2.of(100, 789), obj);
            quadTree.insert(IVector2.of(300, 1877), obj);
            quadTree.insert(IVector2.of(700, 200), obj);
            quadTree.insert(IVector2.of(1000, 200), obj);
            quadTree.insert(IVector2.of(1200, 1589), obj);
            quadTree.insert(IVector2.of(1600, 300), obj);
            quadTree.insert(IVector2.of(1900, 900), obj);
            int colCount = 0;
            var t = System.nanoTime();
            for (int i = 0; i < 10_000; ++i) {
//                quadTree.insert(IVector2.of(1600, 300), obj);
//                quadTree.insert(IVector2.of(1900, 900), obj);
//              //  quadTree.remove(IVector2.of(1600, 300), obj);
//                quadTree.remove(IVector2.of(1900, 900), obj);
//               // mVec.setXY(rand.nextInt(1900), rand.nextInt(1900));
//                quadTree.update(lastMVec, mVec, obj);
//                lastMVec.setXY(mVec);
                var found = quadTree.query(mutRec);

//                line.setEnd(rand.nextInt(x1 + 10), rand.nextInt(x1 + 2));
//                for (var f : found) {
//                    if (poly.intersects(line)) {
//                        colCount++;
//                    }
//                }
                colCount += found.size();

            }
            var e = ((System.nanoTime() - t) / 10_000);
            System.out.println("collision time:" + e);
            System.out.println(colCount);
        });

        exec.submit(() -> {
            IPolygon2 poly = IPolygon2.of(pArr);
            IMutVector2 mVec = IVector2.ofMutable(0, 0);
            IMutVector2 lastMVec = IVector2.ofMutable(0, 0);
            var nVec = IVector2.of(123, 1023);
            Object obj = new Object();
            spacMap.insert(IVector2.of(100, 789), obj);
            spacMap.insert(IVector2.of(300, 1877), obj);
            spacMap.insert(IVector2.of(700, 200), obj);
            spacMap.insert(IVector2.of(1000, 200), obj);
            spacMap.insert(IVector2.of(1200, 1589), obj);
            spacMap.insert(IVector2.of(1600, 300), obj);
            spacMap.insert(IVector2.of(1900, 900), obj);
            int colCount = 0;
            var t = System.nanoTime();
            for (int i = 0; i < 10_000; ++i) {
                spacMap.insert(IVector2.of(1600, 300), obj);
                spacMap.insert(IVector2.of(1900, 900), obj);
//                spacMap.remove(IVector2.of(1600, 300), obj);
//                spacMap.remove(IVector2.of(1900, 900), obj);
//                mVec.setXY(rand.nextInt(1900), rand.nextInt(1900));
//                quadTree.update(lastMVec, mVec, obj);
//                lastMVec.setXY(mVec);
                var found = spacMap.query(mutRec.reCenter(nVec));
//                line.setEnd(rand.nextInt(x1 + 10), rand.nextInt(x1 + 2));
//                for (var f : found) {
//                    if (poly.intersects(line)) {
//                        colCount++;
//                    }
//                }
                colCount += found.size();

            }
            var e = ((System.nanoTime() - t) / 10_000);
            System.out.println("space collision time:" + e);
            System.out.println(colCount);
        });

        record BoxTest(boolean hasCollision) { }

        exec.submit(() -> {
            IPolygon2 poly = IPolygon2.of(pArr);
            IMutVector2 mVec = IVector2.ofMutable(0, 0);
            IMutVector2 lastMVec = IVector2.ofMutable(0, 0);
            var nVec = IVector2.of(123, 1023);
            Object obj = new Object();
            BoxTest[][] bArr = new BoxTest[1920 / 32][1920 / 32];
            for (int x = 0; x < 1920 / 32; ++x) {
                for (int y = 0; y < 1920 / 32; ++y) {
                    bArr[x][y] = new BoxTest(ThreadLocalRandom.current().nextBoolean());
                }
            }
            int colCount = 0;
            var t = System.nanoTime();
            for (int j = 0; j < 10_000; j++) {
                mutRec.reCenter(rand.nextInt(1920), rand.nextInt(1920));
                var c = 0;
                for (int x = 0; x <= mutRec.size().x() / 32; x++) {
                    for (int y = 0; y <= mutRec.size().y() / 32; ++y) {
                        if (bArr[x][y].hasCollision) {
                            colCount++;

                        }
                    }
                }
                for (int x = 0; x <= mutRec.size().x() / 32; x++) {
                    for (int y = 0; y <= mutRec.size().y() / 32; ++y) {
                        bArr[x][y] = bArr[y][x];
                    }
                }

            }
            var e = ((System.nanoTime() - t) / 10_000);
            System.out.println("rawGrid collision time:" + e);
            System.out.println(colCount);
        });

        Thread.sleep(Integer.MAX_VALUE);

        //     System.out.println(quadTree);
    }
}
