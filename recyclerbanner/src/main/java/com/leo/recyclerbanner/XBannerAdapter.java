package com.leo.recyclerbanner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.leo.recyclerbanner.callback.IBannerData;
import com.leo.recyclerbanner.callback.IPageClickListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class XBannerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected final List<IBannerData> mData = new ArrayList<>();
    protected IPageClickListener mPageClickListener;

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public abstract boolean isLoop();

    public void setPageClickListener(IPageClickListener iPageClickListener) {
        this.mPageClickListener = iPageClickListener;
    }

    public void updateData(@NonNull List<? extends IBannerData> data) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BannerDiffCallback(mData, data));
        diffResult.dispatchUpdatesTo(this);
        mData.clear();
        mData.addAll(data);
    }

    private static class BannerDiffCallback extends DiffUtil.Callback {
        private final List<? extends IBannerData> oldData;
        private final List<? extends IBannerData> newData;

        public BannerDiffCallback(List<? extends IBannerData> oldData,
                                  List<? extends IBannerData> newData) {
            this.oldData = oldData;
            this.newData = newData;
        }

        @Override
        public int getOldListSize() {
            return null != oldData ? oldData.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return null != newData ? newData.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldData.get(oldItemPosition).equals(newData.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldData.get(oldItemPosition).equals(newData.get(newItemPosition));
        }
    }
}
