package com.sohu.inputmethod.sogou.ui.adapter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiao.gifin.R;
import com.sohu.inputmethod.sogou.Constants;
import com.sohu.inputmethod.sogou.util.GsonUtil;
import com.sohu.inputmethod.sogou.util.SharedPrefUtil;
import com.sohu.inputmethod.sogou.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qiao on 2018/12/21.
 */
public class GifGridAdapter extends RecyclerView.Adapter<GifGridAdapter.GifHolder> {
    private boolean mIsFavorite;
    private ArrayList<String> mList = new ArrayList<>();

    @NonNull
    @Override
    public GifGridAdapter.GifHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new GifHolder(View.inflate(viewGroup.getContext(), R.layout.item_gif, null));
    }

    @Override
    public void onBindViewHolder(@NonNull GifGridAdapter.GifHolder viewHolder, int i) {
        viewHolder.loadGif(mList.get(i));
    }

    class GifHolder extends RecyclerView.ViewHolder {

        private ImageView item_gif;
        private View item_cover;
        private TextView item_favorite;
        private TextView item_cancel;

        GifHolder(@NonNull View itemView) {
            super(itemView);
            item_gif = itemView.findViewById(R.id.item_gif);

            item_cover = itemView.findViewById(R.id.item_cover);
            item_favorite = itemView.findViewById(R.id.item_favorite);
            item_cancel = itemView.findViewById(R.id.item_cancel);
            ViewGroup.LayoutParams layoutParams = item_gif.getLayoutParams();
            int size = (Resources.getSystem().getDisplayMetrics().widthPixels - 4) / 5;
            layoutParams.height = layoutParams.width = size;
            item_gif.requestLayout();
            ViewGroup.LayoutParams layoutParams2 = item_cover.getLayoutParams();
            layoutParams2.height = layoutParams2.width = size;
            item_cover.requestLayout();
        }

        void loadGif(String url) {
            item_cover.setVisibility(View.GONE);
            item_favorite.setText(mIsFavorite ? "删除" : "收藏");
            Glide.with(itemView.getContext())
                    .load(url)
                    .centerCrop()
                    .placeholder(R.color.colorBackground)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(item_gif);
            item_gif.setOnClickListener(v -> mOnItemClickListener.onItemClick(url));
            item_gif.setOnLongClickListener(v -> {
                item_cover.setVisibility(View.VISIBLE);
                return true;
            });
            item_favorite.setOnClickListener(v -> {
                item_cover.setVisibility(View.GONE);
                if (mIsFavorite) {
                    deFavorite(url);
                } else {
                    favorite(url);
                }
                ToastUtil.showToast(mIsFavorite ? "已删除" : "已收藏");
            });
            item_cancel.setOnClickListener(v -> item_cover.setVisibility(View.GONE));
        }
    }

    public void setFavorite(boolean isFavorite) {
        mIsFavorite = isFavorite;
        notifyDataSetChanged();
        SharedPrefUtil.putBoolean(Constants.KEY_SETTING_IS_FAVORITE, isFavorite);
    }

    public boolean isFavorite() {
        return mIsFavorite;
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
    }

    private void deFavorite(String url) {
        String json = SharedPrefUtil.getString(Constants.KEY_FAVORITE_LIST);
        List<String> list = GsonUtil.fromJsonList(json, String.class);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.remove(url);
        SharedPrefUtil.putString(Constants.KEY_FAVORITE_LIST, GsonUtil.toJson(list));
        remove(url);
    }

    @Override
    public int getItemCount() {
        return mList.size();
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
        void onItemClick(String url);
    }
}
