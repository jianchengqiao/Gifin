package com.sohu.inputmethod.sogou.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.sohu.inputmethod.sogou.App;

public class ClipboardUtil {
    public static String getClipBoardText() {
        ClipboardManager clipBoardManager = (ClipboardManager) App.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipBoardManager != null) {
            if (clipBoardManager.hasPrimaryClip()) {
                ClipData clipData = clipBoardManager.getPrimaryClip();
                if (clipData.getItemCount() > 0) {
                    return clipData.getItemAt(0).getText().toString();
                }
            }
        }
        return null;
    }

    public static void copyText(String text) {
        ClipboardManager clipBoardManager = (ClipboardManager) App.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipBoardManager != null) {
            clipBoardManager.setPrimaryClip(ClipData.newPlainText(null, text));
        }
    }
}
