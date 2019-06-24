package com.leo.recyclerbanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.leo.recyclerbanner.callback.IBannerEntity;
import com.leo.recyclerbanner.callback.ICreateAdapterCallback;
import com.leo.recyclerbanner.callback.IPageChangeCallback;
import com.leo.recyclerbanner.callback.IPageClickCallback;
import com.leo.recyclerbanner.callback.IPageDataChangeCallback;

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

    private ArrayList<IPageChangeCallback> iPageChangeCallbacks;
    private ICreateAdapterCallback iCreateAdapterCallback;
    private IPageClickCallback iPageClickCallback;
    private IPageDataChangeCallback iPageDataChangeCallback;

    private BaseBannerAdapter adapter;
    private List<IBannerEntity> datas = new ArrayList<>();

    private int currentIndex;
    private int startX, startY;

    private int intervalTime = INTERVAL_TIME;
    private boolean isLoop;
    private boolean isAutoPlay;
    private boolean isPlaying;

    private Handler handler = new Handler();
    private Runnable playTask = new Runnable() {
        @Override
        public void run() {
            if (currentIndex == Integer.MAX_VALUE) {
                currentIndex = datas.size() * 10000;
                scrollToPosition(currentIndex);
            } else {
                smoothScrollToPosition(++currentIndex);
            }
            if (intervalTime < 1000) {
                intervalTime = INTERVAL_TIME;
            }
            resetDelayed();
            if (null != iPageChangeCallbacks) {
                for (IPageChangeCallback iPageChangeCallback : iPageChangeCallbacks) {
                    if (null != iPageChangeCallback) {
                        iPageChangeCallback.onPageSelect(getCurrentIndex(), false);
                    }
                }
            }
        }
    };

    private synchronized void resetDelayed(){
        handler.removeCallbacks(playTask);
        handler.postDelayed(playTask, intervalTime);
    }

    public RecyclerBanner(Context context) {
        this(context, null);
        initAttrs(context, null, 0);
        init();
    }

    public RecyclerBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initAttrs(context, attrs, 0);
        init();
    }

    public RecyclerBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
        init();
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
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

    private void init() {
        adapter = new BaseBannerAdapter() {
            @Override
            public RecyclerView.ViewHolder createHold(ViewGroup parent, int viewType) {
                if (null != iCreateAdapterCallback) {
                    return iCreateAdapterCallback.createHold(parent, viewType);
                }
                return null;
            }

            @Override
            public void bindHold(RecyclerView.ViewHolder holder, int position) {
                if (null != iCreateAdapterCallback) {
                    iCreateAdapterCallback.bindHold(holder, position);
                }
            }
        };
        setAdapter(adapter);
        new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                int targetPos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
                currentIndex = targetPos;
                if (null != iPageChangeCallbacks) {
                    for (IPageChangeCallback iPageChangeCallback : iPageChangeCallbacks) {
                        if (null != iPageChangeCallback) {
                            iPageChangeCallback.onPageSelect(getCurrentIndex(), true);
                        }
                    }
                }
                return targetPos;
            }
        }.attachToRecyclerView(this);
    }

    public void setAdapter(ICreateAdapterCallback adapterCallback) {
        iCreateAdapterCallback = adapterCallback;
    }

    public synchronized void setPlaying(boolean playing) {
        if (playing) {
            if (isAutoPlay()
                    && !isPlaying()
                    && null != adapter
                    && adapter.getItemCount() > 2) {
                isPlaying = true;
                resetDelayed();
            }
        } else {
            if (isPlaying()) {
                handler.removeCallbacks(playTask);
                isPlaying = false;
            }
        }
    }

    public int setDatas(Collection<? extends IBannerEntity> datas) {
        setPlaying(false);
        this.datas.clear();
        if (datas != null) {
            this.datas.addAll(datas);
        }
        if (!this.datas.isEmpty()) {
            currentIndex = this.datas.size() * 10000;
            adapter.notifyDataSetChanged();
            if (null != iPageDataChangeCallback) {
                iPageDataChangeCallback.onPageDataChange(this.datas.size(), currentIndex);
            }
            scrollToPosition(currentIndex);
            setPlaying(true);
        } else {
            currentIndex = 0;
            adapter.notifyDataSetChanged();
            if (null != iPageDataChangeCallback) {
                iPageDataChangeCallback.onPageDataChange(this.datas.size(), currentIndex);
            }
        }
        return this.datas.size();
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
                if (iPageClickCallback != null) {
                    iPageClickCallback.onClick(holder.getLayoutPosition(), datas.get(currentIndex % datas.size()));
                }
            });
            bindHold(holder, position);
        }

        @Override
        public int getItemCount() {
            return (datas == null || datas.isEmpty()) ? 0 : isLoop() ? Integer.MAX_VALUE : datas.size();
        }

        public abstract RecyclerView.ViewHolder createHold(ViewGroup parent, int viewType);

        public abstract void bindHold(RecyclerView.ViewHolder holder, int position);
    }

    public void setCurrentIndex(int index) {
        handler.postDelayed(() -> {
            if (null != datas && !datas.isEmpty()) {
                currentIndex = datas.size() * 10000 + index;
                smoothScrollToPosition(currentIndex);
            }
        }, 100);
    }

    public int getCurrentIndex() {
        if (null == datas || datas.isEmpty()) {
            return 0;
        } else {
            return currentIndex % datas.size();
        }
    }

    /**
     * 页面切换监听
     *
     * @param iPageChangeCallback
     */
    public void addIPageChangeCallback(@NonNull IPageChangeCallback iPageChangeCallback) {
        if (null == iPageChangeCallbacks) {
            iPageChangeCallbacks = new ArrayList<>();
        }
        this.iPageChangeCallbacks.add(iPageChangeCallback);
    }

    public void removeIPageChangeCallback(@NonNull IPageChangeCallback iPageChangeCallback) {
        if (null == iPageChangeCallbacks) {
            return;
        }
        this.iPageChangeCallbacks.remove(iPageChangeCallback);
    }

    public void clearIPageChangeCallback() {
        if (null == iPageChangeCallbacks) {
            return;
        }
        this.iPageChangeCallbacks.clear();
    }

    /**
     * adapter实现回调
     *
     * @param iCreateAdapterCallback
     */
    public void setICreateAdapterCallback(ICreateAdapterCallback iCreateAdapterCallback) {
        this.iCreateAdapterCallback = iCreateAdapterCallback;
    }

    /**
     * 页面点击回调
     *
     * @param iPageClickCallback
     */
    public void setIPageClickCallback(IPageClickCallback iPageClickCallback) {
        this.iPageClickCallback = iPageClickCallback;
    }

    /**
     * 数据变更回调
     *
     * @param iPageDataChangeCallback
     */
    public void setiPageDataChangeCallback(IPageDataChangeCallback iPageDataChangeCallback) {
        this.iPageDataChangeCallback = iPageDataChangeCallback;
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

    public List<IBannerEntity> getDatas() {
        return datas;
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
}