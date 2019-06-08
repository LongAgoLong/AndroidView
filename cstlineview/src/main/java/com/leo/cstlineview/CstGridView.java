package com.leo.cstlineview;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

/**
 * Created by LEO
 * On 2019/6/7
 * Description:宫格布局
 */
public class CstGridView extends ViewGroup {

    private int gap;            // 宫格间距

    private int columnCount;    // 设置的列数
    private int rowCount;       // 行数
    private int gridWidth;      // 宫格宽度
    private int maxGridHeight;  // 宫格高度

    private AdapterDataSetObserver mDataSetObserver;

    private CstBaseAdapter adapter;
    private OnCstItemClickCallback onCstItemClickCallback;

    public CstGridView(Context context) {
        this(context, null);
    }

    public CstGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CstGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CstGridView);
            gap = a.getDimensionPixelSize(R.styleable.CstGridView_cst_grid_gap, 0);
            columnCount = a.getInt(R.styleable.CstGridView_cst_grid_column, 4);
            a.recycle();
        } else {
            gap = 0;
            columnCount = 4;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        int totalWidth = width - getPaddingLeft() - getPaddingRight();
        if (adapter != null && adapter.getCount() > 0) {
            gridWidth = (totalWidth - gap * (columnCount - 1)) / columnCount;
            /**
             * 先测量子View，获取最大高度
             */
            for (int i = 0; i < adapter.getCount(); i++) {
                View child = getChildAt(i);
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                int measuredHeight = child.getMeasuredHeight();
                maxGridHeight = Math.max(measuredHeight, maxGridHeight);
            }
            width = gridWidth * columnCount + gap * (columnCount - 1) + getPaddingLeft() + getPaddingRight();
            height = maxGridHeight * rowCount + gap * (rowCount - 1) + getPaddingTop() + getPaddingBottom();
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (adapter == null) return;
        int childrenCount = adapter.getCount();
        for (int i = 0; i < childrenCount; i++) {
            View childrenView = getChildAt(i);
            int rowNum = i / columnCount;
            int columnNum = i % columnCount;
            int left = (gridWidth + gap) * columnNum + getPaddingLeft();
            int top = (maxGridHeight + gap) * rowNum + getPaddingTop();
            int right = left + gridWidth;
            int bottom = top + maxGridHeight;
            childrenView.layout(left, top, right, bottom);
        }
    }

    public void setOnCstItemClickCallback(final OnCstItemClickCallback onCstItemClickCallback) {
        this.onCstItemClickCallback = onCstItemClickCallback;
        if (adapter != null) {
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                final Object obj = adapter.getItem(i);
                final int tmp = i;
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onCstItemClickCallback != null) {
                            onCstItemClickCallback.onItemClicked(view, obj, tmp);
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
     * 添加子项
     */
    private void bindView() {
        removeAllViews();
        if (adapter == null || adapter.getCount() == 0) {
            setVisibility(GONE);
            return;
        } else {
            setVisibility(VISIBLE);
        }
        int imageCount = adapter.getCount();
        /**
         * 默认是4列显示，行数根据图片的数量决定
         */
        rowCount = imageCount / columnCount + (imageCount % columnCount == 0 ? 0 : 1);
        for (int i = 0; i < imageCount; i++) {
            final View view = adapter.getView(i, null, this);
            final int tmp = i;
            final Object obj = adapter.getItem(i);
            /**
             * view 点击事件触发时回调我们自己的接口
             */
            if (onCstItemClickCallback != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCstItemClickCallback != null) {
                            onCstItemClickCallback.onItemClicked(v, obj, tmp);
                        }
                    }
                });
            }
            addView(view, generateDefaultLayoutParams());
        }
        requestLayout();
    }

    /**
     * 设置适配器
     */
    public void setAdapter(@NonNull CstBaseAdapter adapter) {
        if (this.adapter != null && mDataSetObserver != null) {
            this.adapter.unregisterDataSetObserver(mDataSetObserver);
            this.adapter.recycle();
        }
        this.adapter = adapter;
        mDataSetObserver = new AdapterDataSetObserver();
        this.adapter.registerDataSetObserver(mDataSetObserver);
        bindView();
    }

    /**
     * 设置宫格间距
     */
    public void setGap(int spacing) {
        gap = spacing;
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

    public void recycle() {
        if (null != adapter) {
            adapter.recycle();
        }
    }
}