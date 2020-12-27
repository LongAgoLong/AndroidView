package com.leo.recyclerbanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.leo.recyclerbanner.callback.IBannerEntity;
import com.leo.recyclerbanner.callback.ICreateAdapter;
import com.leo.recyclerbanner.callback.IPageChange;
import com.leo.recyclerbanner.callback.IPageClick;
import com.leo.recyclerbanner.callback.IPageDataChange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by LEO
 * On 2019/6/24
 * Description:轮播图
 * setLayoutManager first
 */
public class RecyclerBanner extends RecyclerView {
    private static final int INTERVAL_TIME = 4000;

    private ArrayList<IPageChange> iPageChanges;
    private ICreateAdapter iCreateAdapter;
    private IPageClick iPageClick;
    private IPageDataChange iPageDataChange;

    private BaseBannerAdapter adapter;
    private final List<IBannerEntity> dates = new ArrayList<>();

    private int currentIndex;
    private int startX, startY;

    private int intervalTime = INTERVAL_TIME;
    private boolean isLoop;
    private boolean isAutoPlay;
    private boolean isPlaying;

    private static final Handler mUIHandler = new Handler(Looper.getMainLooper());

    public RecyclerBanner(Context context) {
        this(context, null);
        initAttrs(context, null, 0);
    }

    public RecyclerBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initAttrs(context, attrs, 0);
    }

    public RecyclerBanner(Context context, AttributeSet attrs, int defStyleAttr) {
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
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerBanner, defStyleAttr, 0);
            isLoop = a.getBoolean(R.styleable.RecyclerBanner_rb_isLoop, true);
            isAutoPlay = a.getBoolean(R.styleable.RecyclerBanner_rb_isAutoPlay, true);
            intervalTime = a.getInt(R.styleable.RecyclerBanner_rb_intervalTime, INTERVAL_TIME);
            if (intervalTime < 2000) {
                intervalTime = 2000;
            }
            a.recycle();
        } else {
            isAutoPlay = true;
            isLoop = true;
            intervalTime = INTERVAL_TIME;
        }
    }

    public void init(@Nullable LayoutManager layout) {
        setLayoutManager(layout);
        adapter = new BaseBannerAdapter() {
            @Override
            public RecyclerView.ViewHolder createHold(ViewGroup parent, int viewType) {
                if (null != iCreateAdapter) {
                    return iCreateAdapter.createHold(parent, viewType);
                }
                return null;
            }

            @Override
            public void bindHold(RecyclerView.ViewHolder holder, int position) {
                if (null != iCreateAdapter) {
                    iCreateAdapter.bindHold(holder, position);
                }
            }
        };
        setAdapter(adapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper() {
            @Override
            public int findTargetSnapPosition(LayoutManager layoutManager, int velocityX, int velocityY) {
                int targetPos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
                if (targetPos >= 0) {
                    currentIndex = targetPos;
                    if (null != iPageChanges) {
                        for (IPageChange iPageChangeCallback : iPageChanges) {
                            iPageChangeCallback.onPageSelect(pos2Index(currentIndex), true);
                        }
                    }
                }
                return targetPos;
            }
        };
        snapHelper.attachToRecyclerView(this);
    }

    /**
     * adapter实现回调
     *
     * @param adapterCallback
     */
    public void setAdapter(ICreateAdapter adapterCallback) {
        iCreateAdapter = adapterCallback;
    }

    public int setDates(Collection<? extends IBannerEntity> datas) {
        setPlaying(false);
        this.dates.clear();
        if (datas != null) {
            this.dates.addAll(datas);
        }
        if (!this.dates.isEmpty()) {
            currentIndex = this.dates.size() * 10000;
            adapter.notifyDataSetChanged();
            if (null != iPageDataChange) {
                iPageDataChange.onPageDataChange(this.dates.size(), currentIndex);
            }
            scrollToPosition(currentIndex);
            setPlaying(true);
        } else {
            currentIndex = 0;
            adapter.notifyDataSetChanged();
            if (null != iPageDataChange) {
                iPageDataChange.onPageDataChange(this.dates.size(), currentIndex);
            }
        }
        return this.dates.size();
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
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setPlaying(false);
    }

    // 内置适配器
    private abstract class BaseBannerAdapter extends RecyclerView.Adapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return createHold(parent, viewType);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            holder.itemView.setOnClickListener(v -> {
                if (iPageClick != null) {
                    iPageClick.onClick(holder.getAdapterPosition(),
                            dates.get(currentIndex % dates.size()),
                            holder.itemView);
                }
            });
            bindHold(holder, position);
        }

        @Override
        public int getItemCount() {
            return dates.isEmpty() ? 0 : isLoop() ? Integer.MAX_VALUE : dates.size();
        }

        public abstract RecyclerView.ViewHolder createHold(ViewGroup parent, int viewType);

        public abstract void bindHold(RecyclerView.ViewHolder holder, int position);
    }

    public void setCurrentIndex(int index) {
        mUIHandler.postDelayed(() -> {
            if (null != dates && !dates.isEmpty()) {
                currentIndex = dates.size() * 10000 + index;
                smoothScrollToPosition(currentIndex);
            }
        }, 100);
    }

    public int getCurrentIndex() {
        return pos2Index(currentIndex);
    }

    public int pos2Index(int pos) {
        if (null == dates || dates.isEmpty()) {
            return 0;
        } else {
            return pos % dates.size();
        }
    }

    /**
     * 页面切换监听
     *
     * @param iPageChange
     */
    public void addPageChangeCallback(@NonNull IPageChange iPageChange) {
        if (null == iPageChanges) {
            iPageChanges = new ArrayList<>();
        }
        this.iPageChanges.add(iPageChange);
    }

    public void removePageChangeCallback(@NonNull IPageChange iPageChange) {
        if (null == iPageChanges) {
            return;
        }
        this.iPageChanges.remove(iPageChange);
    }

    public void clearPageChangeCallback() {
        if (null == iPageChanges) {
            return;
        }
        this.iPageChanges.clear();
    }

    /**
     * 页面点击回调
     *
     * @param iPageClick
     */
    public void setPageClickCallback(IPageClick iPageClick) {
        this.iPageClick = iPageClick;
    }

    /**
     * 数据变更回调
     *
     * @param iPageDataChange
     */
    public void setPageDataChangeCallback(IPageDataChange iPageDataChange) {
        this.iPageDataChange = iPageDataChange;
    }

    /**
     * 间隔时间
     *
     * @param time
     */
    public void setIntervalTime(int time) {
        if (time >= 2000) {
            intervalTime = time;
        }
    }

    public List<IBannerEntity> getDates() {
        return dates;
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void setLoop(boolean canLoop) {
        this.isLoop = canLoop;
    }

    public boolean isAutoPlay() {
        return isAutoPlay;
    }

    public void setAutoPlay(boolean canAutoPlay) {
        this.isAutoPlay = canAutoPlay;
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
            if (currentIndex == Integer.MAX_VALUE) {
                currentIndex = dates.size() * 10000;
                scrollToPosition(currentIndex);
            } else {
                smoothScrollToPosition(++currentIndex);
            }
            if (intervalTime < 1000) {
                intervalTime = INTERVAL_TIME;
            }
            resetDelayed();
            if (null != iPageChanges && currentIndex >= 0) {
                for (IPageChange iPageChange : iPageChanges) {
                    if (null != iPageChange) {
                        iPageChange.onPageSelect(pos2Index(currentIndex), false);
                    }
                }
            }
        }
    };

    private synchronized void resetDelayed() {
        mUIHandler.removeCallbacks(playTask);
        if (isAutoPlay() && isPlaying()) {
            mUIHandler.postDelayed(playTask, intervalTime);
        }
    }
}