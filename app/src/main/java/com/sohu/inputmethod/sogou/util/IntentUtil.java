package com.sohu.inputmethod.sogou.util;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.sohu.inputmethod.sogou.App;
import com.sohu.inputmethod.sogou.service.InputService;
import com.sohu.inputmethod.sogou.service.SwitchService;

import java.util.List;

/**
 * Created by Qiao on 2018/12/26.
 */
public class IntentUtil {
    public static void showInputMethodPicker() {
        InputMethodManager inputMethodManager = (InputMethodManager) App.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showInputMethodPicker();
        }
    }
    public static void showInputMethodPicker(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showInputMethodPicker();
        }
    }

    public static void showInputMethod(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) App.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void toEnableInput() {
        Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(intent);
    }

    public static void toEnableAccessibility() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(intent);
    }

    public static boolean isInputEnabled() {
        InputMethodManager inputManager = (InputMethodManager) App.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            List<InputMethodInfo> methodList = inputManager.getEnabledInputMethodList();
            for (InputMethodInfo info : methodList) {
                if (info.getId().contains(App.getContext().getPackageName() + "/" + InputService.class.getCanonicalName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isInputSelected() {
        try {
            String currentInputMethod = android.provider.Settings.Secure.getString(App.getContext().getContentResolver(),
                    android.provider.Settings.Secure.DEFAULT_INPUT_METHOD);
            return TextUtils.equals(App.getContext().getPackageName() + "/" + InputService.class.getCanonicalName(), currentInputMethod);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isAccessibilityOn() {
        try {
            int accessibilityEnabled = android.provider.Settings.Secure.getInt(App.getContext().getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            if (accessibilityEnabled == 1) {
                String service = App.getContext().getPackageName() + "/" + SwitchService.class.getCanonicalName();
                String settingValue = android.provider.Settings.Secure.getString(App.getContext().getApplicationContext().getContentResolver(),
                        android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                return settingValue != null && settingValue.contains(service);
            }
        } catch (android.provider.Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
