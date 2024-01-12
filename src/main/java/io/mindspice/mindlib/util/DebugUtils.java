package io.mindspice.mindlib.util;

public class DebugUtils {

    public static void printThreadMethod() {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String methodName = stackTrace[2].getMethodName();
        String className = stackTrace[2].getClassName();
        String threadName = Thread.currentThread().getName();
        System.out.println("Method called: " + className + "." + methodName + " on thread " + threadName);
    }

    public static String getThreadMethod() {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String methodName = stackTrace[2].getMethodName();
        String className = stackTrace[2].getClassName();
        String threadName = Thread.currentThread().getName();
        return "Method called: " + className + "." + methodName + " on thread " + threadName;
    }

    public static void printStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            System.out.println(element);
        }
    }

    public static StackTraceElement[] getStackTrace() {
        return Thread.currentThread().getStackTrace();

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
