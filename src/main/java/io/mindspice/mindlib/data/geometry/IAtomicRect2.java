package io.mindspice.mindlib.data.geometry;

import java.util.concurrent.locks.StampedLock;


public class IAtomicRect2 implements IRect2 {
    private IMutVector2 start;
    private IMutVector2 end;
    private IMutVector2 size;
    private IMutVector2 bottomLeft;
    private IMutVector2 bottomRight;
    private final StampedLock lock = new StampedLock();

    IAtomicRect2(IVector2 start, IVector2 end, IVector2 size) {
        this.start = new IMutVector2(start);
        this.end = new IMutVector2(end);
        this.size = new IMutVector2(size);
        this.bottomLeft = new IMutVector2(start.x(), start.y() + size.y());
        this.bottomRight = new IMutVector2(end.x(), end.y() + size.y());
    }

    IAtomicRect2(int x, int y, int width, int height) {
        start = new IMutVector2(x, y);
        end = new IMutVector2(x + width, y + height);
        size = new IMutVector2(width, height);
        bottomLeft = new IMutVector2(start.x(), start.y() + size.y());
        bottomRight = new IMutVector2(end.x(), end.y() + size.y());
    }

    IAtomicRect2(IVector2 start, IVector2 size) {
        this.start = new IMutVector2(start);
        end = new IMutVector2(start.x() + size.x(), start.y() + size.y());
        this.size = new IMutVector2(size);
        bottomLeft = new IMutVector2(start.x(), start.y() + size.y());
        bottomRight = new IMutVector2(end.x(), end.y() + size.y());
    }

    IAtomicRect2(IRect2 other) {
        this.start = new IMutVector2(other.start());
        this.end = new IMutVector2(other.end());
        this.size = new IMutVector2(other.size());
        bottomLeft = new IMutVector2(start.x(), start.y() + size.y());
        bottomRight = new IMutVector2(end.x(), end.y() + size.y());
    }

    public IConstRect2 asIRec2() {
        return new IConstRect2(this);
    }

    public IMutVector2 start() {
        long stamp = lock.tryOptimisticRead();
        IMutVector2 tmp = start;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                tmp = start;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return tmp;
    }

    public IMutVector2 end() {
        long stamp = lock.tryOptimisticRead();
        IMutVector2 tmp = end;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                tmp = end;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return tmp;
    }

    public IMutVector2 size() {
        long stamp = lock.tryOptimisticRead();
        IMutVector2 tmp = size;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                tmp = size;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return tmp;
    }

    public void setStart(int x, int y) {
        long stamp = lock.writeLock();
        try {
            this.start.setXY(x, y);
            reCalcBottomCorners();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public void setStart(IVector2 start) {
        long stamp = lock.writeLock();
        try {
            this.start.setXY(start.x(), start.y());
            reCalcBottomCorners();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public void setEnd(int x, int y) {
        long stamp = lock.writeLock();
        try {
            this.end.setXY(x, y);
            reCalcBottomCorners();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public void setEnd(IVector2 end) {
        long stamp = lock.writeLock();
        try {
            this.end.setXY(end.x(), end.y());
            reCalcBottomCorners();
        } finally {
            lock.unlockWrite(stamp);
        }
    }
    public void setSize(IVector2 size) {
        long stamp = lock.writeLock();
        try {
            this.size.setXY(size.x(), size.y());
            reCalcBottomCorners();
        } finally {
            lock.unlockWrite(stamp);
        }
    }


    public void setSize(int x, int y) {
        long stamp = lock.writeLock();
        try {
            this.size.setXY(x, y);
            reCalcBottomCorners();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public IAtomicRect2 reCenter(int x, int y) {
        long stamp = lock.writeLock();
        try {
            start.setX(x - (size.x() / 2));
            start.setY(y - (size.y() / 2));
            end.setX(start.x() + size.x());
            end.setY(start.y() + size.y());
            reCalcBottomCorners();
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    public IAtomicRect2 reCenter(IVector2 center) {
        long stamp = lock.writeLock();
        try {
            start.setX(center.x() - (size.x() / 2));
            start.setY(center.y() - (size.y() / 2));
            end.setX(start.x() + size.x());
            end.setY(start.y() + size.y());
            reCalcBottomCorners();
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public IVector2 getCenter() {
        long stamp = lock.tryOptimisticRead();
        IVector2 center = new IConstVector2((start.x() + end.x()) / 2, (start.y() + end.y()) / 2);
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                center = new IConstVector2((start.x() + end.x()) / 2, (start.y() + end.y()) / 2);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return center;
    }

    @Override
    public boolean contains(IVector2 pos) {
        long stamp = lock.tryOptimisticRead();
        boolean contains = pos.x() >= start.x() && pos.x() <= end.x() && pos.y() >= start.y() && pos.y() <= end.y();
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                contains = pos.x() >= start.x() && pos.x() <= end.x() && pos.y() >= start.y() && pos.y() <= end.y();
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return contains;
    }

    @Override
    public boolean contains(int x, int y) {
        long stamp = lock.tryOptimisticRead();
        boolean contains = x >= start.x() && x <= end.x() && y >= start.y() && y <= end.y();
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                contains = x >= start.x() && x <= end.x() && y >= start.y() && y <= end.y();
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return contains;
    }

    @Override
    public boolean intersects(IRect2 other) {
        long stamp = lock.tryOptimisticRead();
        boolean intersects = !(other.end().x() < start.x()
                || other.start().x() > end.x()
                || other.end().y() < start.y()
                || other.start().y() > end.y());
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                intersects = !(other.end().x() < start.x()
                        || other.start().x() > end.x()
                        || other.end().y() < start.y()
                        || other.start().y() > end.y());
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return intersects;
    }

    @Override
    public boolean intersectsLine(IVector2 lineStart, IVector2 lineEnd) {
        long stamp = lock.tryOptimisticRead();
        boolean intersects = lineStart.x() <= end.x() && lineEnd.x() >= start.x() && lineStart.y() <= end.y() && lineEnd.y() >= start.y();
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                intersects = lineStart.x() <= end.x() && lineEnd.x() >= start.x() && lineStart.y() <= end.y() && lineEnd.y() >= start.y();
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return intersects;
    }

    @Override
    public IRect2 combine(IRect2 other) {
        long stamp = lock.writeLock();
        try {
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
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public IAtomicRect2 combineMutable(IRect2 other) {
        return (IAtomicRect2) this.combine(other);
    }

    private void reCalcBottomCorners() {
        bottomLeft.setXY(start.x(), start.y() + size.y());
        bottomRight.setXY(end.x(), end.y() + size.y());
    }

    @Override
    public IVector2 topLeft() {
        long stamp = lock.tryOptimisticRead();
        IVector2 result = start;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                result = start;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return result;
    }

    @Override
    public IVector2 topRight() {
        long stamp = lock.tryOptimisticRead();
        IVector2 result = end;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                result = end;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return result;
    }

    @Override
    public IVector2 bottomLeft() {
        long stamp = lock.tryOptimisticRead();
        IVector2 result = bottomLeft;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                result = bottomLeft;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return result;
    }

    @Override
    public IVector2 bottomRight() {
        long stamp = lock.tryOptimisticRead();
        IVector2 result = bottomRight;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                result = bottomRight;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return result;
    }

    @Override
    public ILine2 leftEdge() {
        long stamp = lock.tryOptimisticRead();
        ILine2 line = new IConstLine2(start.x(), start.y(), start.x(), start.y() + size().y());
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                line = new IConstLine2(start.x(), start.y(), start.x(), start.y() + size().y());
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return line;
    }

    @Override
    public ILine2 rightEdge() {
        long stamp = lock.tryOptimisticRead();
        ILine2 line = new IConstLine2(end.x(), end.y(), end.x(), end.y() + size().y());
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                line = new IConstLine2(end.x(), end.y(), end.x(), end.y() + size().y());
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return line;
    }

    @Override
    public ILine2 topEdge() {
        long stamp = lock.tryOptimisticRead();
        ILine2 line = new IConstLine2(start.x(), start.y(), end.x(), end.y());
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                line = new IConstLine2(start.x(), start.y(), end.x(), end.y());
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return line;
    }

    @Override
    public ILine2 bottomEdge() {
        long stamp = lock.tryOptimisticRead();
        ILine2 line = new IConstLine2(start.x(), start.y() + size().y(), end.x(), end.y() + size().y());
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                line = new IConstLine2(start.x(), start.y() + size().y(), end.x(), end.y() + size().y());
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return line;
    }

    @Override
    public String toString() {
        long stamp = lock.tryOptimisticRead();
        String result = "(" + start + ", " + size + ")";
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                result = "(" + start + ", " + size + ")";
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        long stamp = lock.tryOptimisticRead();
        boolean equals;
        if (this == o) {
            equals = true;
        } else if (!(o instanceof IRect2 iRect2)) {
            equals = false;
        } else {
            equals = start.equals(iRect2.start()) && end.equals(iRect2.end()) && size.equals(iRect2.size());
        }
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                if (this == o) {
                    equals = true;
                } else if (!(o instanceof IRect2 iRect2)) {
                    equals = false;
                } else {
                    equals = start.equals(iRect2.start()) && end.equals(iRect2.end()) && size.equals(iRect2.size());
                }
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return equals;
    }

    @Override
    public int hashCode() {
        long stamp = lock.tryOptimisticRead();
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + size.hashCode();
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                result = start.hashCode();
                result = 31 * result + end.hashCode();
                result = 31 * result + size.hashCode();
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return result;
    }
}
