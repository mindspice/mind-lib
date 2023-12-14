package io.mindspice.mindlib.data.geometry;

import java.util.concurrent.locks.StampedLock;


public class IAtomicVector2 implements IVector2 {
    private int x;
    private int y;
    private final StampedLock lock = new StampedLock();

    IAtomicVector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    IAtomicVector2(float x, float y) {
        this.x = (int) x;
        this.y = (int) y;
    }

    IAtomicVector2(IVector2 other) {
        this.x = other.x();
        this.y = other.y();
    }

    @Override
    public int x() {
        long stamp = lock.tryOptimisticRead();
        int currentX = x;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                currentX = x;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return currentX;
    }

    @Override
    public int y() {
        long stamp = lock.tryOptimisticRead();
        int currentY = y;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                currentY = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return currentY;
    }

    public void setX(int x) {
        long stamp = lock.writeLock();
        try {
            this.x = x;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public void setY(int y) {
        long stamp = lock.writeLock();
        try {
            this.y = y;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public IAtomicVector2 setXY(int x, int y) {
        long stamp = lock.writeLock();
        try {
            this.x = x;
            this.y = y;
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public IAtomicVector2 setXY(IVector2 other) {
        long stamp = lock.writeLock();
        try {
            this.x = other.x();
            this.y = other.y();
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    public IConstVector2 asImmutable() {
        return new IConstVector2(this);
    }

    @Override
    public IAtomicVector2 withXInc() {
        long stamp = lock.writeLock();
        try {
            x += 1;
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public IAtomicVector2 withYInc() {
        long stamp = lock.writeLock();
        try {
            y += 1;
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public IAtomicVector2 withXDec() {
        long stamp = lock.writeLock();
        try {
            x -= 1;
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public IAtomicVector2 withYDec() {
        long stamp = lock.writeLock();
        try {
            y -= 1;
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public IAtomicVector2 add(IVector2 other) {
        long stamp = lock.writeLock();
        try {
            this.x += other.x();
            this.y += other.y();
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public IVector2 add(int x, int y) {
        long stamp = lock.writeLock();
        try {
            this.x += x;
            this.y += y;
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public IAtomicVector2 subtract(IVector2 other) {
        long stamp = lock.writeLock();
        try {
            this.x -= other.x();
            this.y -= other.y();
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public IVector2 subtract(int x, int y) {
        long stamp = lock.writeLock();
        try {
            this.x -= x;
            this.y -= y;
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public IAtomicVector2 multiply(IVector2 other) {
        long stamp = lock.writeLock();
        try {
            this.x *= other.x();
            this.y *= other.y();
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public IVector2 multiply(int x, int y) {
        long stamp = lock.writeLock();
        try {
            this.x *= x;
            this.y *= y;
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public IAtomicVector2 divide(IVector2 other) {
        long stamp = lock.writeLock();
        try {
            this.x /= other.x();
            this.y /= other.y();
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public IVector2 divide(int x, int y) {
        long stamp = lock.writeLock();
        try {
            this.x /= x;
            this.y /= y;
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public IAtomicVector2 scale(int scalar) {
        long stamp = lock.writeLock();
        try {
            this.x *= scalar;
            this.y *= scalar;
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public IVector2 modulo(int divisor) {
        long stamp = lock.writeLock();
        try {
            this.x %= divisor;
            this.y %= divisor;
        } finally {
            lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public IVector2 normalize() {
        double magnitude = magnitude();
        long stamp = lock.writeLock();
        try {
            if (magnitude == 0) {
                x = 0;
                y = 0;
                return this;
            }
            x = (int) (x / magnitude);
            y = (int) (y / magnitude);
        } finally {
            lock.unlockWrite(stamp);
        }

        return this;
    }

    @Override
    public int dotProduct(IVector2 other) {
        long stamp = lock.tryOptimisticRead();
        int currentX = x;
        int currentY = y;
        int result = (currentX * other.x()) + (currentY * other.y());
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                result = (this.x * other.x()) + (this.y * other.y());
            } finally {
                lock.unlockRead(stamp);
            }
        }

        return result;
    }

    @Override
    public int dotProduct(int x, int y) {
        long stamp = lock.readLock();
        try {
            return (this.x * x) + (this.y * y);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public double magnitude() {
        long stamp = lock.tryOptimisticRead();
        double localX = x;
        double localY = y;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                localX = x;
                localY = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return Math.sqrt((localX * localX) + (localY * localY));
    }

    @Override
    public String toString() {
        long stamp = lock.tryOptimisticRead();
        int localX = x;
        int localY = y;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                localX = x;
                localY = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return "(" + localX + ", " + localY + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof IVector2 iVector2)) { return false; }

        long stamp = lock.tryOptimisticRead();
        int localX = x;
        int localY = y;

        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                localX = x;
                localY = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return localX == iVector2.x() && localY == iVector2.y();
    }

    @Override
    public int hashCode() {
        long stamp = lock.tryOptimisticRead();
        int localX = x;
        int localY = y;

        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                localX = x;
                localY = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        int result = localX;
        result = 31 * result + localY;
        return result;
    }

}