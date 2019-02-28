package com.sohu.inputmethod.sogou;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.qiao.gifin.BuildConfig;

import java.util.List;

/**
 * Created by Qiao on 2016/12/15.
 */

public class App extends Application {
    public static final String TAG = "APP";

    private static App APP;
    private Handler mHandler = new Handler();

    public static void post(Runnable r) {
        APP.mHandler.post(r);
    }

    public static void postDelay(Runnable r, long delay) {
        APP.mHandler.postDelayed(r, delay);
    }

    public static Context getContext() {
        return APP.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        APP = this;
    }

    public static boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) App.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = null;
        if (activityManager != null) {
            appProcesses = activityManager.getRunningAppProcesses();
        }
        if (appProcesses == null) return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(BuildConfig.APPLICATION_ID)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}