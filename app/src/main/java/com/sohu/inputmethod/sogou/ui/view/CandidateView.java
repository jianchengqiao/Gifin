package com.sohu.inputmethod.sogou.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qiao.gifin.R;
import com.sohu.inputmethod.sogou.Constants;
import com.sohu.inputmethod.sogou.ui.adapter.HistoryAdapter;
import com.sohu.inputmethod.sogou.util.GsonUtil;
import com.sohu.inputmethod.sogou.util.SharedPrefUtil;

import java.util.List;

/**
 * Created by Qiao on 2018/12/25.
 */
public class CandidateView extends FrameLayout {
    private final HistoryAdapter mHistoryAdapter;
    private RadioGroup candidate_group;
    private RadioButton candidate_radio_1;
    private RadioButton candidate_radio_3;
    private RadioButton candidate_radio_4;
    private TextView candidate_clear;
    private RecyclerView candidate_list;
    private CandidateViewActionListener mActionListener;

    public CandidateView(@NonNull Context context) {
        this(context, null);
    }

    public CandidateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CandidateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_candidate_view, this);

        candidate_group = findViewById(R.id.candidate_group);
        candidate_radio_1 = findViewById(R.id.candidate_radio_1);
        candidate_radio_3 = findViewById(R.id.candidate_radio_3);
        candidate_radio_4 = findViewById(R.id.candidate_radio_4);
        candidate_clear = findViewById(R.id.candidate_clear);
        candidate_list = findViewById(R.id.candidate_list);

        candidate_radio_1.setTag(1);
        candidate_radio_3.setTag(3);
        candidate_radio_4.setTag(4);

        candidate_list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mHistoryAdapter = new HistoryAdapter();
        mHistoryAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String text) {
                mActionListener.onHistoryClicked(text);
            }

            @Override
            public void onItemClickDelete(String text) {
                mHistoryAdapter.remove(text);
                SharedPrefUtil.putString(Constants.KEY_WORDS_LIST, GsonUtil.toJson(mHistoryAdapter.getList()));
            }
        });
        candidate_list.setAdapter(mHistoryAdapter);
        candidate_clear.setOnClickListener(v -> {
            mHistoryAdapter.clear();
            SharedPrefUtil.remove(Constants.KEY_WORDS_LIST);
        });

        if (isInEditMode()) return;

        candidate_group.check(candidate_group.findViewWithTag(SharedPrefUtil.getInt(Constants.KEY_GIF_SOURCE, 1)).getId());

        candidate_group.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPrefUtil.putInt(Constants.KEY_GIF_SOURCE, (Integer) group.findViewById(checkedId).getTag());
            mActionListener.onSourceChanged();
        });

        loadHistory();
    }

    public void loadHistory() {
        mHistoryAdapter.clear();
        String list = SharedPrefUtil.getString(Constants.KEY_WORDS_LIST);
        if (list != null) {
            List<String> stringList = GsonUtil.fromJsonList(list, String.class);
            if (stringList != null) {
                mHistoryAdapter.addAll(stringList);
            }
        }
    }

    public void setActionListener(CandidateViewActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface CandidateViewActionListener {
        void onSourceChanged();

        void onHistoryClicked(String text);

        void onClose();
    }
}
