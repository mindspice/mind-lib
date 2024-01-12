package io.mindspice.mindlib.data.wrappers;

public class MutableBoolean {
    boolean bool;

    public MutableBoolean(boolean bool) {
        this.bool = bool;
    }

    public static MutableBoolean of(boolean bool) {
        return new MutableBoolean(bool);
    }

    public void setTrue() {
        bool = true;
    }

    public void setFalse() {
        bool = false;
    }

    public boolean get() {
        return bool;
    }
}
