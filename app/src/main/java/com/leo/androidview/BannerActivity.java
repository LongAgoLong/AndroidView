package com.leo.androidview;

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

import com.leo.androidview.entity.BannerEntity;
import com.leo.recyclerbanner.RecyclerBanner;
import com.leo.recyclerbanner.callback.IBannerEntity;
import com.leo.recyclerbanner.callback.ICreateAdapterCallback;
import com.leo.recyclerbanner.callback.IPageChangeCallback;
import com.leo.recyclerbanner.callback.IPageClickCallback;

import java.util.ArrayList;

/**
 * Created by LEO
 * On 2019/6/24
 * Description:轮播图
 */
public class BannerActivity extends BaseActivity {
    private TextView mResultTv;
    private RecyclerBanner mBanner;
    private ArrayList<BannerEntity> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        initData();
        initView();
    }

    private void initData() {
        list = new ArrayList<>();
        list.add(new BannerEntity());
        list.add(new BannerEntity());
        list.add(new BannerEntity());
    }

    private void initView() {
        mResultTv = findViewById(R.id.resultTv);

        mBanner = findViewById(R.id.banner);
        mBanner.init(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBanner.setAdapter(new ICreateAdapterCallback() {
            @Override
            public RecyclerView.ViewHolder createHold(ViewGroup parent, int viewType) {
                return new ImgHold(LayoutInflater.from(BannerActivity.this).inflate(R.layout.item_banner, parent, false));
            }

            @Override
            public void bindHold(RecyclerView.ViewHolder holder, int position) {
                if (holder instanceof ImgHold) {
                    ImgHold imgHold = (ImgHold) holder;
                    int i = mBanner.pos2Index(position);
                    switch (i) {
                        case 0:
                            imgHold.mImg.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            break;
                        case 1:
                            imgHold.mImg.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            break;
                        default:
                            imgHold.mImg.setBackgroundColor(getResources().getColor(R.color.color_13b5b1));
                            break;
                    }
                }
            }
        });
        mBanner.addIPageChangeCallback(new IPageChangeCallback() {
            @Override
            public void onPageSelect(int realPos, boolean isUserTouch) {
                mResultTv.setText("currentIndex:" + realPos);
            }
        });
        mBanner.setIPageClickCallback(new IPageClickCallback() {
            @Override
            public void onClick(int position, IBannerEntity entity) {

            }
        });
        mBanner.setDatas(list);
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

    private class ImgHold extends RecyclerView.ViewHolder {
        public ImageView mImg;

        public ImgHold(@NonNull View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
        }
    }
}
