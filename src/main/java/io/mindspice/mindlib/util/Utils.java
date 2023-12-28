package io.mindspice.mindlib.util;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class Utils {

    public static void printThreadMethod() {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String methodName = stackTrace[2].getMethodName();
        String className = stackTrace[2].getClassName();
        String threadName = Thread.currentThread().getName();
        System.out.println("Method called: " + className + "." + methodName + " on thread " + threadName);
    }

    public static void print(Object... objects) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objects.length; i++) {
            sb.append(objects[i].toString());
            if (i < objects.length - 1) {
                sb.append(" ");
            }
        }
        System.out.println(sb);
    }


}
