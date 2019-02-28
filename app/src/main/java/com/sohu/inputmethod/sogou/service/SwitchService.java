package com.sohu.inputmethod.sogou.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.qiao.gifin.R;
import com.sohu.inputmethod.sogou.Constants;
import com.sohu.inputmethod.sogou.ui.dialog.EmptyDialog;
import com.sohu.inputmethod.sogou.util.LogUtil;
import com.sohu.inputmethod.sogou.util.SharedPrefUtil;

import java.util.List;

import static android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK;
import static android.view.accessibility.AccessibilityNodeInfo.FOCUS_INPUT;

/**
 * Created by Qiao on 2018/12/25.
 */
public class SwitchService extends AccessibilityService {
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                Intent intent = new Intent(getApplicationContext(), EmptyDialog.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        CharSequence packageName = event.getPackageName();
        LogUtil.e(packageName + ": " + Integer.toHexString(event.getEventType()));
        long l = System.currentTimeMillis();
        if (l - SharedPrefUtil.getLong(Constants.KEY_TIMESTAMP_ASSIST, 0) < 1000) {
            AccessibilityNodeInfo root = getRootInActiveWindow();
            if (root != null) {
                if ("android".equals(packageName.toString())) {
                    List<AccessibilityNodeInfo> infos = root.findAccessibilityNodeInfosByText(getString(R.string.app_name));
                    if (infos != null && infos.size() > 0) {
                        LogUtil.e("android click");
                        infos.get(0).getParent().performAction(ACTION_CLICK);
                        infos.get(0).recycle();
                    }
                } else if ("com.tencent.mm".equals(packageName.toString())) {
                    AccessibilityNodeInfo focus = root.findFocus(FOCUS_INPUT);
                    if (focus != null) {
                        focus.performAction(ACTION_CLICK);
                    } else {
                        searchTree(root);
                    }
                }
                root.recycle();
            }
        }
        if ("com.tencent.mm".equals(packageName.toString())) {
            if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_LONG_CLICKED) {
                mHandler.sendEmptyMessageDelayed(100, 200);
            } else {
                mHandler.removeMessages(100);
            }
        }
    }

    private void searchTree(AccessibilityNodeInfo root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo child = root.getChild(i);
            if (child.getClassName().toString().contains("EditText")) {
                LogUtil.e("tencent click");
                child.performAction(ACTION_CLICK);
                child.recycle();
                return;
            }
            if (child.getChildCount() > 0) {
                searchTree(child);
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}
