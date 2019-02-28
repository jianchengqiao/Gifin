package com.sohu.inputmethod.sogou.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.qiao.gifin.R;
import com.sohu.inputmethod.sogou.Constants;
import com.sohu.inputmethod.sogou.http.HttpRequest;
import com.sohu.inputmethod.sogou.http.ResponseSubscriber;
import com.sohu.inputmethod.sogou.ui.adapter.GifGridAdapter;
import com.sohu.inputmethod.sogou.ui.activity.Settings;
import com.sohu.inputmethod.sogou.util.GsonUtil;
import com.sohu.inputmethod.sogou.util.IntentUtil;
import com.sohu.inputmethod.sogou.util.SharedPrefUtil;
import com.sohu.inputmethod.sogou.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qiao on 2018/12/21.
 */
public class InputView extends FrameLayout {
    private final GifGridAdapter mGifGridAdapter;
    private ImageView input_setting;
    private TextView input_enter;
    private TextView input_search;
    private TextView input_favorite;
    private TextView input_operation;
    private TextView input_close;
    private RecyclerView input_grid;
    private TextView input_tip;
    private View input_progress;
    private InputViewActionListener mInputViewActionListener;
    private boolean mIsShift;

    public InputView(@NonNull Context context) {
        super(context);
        inflate(context, R.layout.layout_input_view, this);

        input_setting = findViewById(R.id.input_setting);
        input_enter = findViewById(R.id.input_enter);
        input_search = findViewById(R.id.input_search);
        input_favorite = findViewById(R.id.input_favorite);
        input_operation = findViewById(R.id.input_operation);
        input_close = findViewById(R.id.input_close);
        input_grid = findViewById(R.id.input_grid);
        input_tip = findViewById(R.id.input_tip);
        input_progress = findViewById(R.id.input_progress);

        input_grid.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = outRect.left = outRect.right = outRect.top = 1;
            }
        });
        input_grid.setLayoutManager(new GridLayoutManager(getContext(), 5));
        mGifGridAdapter = new GifGridAdapter();
        mGifGridAdapter.setOnItemClickListener(this::sendGif);
        input_grid.setAdapter(mGifGridAdapter);
        input_tip.setOnClickListener(v -> searchGif(getText()));

        input_setting.setOnClickListener(v -> mInputViewActionListener.onSettingClick());
        input_setting.setOnLongClickListener(v -> {
            Intent intent = new Intent(getContext(), Settings.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
            return true;
        });
        input_enter.setOnClickListener(v -> mInputViewActionListener.onSearchClick());
        input_search.setOnClickListener(v -> search());
        input_favorite.setOnClickListener(v -> favorite());
        input_close.setOnClickListener(v -> close());
        input_operation.setOnClickListener(v -> operation());
        input_close.setOnLongClickListener(v -> {
            IntentUtil.showInputMethodPicker();
            return true;
        });

    }

    private void search() {
        if (mIsShift) {
            mInputViewActionListener.onCommitText(input_enter.getText().toString());
        } else {
            String text = getText();
            if (TextUtils.isEmpty(text)) {
                mInputViewActionListener.onSearchClick();
            } else {
                searchGif(text);
            }
        }
    }

    public void favorite() {
        if (mIsShift) {
            mInputViewActionListener.onSendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        } else {
            mGifGridAdapter.clear();
            input_grid.setVisibility(VISIBLE);
            String json = SharedPrefUtil.getString(Constants.KEY_FAVORITE_LIST);
            List<String> list = GsonUtil.fromJsonList(json, String.class);
            if (list == null || list.size() == 0) {
                input_tip.setVisibility(VISIBLE);
                input_tip.setText("还没有收藏，长按表情收藏");
            } else {
                input_tip.setVisibility(GONE);
                mGifGridAdapter.addAll(list);
            }
            mGifGridAdapter.setFavorite(true);
        }
    }

    private void close() {
        if (mIsShift) {
            mInputViewActionListener.onSendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
        } else {
            mInputViewActionListener.onCloseClick();
        }
    }

    private void operation() {
        mIsShift = !mIsShift;
        input_search.setText(mIsShift ? R.string.button_text : R.string.button_search);
        input_favorite.setText(mIsShift ? R.string.button_delete : R.string.button_favorite);
        input_close.setText(mIsShift ? R.string.button_send : R.string.button_close);
    }

    private void sendGif(String url) {
        Glide.with(getContext())
                .load(url)
                .downloadOnly(new SimpleTarget<File>(200, 200) {
                    @Override
                    public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                        mInputViewActionListener.onClearText();
                        mInputViewActionListener.onCommitText(resource.getAbsolutePath());
                        favorite(url);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        ToastUtil.showToast("下载失败");
                    }
                });
    }

    public void searchGif(String text) {
        if (TextUtils.isEmpty(text)) return;
        saveWords(text);
        input_progress.setVisibility(VISIBLE);
        input_tip.setVisibility(GONE);
        input_enter.setText(text);
        mGifGridAdapter.setFavorite(false);
        HttpRequest.getInstance().getGifList(text, new ResponseSubscriber<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> model) {
                input_grid.setVisibility(VISIBLE);
                input_progress.setVisibility(GONE);
                mGifGridAdapter.clear();
                if (model != null && model.size() > 0) {
                    mGifGridAdapter.addAll(model);
                    input_tip.setVisibility(GONE);
                } else {
                    input_tip.setVisibility(VISIBLE);
                    input_tip.setText("搜索结果为空");
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                ToastUtil.showToast("搜索失败:" + msg);
                input_progress.setVisibility(GONE);
                if (mGifGridAdapter.getItemCount() == 0) {
                    input_tip.setVisibility(VISIBLE);
                    input_tip.setText("搜索失败，点击重试");
                } else {
                    input_tip.setVisibility(GONE);
                }
            }
        });
    }

    public void clear() {
        mGifGridAdapter.clear();
        input_grid.setVisibility(GONE);
        input_progress.setVisibility(GONE);
        input_tip.setVisibility(VISIBLE);
        input_tip.setText(R.string.tip_search);
    }

    private void saveWords(String text) {
        SharedPrefUtil.putString(Constants.KEY_CURRENT_WORD, text);
        List<String> list = GsonUtil.fromJsonList(SharedPrefUtil.getString(Constants.KEY_WORDS_LIST), String.class);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.remove(text);
        list.add(0, text);
        SharedPrefUtil.putString(Constants.KEY_WORDS_LIST, GsonUtil.toJson(list));
        mInputViewActionListener.onSearched();
    }

    public String getText() {
        return input_enter.getText().toString().trim();
    }

    private void favorite(String url) {
        String json = SharedPrefUtil.getString(Constants.KEY_FAVORITE_LIST);
        List<String> list = GsonUtil.fromJsonList(json, String.class);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.remove(url);
        list.add(0, url);
        SharedPrefUtil.putString(Constants.KEY_FAVORITE_LIST, GsonUtil.toJson(list));
        if (mGifGridAdapter.isFavorite()) {
            mGifGridAdapter.clear();
            mGifGridAdapter.addAll(list);
            mGifGridAdapter.notifyDataSetChanged();
        }
    }

    public boolean isFavorite() {
        return SharedPrefUtil.getBoolean(Constants.KEY_SETTING_IS_FAVORITE, false);
    }

    public void setInputViewActionListener(InputViewActionListener inputViewActionListener) {
        mInputViewActionListener = inputViewActionListener;
    }

    public interface InputViewActionListener {
        void onSettingClick();

        void onSearchClick();

        void onCloseClick();

        void onCommitText(String text);

        void onClearText();

        void onSendKeyEvent(KeyEvent keyEvent);

        void onSearched();
    }
}
