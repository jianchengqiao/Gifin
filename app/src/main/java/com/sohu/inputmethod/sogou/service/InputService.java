package com.sohu.inputmethod.sogou.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.qiao.gifin.R;
import com.sohu.inputmethod.sogou.App;
import com.sohu.inputmethod.sogou.Constants;
import com.sohu.inputmethod.sogou.ui.view.CandidateView;
import com.sohu.inputmethod.sogou.ui.dialog.FirstTipDialog;
import com.sohu.inputmethod.sogou.ui.view.InputView;
import com.sohu.inputmethod.sogou.ui.activity.SearchUI;
import com.sohu.inputmethod.sogou.util.SharedPrefUtil;

import java.util.List;

/**
 * Created by Qiao on 2018/12/20.
 */
public class InputService extends InputMethodService {

    private InputView mInputView;
    private boolean mIsCandidateViewShown;
    private CandidateView mCandidateView;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver();
        if (SharedPrefUtil.getBoolean(Constants.KEY_FIRST_TIP_SHOWN, true)) {
            Intent intent = new Intent(this, FirstTipDialog.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            SharedPrefUtil.putBoolean(Constants.KEY_FIRST_TIP_SHOWN, false);
        }
        mIsCandidateViewShown = SharedPrefUtil.getBoolean(Constants.KEY_SETTING_EXPAND_HISTORY, true);
    }

    @Override
    public void onInitializeInterface() {
        super.onInitializeInterface();
    }

    @Override
    public AbstractInputMethodImpl onCreateInputMethodInterface() {
        return super.onCreateInputMethodInterface();
    }

    @Override
    public View onCreateInputView() {
        if (mInputView == null) {
            mInputView = new InputView(getApplicationContext());
            mCandidateView = mInputView.findViewById(R.id.input_candidate_view);
        }
        mInputView.setInputViewActionListener(new InputView.InputViewActionListener() {
            @Override
            public void onSettingClick() {
                mIsCandidateViewShown = !mIsCandidateViewShown;
                mCandidateView.setVisibility(mIsCandidateViewShown ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onSearchClick() {
                switchNextInput();
                Intent intent = new Intent(InputService.this, SearchUI.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onCloseClick() {
                switchNextInput();
            }

            @Override
            public void onCommitText(String text) {
                getCurrentInputConnection().commitText(text, 0);
            }

            @Override
            public void onClearText() {
                getCurrentInputConnection().deleteSurroundingText(100, 100);
            }

            @Override
            public void onSendKeyEvent(KeyEvent keyEvent) {
                getCurrentInputConnection().sendKeyEvent(keyEvent);
            }

            @Override
            public void onSearched() {
                mCandidateView.loadHistory();
            }
        });
        mCandidateView.setActionListener(new CandidateView.CandidateViewActionListener() {
            @Override
            public void onSourceChanged() {
                mInputView.searchGif(mInputView.getText());
            }

            @Override
            public void onHistoryClicked(String text) {
                mInputView.searchGif(text);
            }

            @Override
            public void onClose() {
                mIsCandidateViewShown = false;
                mCandidateView.setVisibility(View.GONE);
            }
        });
        return mInputView;
    }

    @Override
    public View onCreateCandidatesView() {
        return super.onCreateCandidatesView();
    }

    @Override
    public View onCreateExtractTextView() {
        return super.onCreateExtractTextView();
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        long last = SharedPrefUtil.getLong(Constants.KEY_TIMESTAMP_SEARCH, 0);
        if (System.currentTimeMillis() - last < 1000) {
            String text = SharedPrefUtil.getString(Constants.KEY_CURRENT_WORD);
            mInputView.searchGif(text);
        } else {
            CharSequence textBeforeCursor = getCurrentInputConnection().getTextBeforeCursor(100, 0);
            boolean remember = SharedPrefUtil.getBoolean(Constants.KEY_SETTING_REMEMBER_LAST, true);
            if (textBeforeCursor != null && textBeforeCursor.length() > 0) {
                mInputView.searchGif(textBeforeCursor.toString());
            } else if (mInputView.isFavorite()) {
                mInputView.favorite();
            } else if (remember) {
                String text = SharedPrefUtil.getString(Constants.KEY_CURRENT_WORD);
                mInputView.searchGif(text);
            }
        }
        setCandidatesViewShown(SharedPrefUtil.getBoolean(Constants.KEY_SETTING_EXPAND_HISTORY, false));
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_ACTION_RESTORE);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mCandidateView != null) {
                    mCandidateView.loadHistory();
                }
                if (mInputView != null && mInputView.isFavorite()) {
                    mInputView.favorite();
                }
            }
        };
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void switchNextInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            List<InputMethodInfo> inputMethodList = inputMethodManager.getEnabledInputMethodList();
            if (inputMethodList != null && inputMethodList.size() > 0) {
                for (InputMethodInfo info : inputMethodList) {
                    if (!info.getId().equals(App.getContext().getPackageName() + "/" + InputService.class.getCanonicalName())) {
                        switchInputMethod(info.getId());
                        break;
                    }
                }
            }
        }
    }
}
