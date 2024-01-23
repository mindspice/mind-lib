package io.mindspice.mindlib.util;

import java.util.ArrayList;
import java.util.List;


public class MUtils {
    public static int byteToUnsignedInt(byte b) {
        if (b >= 0) {
            return b;
        }
        return b & 0xFF;
    }

    public static <T> List<T> mergeToNewList(List<T> ogList, T... elements) {
        ArrayList<T> newList = new ArrayList<>(ogList.size() + elements.length);
        for (var e : elements) {
            newList.add(e);
        }
        return newList;
    }


}
