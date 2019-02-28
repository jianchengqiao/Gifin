package com.sohu.inputmethod.sogou.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiao.gifin.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qiao on 2018/12/21.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {
    private ArrayList<String> mList = new ArrayList<>();

    @NonNull
    @Override
    public HistoryAdapter.HistoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HistoryHolder(View.inflate(viewGroup.getContext(), R.layout.item_history, null));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.HistoryHolder viewHolder, int i) {
        viewHolder.setData(mList.get(i));
    }

    class HistoryHolder extends RecyclerView.ViewHolder {
        private TextView item_history_text;
        private View item_click_view;
        private ImageView item_history_delete;

        HistoryHolder(@NonNull View itemView) {
            super(itemView);
            item_history_text = itemView.findViewById(R.id.item_history_text);
            item_click_view = itemView.findViewById(R.id.item_click_view);
            item_history_delete = itemView.findViewById(R.id.item_history_delete);

        }

        void setData(String text) {
            item_history_text.setText(text);
            item_click_view.setOnClickListener(v -> {
                mOnItemClickListener.onItemClick(text);
            });
            item_history_delete.setOnClickListener(v -> mOnItemClickListener.onItemClickDelete(text));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public ArrayList<String> getList() {
        return mList;
    }

    public void addAll(List<String> list) {
        int start = mList.size();
        mList.addAll(list);
        notifyItemRangeInserted(start, list.size());
    }

    public void remove(String item) {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).equals(item)) {
                mList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(String text);

        void onItemClickDelete(String text);
    }
}
