package com.leo.layoutmanager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;


public class RepeatLayoutManager extends RecyclerView.LayoutManager {
    private Context mContext;
    @RecyclerView.Orientation
    private int mOrientation = RecyclerView.HORIZONTAL;

    public RepeatLayoutManager(Context context, @RecyclerView.Orientation int orientation) {
        mContext = context;
        mOrientation = orientation;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean isAutoMeasureEnabled() {
        return super.isAutoMeasureEnabled();
    }

    @Override
    public boolean canScrollHorizontally() {
        return mOrientation == RecyclerView.HORIZONTAL;
    }

    @Override
    public boolean canScrollVertically() {
        return mOrientation == RecyclerView.VERTICAL;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }
        /**
         * 将所有Item分离至scrap
         */
        detachAndScrapAttachedViews(recycler);
        if (mOrientation == RecyclerView.HORIZONTAL) {
            int itemLeft = getPaddingLeft();
            for (int i = 0; ; i++) {
                if (itemLeft >= getWidth() - getPaddingRight()) {
                    return;
                }
                View itemView = recycler.getViewForPosition(i % getItemCount());
                addView(itemView);
                /**
                 * 测量子view
                 */
                measureChildWithMargins(itemView, 0, 0);
                int right = itemLeft + getDecoratedMeasuredWidth(itemView);
                int top = getPaddingTop();
                int bottom = top + getDecoratedMeasuredHeight(itemView) - getPaddingBottom();
                layoutDecorated(itemView, itemLeft, top, right, bottom);
                itemLeft = right;
            }
        } else {
            int itemTop = getPaddingTop();
            for (int i = 0; ; i++) {
                if (itemTop >= getHeight() - getPaddingBottom()) {
                    return;
                }
                View itemView = recycler.getViewForPosition(i % getItemCount());
                addView(itemView);
                measureChildWithMargins(itemView, 0, 0);
                int left = getPaddingLeft();
                int right = left + getDecoratedMeasuredWidth(itemView) - getPaddingRight();
                int bottom = itemTop + getDecoratedMeasuredHeight(itemView);
                layoutDecorated(itemView, left, itemTop, right, bottom);
                itemTop = bottom;
            }
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        fillHorizontal(recycler, dx > 0);
        offsetChildrenHorizontal(-dx);
        recyclerChildViewHorizontal(recycler, dx > 0);
        return dx;
    }

    /**
     * @param recycler
     * @param fillEnd  是否填充尾部
     */
    private void fillHorizontal(RecyclerView.Recycler recycler, boolean fillEnd) {
        if (getItemCount() == 0) {
            return;
        }
        if (fillEnd) {
            View anchorView = getChildAt(getChildCount() - 1);
            int anchorPosition = getPosition(anchorView);
            while (anchorView.getRight() < getWidth() - getPaddingRight()) {
                int position = (++anchorPosition) % getItemCount();
                if (position < 0) {
                    position += getItemCount();
                }
                View itemView = recycler.getViewForPosition(position);
                addView(itemView);
                measureChildWithMargins(itemView, 0, 0);
                int left = anchorView.getRight();
                int right = left + getDecoratedMeasuredWidth(itemView);
                int top = getPaddingTop();
                int bottom = top + getDecoratedMeasuredHeight(itemView) - getPaddingBottom();
                layoutDecorated(itemView, left, top, right, bottom);
                anchorView = itemView;
            }
        } else {
            View anchorView = getChildAt(0);
            int anchorPosition = getPosition(anchorView);
            while (anchorView.getLeft() > getPaddingLeft()) {
                int position = (--anchorPosition) % getItemCount();
                if (position < 0) {
                    position += getItemCount();
                }
                View itemView = recycler.getViewForPosition(position);
                addView(itemView, 0);
                measureChildWithMargins(itemView, 0, 0);
                int top = getPaddingTop();
                int right = anchorView.getLeft();
                int left = right - getDecoratedMeasuredWidth(itemView);
                int bottom = top + getDecoratedMeasuredHeight(itemView) - getPaddingBottom();
                layoutDecorated(itemView, left, top, right, bottom);
                anchorView = itemView;
            }
        }
    }

    /**
     * 回收不可见的View
     *
     * @param recycler
     * @param fillEnd  是否填充尾部
     */
    private void recyclerChildViewHorizontal(RecyclerView.Recycler recycler, boolean fillEnd) {
        if (fillEnd) {
            // 回收头部
            for (int i = 0; ; i++) {
                View view = getChildAt(i);
                boolean needRecycle = null != view && view.getRight() < getPaddingLeft();
                if (needRecycle) {
                    removeAndRecycleView(view, recycler);
                    continue;
                }
                break;
            }
        } else {
            for (int i = getChildCount() - 1; ; i--) {
                View view = getChildAt(i);
                boolean needRecycle = null != view && view.getLeft() > getWidth() - getPaddingRight();
                if (needRecycle) {
                    removeAndRecycleView(view, recycler);
                    continue;
                }
                break;
            }
        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        fillVertical(recycler, dy > 0);
        offsetChildrenVertical(-dy);
        recyclerChildViewVertical(recycler, dy > 0);
        return dy;
    }

    /**
     * @param recycler
     * @param fillEnd  是否填充尾部
     */
    private void fillVertical(RecyclerView.Recycler recycler, boolean fillEnd) {
        if (getItemCount() == 0) {
            return;
        }
        if (fillEnd) {
            View anchorView = getChildAt(getChildCount() - 1);
            int anchorPosition = getPosition(anchorView);
            while (anchorView.getBottom() < getHeight() - getPaddingBottom()) {
                int position = (++anchorPosition) % getItemCount();
                if (position < 0) {
                    position += getItemCount();
                }
                View itemView = recycler.getViewForPosition(position);
                addView(itemView);
                measureChildWithMargins(itemView, 0, 0);
                int top = anchorView.getBottom();
                int left = getPaddingLeft();
                int right = left + getDecoratedMeasuredWidth(itemView) - getPaddingRight();
                int bottom = top + getDecoratedMeasuredHeight(itemView);
                layoutDecorated(itemView, left, top, right, bottom);
                anchorView = itemView;
            }
        } else {
            View anchorView = getChildAt(0);
            int anchorPosition = getPosition(anchorView);
            while (anchorView.getTop() > getPaddingTop()) {
                int position = (--anchorPosition) % getItemCount();
                if (position < 0) {
                    position += getItemCount();
                }
                View itemView = recycler.getViewForPosition(position);
                addView(itemView, 0);
                measureChildWithMargins(itemView, 0, 0);
                int bottom = anchorView.getTop();
                int top = bottom - getDecoratedMeasuredHeight(itemView);
                int left = getPaddingLeft();
                int right = left + getDecoratedMeasuredWidth(itemView) - getPaddingRight();
                layoutDecorated(itemView, left, top, right, bottom);
                anchorView = itemView;
            }
        }
    }

    /**
     * 回收不可见的View
     *
     * @param recycler
     * @param fillEnd  是否填充尾部
     */
    private void recyclerChildViewVertical(RecyclerView.Recycler recycler, boolean fillEnd) {
        if (fillEnd) {
            // 回收头部
            for (int i = 0; ; i++) {
                View view = getChildAt(i);
                boolean needRecycle = null != view && view.getBottom() < getPaddingTop();
                if (needRecycle) {
                    removeAndRecycleView(view, recycler);
                    continue;
                }
                break;
            }
        } else {
            for (int i = getChildCount() - 1; ; i--) {
                View view = getChildAt(i);
                boolean needRecycle = null != view && view.getTop() > getHeight() - getPaddingBottom();
                if (needRecycle) {
                    removeAndRecycleView(view, recycler);
                    continue;
                }
                break;
            }
        }
    }

}
