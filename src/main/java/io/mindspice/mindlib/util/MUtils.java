package io.mindspice.mindlib.util;

public class MUtils {
    public static int byteToUnsignedInt(byte b) {
        if (b >= 0) {
            return b;
        }
        return b & 0xFF;
    }


}
