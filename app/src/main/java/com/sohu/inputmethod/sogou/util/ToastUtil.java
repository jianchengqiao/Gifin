package com.sohu.inputmethod.sogou.util;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.sohu.inputmethod.sogou.App;

/**
 * Created by Qiao on 2018/12/24.
 */
public class ToastUtil {

    private static Toast mToast;

    @SuppressLint("ShowToast")
    public static void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(App.getContext(), text, Toast.LENGTH_LONG);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }
}
