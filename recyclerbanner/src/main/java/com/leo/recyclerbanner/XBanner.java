package com.leo.recyclerbanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.leo.recyclerbanner.callback.IPageSelectedListener;
import com.leo.recyclerbanner.layoutmanager.RepeatLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LEO
 * On 2019/6/24
 * Description:轮播图
 */
public class XBanner extends RecyclerView {
    private static final int INTERVAL_TIME = 4000;

    private final List<IPageSelectedListener> iPageSelectedListeners = new ArrayList<>();
    private XBannerAdapter adapter;
    private int intervalMilli = INTERVAL_TIME;

    private int currentIndex;
    private int startX, startY;

    private boolean isPlaying;

    private static final Handler mUIHandler = new Handler(Looper.getMainLooper());
    private int orientation;

    public XBanner(Context context) {
        this(context, null);
        initAttrs(context, null, 0);
    }

    public XBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initAttrs(context, attrs, 0);
    }

    public XBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
    }

    /**
     * 相关属性初始化
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XBanner, defStyleAttr, 0);
            intervalMilli = a.getInt(R.styleable.XBanner_intervalTime, INTERVAL_TIME);
            orientation = a.getInt(R.styleable.XBanner_orientation, OrientationHelper.HORIZONTAL);
            a.recycle();
        } else {
            intervalMilli = INTERVAL_TIME;
            orientation = OrientationHelper.HORIZONTAL;
        }
    }

    public void setXBannerAdapter(@NonNull XBannerAdapter adapter) {
        if (adapter.isLoop()) {
            setLayoutManager(new RepeatLayoutManager(getContext(), orientation));
        } else {
            setLayoutManager(new LinearLayoutManager(getContext(), orientation, false));
        }
        this.adapter = adapter;
        setAdapter(adapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper() {
            @Override
            public int findTargetSnapPosition(LayoutManager layoutManager, int velocityX, int velocityY) {
                int targetPos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
                notifyPageSelected(targetPos, true);
                return targetPos;
            }
        };
        snapHelper.attachToRecyclerView(this);
        notifyPageSelected(currentIndex, false);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                setPlaying(false);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();
                int disX = moveX - startX;
                int disY = moveY - startY;
                getParent().requestDisallowInterceptTouchEvent(2 * Math.abs(disX) > Math.abs(disY));
                setPlaying(false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setPlaying(true);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setPlaying(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setPlaying(false);
    }

    public void setCurrentIndex(int index) {
        mUIHandler.postDelayed(() -> {
            if (null == adapter || adapter.getItemCount() == 0
                    || index >= adapter.getItemCount()) {
                return;
            }
            smoothScrollToPosition(index);
            notifyPageSelected(index, false);
        }, 100);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * 页面切换监听
     *
     * @param iPageSelectedListener
     */
    public void addPageSelectedListener(@NonNull IPageSelectedListener iPageSelectedListener) {
        this.iPageSelectedListeners.add(iPageSelectedListener);
    }

    public void removePageSelectedListener(@NonNull IPageSelectedListener iPageSelectedListener) {
        this.iPageSelectedListeners.remove(iPageSelectedListener);
    }

    /**
     * 间隔时间
     *
     * @param millisecond
     */
    public void setIntervalMilli(int millisecond) {
        if (millisecond <= 0) {
            intervalMilli = 0;
        } else if (intervalMilli < 2000) {
            intervalMilli = 2000;
        } else {
            intervalMilli = millisecond;
        }
    }

    private boolean isAutoPlay() {
        return intervalMilli >= 2000;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public synchronized void setPlaying(boolean playing) {
        if (playing) {
            if (isAutoPlay() && !isPlaying()) {
                isPlaying = true;
                resetDelayed();
            }
        } else {
            if (isPlaying()) {
                mUIHandler.removeCallbacks(playTask);
                isPlaying = false;
            }
        }
    }

    private final Runnable playTask = new Runnable() {
        @Override
        public void run() {
            if (!isAutoPlay()) {
                resetDelayed();
                return;
            }
            int index;
            if (currentIndex >= adapter.getItemCount() - 1) {
                index = 0;
            } else {
                index = ++currentIndex;
            }
            smoothScrollToPosition(index);
            resetDelayed();
            notifyPageSelected(index, false);
        }
    };

    private synchronized void notifyPageSelected(int index, boolean isUserTouch) {
        if (index >= 0) {
            currentIndex = index;
            for (IPageSelectedListener iPageSelectedListener : iPageSelectedListeners) {
                iPageSelectedListener.onPageSelect(currentIndex, isUserTouch);
            }
        }
    }

    private synchronized void resetDelayed() {
        mUIHandler.removeCallbacks(playTask);
        if (isAutoPlay() && isPlaying()) {
            mUIHandler.postDelayed(playTask, intervalMilli);
        }
    }
}