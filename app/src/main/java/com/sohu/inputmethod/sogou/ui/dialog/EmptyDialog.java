package com.sohu.inputmethod.sogou.ui.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.qiao.gifin.R;
import com.sohu.inputmethod.sogou.App;
import com.sohu.inputmethod.sogou.Constants;
import com.sohu.inputmethod.sogou.util.IntentUtil;
import com.sohu.inputmethod.sogou.util.LogUtil;
import com.sohu.inputmethod.sogou.util.SharedPrefUtil;

/**
 * Created by Qiao on 2018/12/28.
 */
public class EmptyDialog extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_INPUT_METHOD_CHANGED);
        registerReceiver(mReceiver, filter);
        App.postDelay(IntentUtil::showInputMethodPicker, 100);
        SharedPrefUtil.putLong(Constants.KEY_TIMESTAMP_ASSIST, System.currentTimeMillis());
        setContentView(R.layout.dialog_empty);
        findViewById(R.id.empty).setOnClickListener(v -> finish());
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.e(intent.getAction());
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
