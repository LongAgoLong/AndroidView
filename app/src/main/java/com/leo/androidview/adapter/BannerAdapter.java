package com.leo.androidview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leo.androidview.R;
import com.leo.recyclerbanner.XBannerAdapter;
import com.leo.recyclerbanner.callback.IBannerData;

public class BannerAdapter extends XBannerAdapter {
    @Override
    public boolean isLoop() {
        return true;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new VHolder(inflater.inflate(R.layout.item_demo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VHolder vHolder = (VHolder) holder;
        IBannerData iBannerData = mData.get(position);
        vHolder.tvDesc.setText(position + "");
        vHolder.data = iBannerData.getUrl();
        holder.itemView.setOnClickListener(view -> {
            if (null != mPageClickListener) {
                mPageClickListener.onClick(holder.getAdapterPosition(), iBannerData, view);
            }
        });
    }

    protected static class VHolder extends RecyclerView.ViewHolder {
        public String data;
        public TextView tvDesc;

        public VHolder(@NonNull View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.tv_text);
        }
    }
}
