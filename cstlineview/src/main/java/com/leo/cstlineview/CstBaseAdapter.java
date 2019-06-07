package com.leo.cstlineview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.LayoutRes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LEO
 * On 2019/6/7
 * Description:adapter基类
 */
public abstract class CstBaseAdapter<T> extends BaseAdapter {
    protected List<T> list;
    protected List<View> viewList;
    protected Context context;
    protected int maxSize;

    public CstBaseAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
        maxSize = -1;
        viewList = new ArrayList<>();
    }

    /**
     * 设置最大限制数量
     *
     * @param maxSize
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * 复用view
     *
     * @param layoutId
     * @param parent
     * @param index
     * @return
     */
    protected View getCacheView(@LayoutRes int layoutId, ViewGroup parent, int index) {
        if (index < viewList.size()) {
            return viewList.get(index);
        } else {
            View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
            viewList.add(view);
            return view;
        }
    }

    public int getCount() {
        if (list != null) {
            if (maxSize != -1 && list.size() > maxSize) {
                return maxSize;
            } else {
                return list.size();
            }
        }
        return 0;
    }

    public T getItem(int position) {
        if (list != null) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
