package com.sohu.inputmethod.sogou.util;

import android.util.Log;

import com.qiao.gifin.BuildConfig;

/**
 * Created by Qiao on 2016/12/18.
 */

public class LogUtil {
    private static boolean DEBUG = BuildConfig.DEBUG;

    public static void v(Object msg) {
        printLog(Constant.V, Constant.TAG, msg);
    }

    public static void d(Object msg) {
        printLog(Constant.D, Constant.TAG, msg);
    }

    public static void i(Object msg) {
        printLog(Constant.I, Constant.TAG, msg);
    }

    public static void w(Object msg) {
        printLog(Constant.W, Constant.TAG, msg);
    }

    public static void e(Object msg) {
        printLog(Constant.E, Constant.TAG, msg);
    }

    public static void a(Object msg) {
        printLog(Constant.A, Constant.TAG, msg);
    }

    public static void v(String tag, Object msg) {
        printLog(Constant.V, tag, msg);
    }

    public static void d(String tag, Object msg) {
        printLog(Constant.D, tag, msg);
    }

    public static void i(String tag, Object msg) {
        printLog(Constant.I, tag, msg);
    }

    public static void w(String tag, Object msg) {
        printLog(Constant.W, tag, msg);
    }

    public static void e(String tag, Object msg) {
        printLog(Constant.E, tag, msg);
    }

    public static void a(String tag, Object msg) {
        printLog(Constant.A, tag, msg);
    }

    private static void printLog(int type, String tagStr, Object objectMsg) {
        if (!DEBUG) {
            return;
        }

        String[] contents = wrapperContent(tagStr, objectMsg);
        String tag = contents[0];
        String msg = contents[1];
        String headString = contents[2];
        printDefault(type, tag, headString + msg);
    }

    private static void printDefault(int type, String tag, String msg) {
        switch (type) {
            case Constant.V:
                Log.v(tag, msg);
                break;
            case Constant.D:
                Log.d(tag, msg);
                break;
            case Constant.I:
                Log.i(tag, msg);
                break;
            case Constant.W:
                Log.w(tag, msg);
                break;
            case Constant.E:
                Log.e(tag, msg);
                break;
            case Constant.A:
                Log.wtf(tag, msg);
                break;
        }
    }

    private static String[] wrapperContent(String tagStr, Object objectMsg) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 5;
        String className = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();
        String methodNameShort = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        String tag = (tagStr == null ? className : tagStr);
        String msg = (objectMsg == null) ? "null" : objectMsg.toString();
        String headString = String.format("[ # (%1$s:%2$s) # %3$s ] ", className, lineNumber, methodNameShort);
        return new String[]{tag, msg, headString};
    }

    interface Constant {
        String TAG = "LogUtil TAG";
        int V = 0x1;
        int D = 0x2;
        int I = 0x3;
        int W = 0x4;
        int E = 0x5;
        int A = 0x6;
    }
}
