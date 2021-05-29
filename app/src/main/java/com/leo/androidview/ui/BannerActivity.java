package com.leo.androidview.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leo.androidview.R;
import com.leo.androidview.adapter.BannerAdapter;
import com.leo.androidview.entity.BannerData;
import com.leo.recyclerbanner.XBanner;
import com.leo.recyclerbanner.XBannerAdapter;
import com.leo.recyclerbanner.callback.IBannerData;
import com.leo.recyclerbanner.callback.IPageClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by LEO
 * On 2019/6/24
 * Description:轮播图
 */
public class BannerActivity extends BaseActivity {
    private TextView mResultTv;
    private XBanner mBanner;
    private ArrayList<BannerData> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        initData();
        initView();
    }

    private void initData() {
        Random random = new Random();
        list = new ArrayList<BannerData>() {{
            for (int i = 0; i < 10; i++) {
                add(new BannerData(String.valueOf(random.nextInt())));
            }
        }};
    }

    private void initView() {
        mResultTv = findViewById(R.id.resultTv);

        mBanner = findViewById(R.id.banner);
        BannerAdapter adapter = new BannerAdapter();
        mBanner.setIntervalMilli(3000);
        mBanner.setXBannerAdapter(adapter);
        adapter.updateData(list);
        adapter.setPageClickListener((position, entity, view) -> {
            mResultTv.setText(String.format("click-currentIndex:%d", position));
        });
        mBanner.addPageSelectedListener((realPos, isUserTouch) -> mResultTv.setText(String.format("currentIndex:%d", realPos)));
    }

    @Override
    protected void initActionBar() {
        super.initActionBar();
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Banner控件");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
