package com.sohu.inputmethod.sogou.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiao.gifin.R;
import com.sohu.inputmethod.sogou.Constants;
import com.sohu.inputmethod.sogou.ui.dialog.EnableAccessTipDialog;
import com.sohu.inputmethod.sogou.util.BackupUtil;
import com.sohu.inputmethod.sogou.util.IntentUtil;
import com.sohu.inputmethod.sogou.util.LogUtil;
import com.sohu.inputmethod.sogou.util.SharedPrefUtil;

import java.util.Objects;

public class Settings extends AppCompatActivity {
    public static final Integer[] ITEM_COUNTS = {10, 20, 30, 50, 100};

    private LinearLayout settings_init_layout;
    private TextView setting_init_step_1;
    private TextView setting_init_button_1;
    private TextView setting_init_step_2;
    private TextView setting_init_button_2;
    private TextView setting_init_step_3;
    private TextView setting_init_button_3;
    private LinearLayout settings_layout;
    private TextView setting_button_0;
    private TextView setting_button_1;
    private TextView setting_button_2;
    private TextView setting_button_3;
    private TextView setting_button_4;
    private TextView setting_button_5;
    private TextView setting_button_6;
    private TextView setting_button_7;
    private TextView setting_6_tip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_INPUT_METHOD_CHANGED);
        registerReceiver(mReceiver, filter);
        initViews();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
    }

    private void initViews() {
        settings_init_layout = findViewById(R.id.settings_init_layout);
        setting_init_button_1 = findViewById(R.id.setting_init_button_1);
        setting_init_button_2 = findViewById(R.id.setting_init_button_2);
        setting_init_button_3 = findViewById(R.id.setting_init_button_3);
        settings_layout = findViewById(R.id.settings_layout);
        setting_button_0 = findViewById(R.id.setting_button_0);
        setting_button_1 = findViewById(R.id.setting_button_1);
        setting_button_2 = findViewById(R.id.setting_button_2);
        setting_button_3 = findViewById(R.id.setting_button_3);
        setting_button_4 = findViewById(R.id.setting_button_4);
        setting_button_5 = findViewById(R.id.setting_button_5);
        setting_button_6 = findViewById(R.id.setting_button_6);
        setting_button_7 = findViewById(R.id.setting_button_7);
        setting_6_tip = findViewById(R.id.setting_6_tip);
        boolean autoAssist = SharedPrefUtil.getBoolean(Constants.KEY_SETTING_AUTO_ASSIST, false);
        boolean autoFavorite = SharedPrefUtil.getBoolean(Constants.KEY_SETTING_AUTO_FAVORITE, true);
        boolean showHistory = SharedPrefUtil.getBoolean(Constants.KEY_SETTING_EXPAND_HISTORY, true);
        boolean rememberLast = SharedPrefUtil.getBoolean(Constants.KEY_SETTING_REMEMBER_LAST, true);
        int itemCount = SharedPrefUtil.getInt(Constants.KEY_SETTING_PAGE_SIZE, 20);
        setting_button_0.setText(autoAssist ? R.string.button_opened : R.string.button_closed);
        setting_button_1.setText(showHistory ? R.string.button_opened : R.string.button_closed);
        setting_button_2.setText(rememberLast ? R.string.button_opened : R.string.button_closed);
        setting_button_3.setText(String.format("%s 条", itemCount));
        setting_button_4.setText(autoFavorite ? R.string.button_opened : R.string.button_closed);
        setting_6_tip.setText(BackupUtil.needRestore() ? R.string.settings_6_tip_2 : R.string.settings_6_tip);
        setting_init_button_1.setOnClickListener(v -> IntentUtil.toEnableInput());
        setting_init_button_2.setOnClickListener(v -> IntentUtil.showInputMethodPicker());
        setting_init_button_3.setOnClickListener(v -> startActivity(new Intent(this, EnableAccessTipDialog.class)));
        setting_button_0.setOnClickListener(v -> setAutoAssist());
        setting_button_1.setOnClickListener(v -> setExpandHistory());
        setting_button_2.setOnClickListener(v -> setRememberLast());
        setting_button_3.setOnClickListener(v -> setItemCount());
        setting_button_4.setOnClickListener(v -> setAutoFavorite());
        setting_button_5.setOnClickListener(v -> backup());
        setting_button_6.setOnClickListener(v -> restore());
        setting_button_7.setOnClickListener(v -> ignore());

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean hasIgnored = Objects.requireNonNull(powerManager).isIgnoringBatteryOptimizations(getPackageName());
        setting_button_7.setText(hasIgnored ? R.string.button_ignored : R.string.button_ignore);
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeVisibleView();
        setting_init_button_1.setEnabled(!IntentUtil.isInputEnabled());
        setting_init_button_2.setEnabled(!IntentUtil.isInputSelected());
        setting_init_button_3.setEnabled(!IntentUtil.isAccessibilityOn());
        setting_init_button_1.setText(IntentUtil.isInputEnabled() ? R.string.button_enabled : R.string.button_disabled);
        setting_init_button_2.setText(IntentUtil.isInputSelected() ? R.string.button_enabled : R.string.button_disabled);
        setting_init_button_3.setText(IntentUtil.isAccessibilityOn() ? R.string.button_enabled : R.string.button_disabled);
        setting_6_tip.setText(BackupUtil.needRestore() ? R.string.settings_6_tip_2 : R.string.settings_6_tip);
    }

    private void changeVisibleView() {
        if ((!IntentUtil.isInputEnabled() && settings_init_layout.getVisibility() == View.GONE)
                || settings_init_layout.getVisibility() == View.VISIBLE) {
            settings_init_layout.setVisibility(View.VISIBLE);
            settings_layout.setVisibility(View.GONE);
        } else {
            settings_init_layout.setVisibility(View.GONE);
            settings_layout.setVisibility(View.VISIBLE);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.e(intent.getAction());
            setting_init_button_2.setEnabled(!IntentUtil.isInputSelected());
            setting_init_button_2.setText(IntentUtil.isInputSelected() ? R.string.button_enabled : R.string.button_disabled);
            changeVisibleView();
        }
    };

    private void setItemCount() {
        ListPopupWindow window = new ListPopupWindow(this);
        window.setAdapter(new ArrayAdapter<>(this, R.layout.item_popup_window, R.id.item_popup_text, ITEM_COUNTS));
        window.setAnchorView(setting_button_3);
        window.setModal(false);
        window.setOnItemClickListener((parent, view, position, id) -> {
            window.dismiss();
            Integer itemCount = ITEM_COUNTS[position];
            SharedPrefUtil.putInt(Constants.KEY_SETTING_PAGE_SIZE, itemCount);
            setting_button_3.setText(String.format("%s 条", itemCount));
        });
        window.show();
    }

    private void setAutoAssist() {
        boolean autoAssist = SharedPrefUtil.getBoolean(Constants.KEY_SETTING_AUTO_ASSIST, false);
        autoAssist = !autoAssist;
        SharedPrefUtil.putBoolean(Constants.KEY_SETTING_AUTO_ASSIST, autoAssist);
        setting_button_0.setText(autoAssist ? R.string.button_opened : R.string.button_closed);
        if (autoAssist && !IntentUtil.isAccessibilityOn()) {
            IntentUtil.toEnableAccessibility();
        }

    }

    private void setAutoFavorite() {
        boolean autoFavorite = SharedPrefUtil.getBoolean(Constants.KEY_SETTING_AUTO_FAVORITE, true);
        autoFavorite = !autoFavorite;
        SharedPrefUtil.putBoolean(Constants.KEY_SETTING_AUTO_FAVORITE, autoFavorite);
        setting_button_4.setText(autoFavorite ? R.string.button_opened : R.string.button_closed);

    }

    private void setRememberLast() {
        boolean rememberLast = SharedPrefUtil.getBoolean(Constants.KEY_SETTING_REMEMBER_LAST, true);
        rememberLast = !rememberLast;
        SharedPrefUtil.putBoolean(Constants.KEY_SETTING_REMEMBER_LAST, rememberLast);
        setting_button_2.setText(rememberLast ? R.string.button_opened : R.string.button_closed);

    }

    private void setExpandHistory() {
        boolean showHistory = SharedPrefUtil.getBoolean(Constants.KEY_SETTING_EXPAND_HISTORY, false);
        showHistory = !showHistory;
        SharedPrefUtil.putBoolean(Constants.KEY_SETTING_EXPAND_HISTORY, showHistory);
        setting_button_1.setText(showHistory ? R.string.button_opened : R.string.button_closed);

    }

    private void backup() {
        BackupUtil.backup();
    }

    private void restore() {
        BackupUtil.restore();
        sendBroadcast(new Intent(Constants.INTENT_ACTION_RESTORE));
        setting_6_tip.setText(BackupUtil.needRestore() ? R.string.settings_6_tip_2 : R.string.settings_6_tip);
    }

    @SuppressLint("BatteryLife")
    private void ignore() {
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
