package com.leo.recyclerbanner.callback;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public interface ICreateAdapterCallback {
    RecyclerView.ViewHolder createHold(ViewGroup parent, int viewType);

    void bindHold(RecyclerView.ViewHolder holder, int position);
}
