package com.sohu.inputmethod.sogou.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.qiao.gifin.R;
import com.sohu.inputmethod.sogou.util.IntentUtil;

/**
 * Created by Qiao on 2018/12/26.
 */
public class EnableAccessTipDialog extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_enable_access_tip);
        findViewById(R.id.tip_root).setOnClickListener(v -> finish());
    }

    @Override
    public void finish() {
        super.finish();
        IntentUtil.toEnableAccessibility();
    }
}
