package com.leo.cstlineview;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by LEO
 * On 2019/6/7
 * Description:线性列表布局
 */
public class CstLinearView extends LinearLayout {
    private AdapterDataSetObserver mDataSetObserver;

    private CstBaseAdapter adapter;
    private ICstItemClickCallback iCstItemClickCallback;

    public CstLinearView(Context context) {
        super(context);
    }

    public CstLinearView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(CstBaseAdapter adapter) {
        if (this.adapter != null && mDataSetObserver != null) {
            this.adapter.unregisterDataSetObserver(mDataSetObserver);
            this.adapter.recycle();
        }
        this.adapter = adapter;
        if (this.adapter != null) {
            mDataSetObserver = new AdapterDataSetObserver();
            this.adapter.registerDataSetObserver(mDataSetObserver);
        }
        bindView();
    }

    public void setOnItemClickCallback(final ICstItemClickCallback iCstItemClickCallback) {
        this.iCstItemClickCallback = iCstItemClickCallback;
        if (adapter != null) {
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                final Object obj = adapter.getItem(i);
                final int tmp = i;
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (iCstItemClickCallback != null) {
                            iCstItemClickCallback.onItemClicked(view, obj, tmp);
                        }
                    }
                });
            }
        }
    }

    private class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            bindView();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }

    /**
     * 绑定 adapter 中所有的 view
     */
    private void bindView() {
        if (adapter == null) {
            return;
        }
        removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            final View view = adapter.getView(i, null, this);
            final int tmp = i;
            final Object obj = adapter.getItem(i);
            /**
             * view 点击事件触发时回调我们自己的接口
             */
            if (iCstItemClickCallback != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (iCstItemClickCallback != null) {
                            iCstItemClickCallback.onItemClicked(v, obj, tmp);
                        }
                    }
                });
            }
            addView(view);
        }
    }

    public void recycle() {
        if (null != adapter) {
            adapter.recycle();
        }
    }
}