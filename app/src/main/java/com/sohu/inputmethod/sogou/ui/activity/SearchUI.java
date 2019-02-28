package com.sohu.inputmethod.sogou.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.qiao.gifin.R;
import com.sohu.inputmethod.sogou.Constants;
import com.sohu.inputmethod.sogou.util.IntentUtil;
import com.sohu.inputmethod.sogou.util.SharedPrefUtil;
import com.sohu.inputmethod.sogou.util.ToastUtil;

/**
 * Created by Qiao on 2018/12/24.
 */
public class SearchUI extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_ui);

        EditText search_text = findViewById(R.id.search_text);
        TextView search_cancel = findViewById(R.id.search_cancel);
        TextView search_confirm = findViewById(R.id.search_confirm);

        search_cancel.setOnClickListener(v -> finishWithShowInputMethodPicker());
        search_confirm.setOnClickListener(v -> {
            String text = search_text.getText().toString().trim();
            if (TextUtils.isEmpty(text)) return;
            SharedPrefUtil.putString(Constants.KEY_CURRENT_WORD, text);
            finishWithShowInputMethodPicker();
            SharedPrefUtil.putLong(Constants.KEY_TIMESTAMP_SEARCH, System.currentTimeMillis());
        });
        search_text.requestFocus();
        search_text.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search_confirm.performClick();
            }
            return false;
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_INPUT_METHOD_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onBackPressed() {
        finishWithShowInputMethodPicker();
    }

    private void finishWithShowInputMethodPicker() {
        SharedPrefUtil.putLong(Constants.KEY_TIMESTAMP_ASSIST, System.currentTimeMillis());
        IntentUtil.showInputMethodPicker();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        boolean accessibility = IntentUtil.isAccessibilityOn();
        boolean autoAssist = SharedPrefUtil.getBoolean(Constants.KEY_SETTING_AUTO_ASSIST, false);
        if (!accessibility && autoAssist) {
            ToastUtil.showToast("请在本页打开 Gifin 的辅助功能");
            IntentUtil.toEnableAccessibility();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
