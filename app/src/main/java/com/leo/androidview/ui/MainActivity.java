package com.leo.androidview.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.leo.androidview.R;
import com.leo.androidview.databinding.ActivityMainBinding;
import com.leo.system.IntentUtil;

import java.lang.ref.WeakReference;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.setEventListener(new EventListener(this));
    }

    public static class EventListener {
        private final WeakReference<MainActivity> reference;

        public EventListener(MainActivity activity) {
            this.reference = new WeakReference<>(activity);
        }

        @SuppressLint("NonConstantResourceId")
        public void onClick(View v) {
            MainActivity activity = reference.get();
            if (null == activity) {
                return;
            }
            switch (v.getId()) {
                case R.id.weiboBtn:
                    IntentUtil.INSTANCE.startActivity(activity, WeiboViewActivity.class);
                    break;
                case R.id.imageBtn:
                    IntentUtil.INSTANCE.startActivity(activity, ImageActivity.class);
                    break;
                case R.id.bannerBtn:
                    IntentUtil.INSTANCE.startActivity(activity, BannerActivity.class);
                    break;
                case R.id.repeatLayoutBtn:
                    IntentUtil.INSTANCE.startActivity(activity, RepeatManagerActivity.class);
                    break;
                case R.id.calendarViewBtn:
                    IntentUtil.INSTANCE.startActivity(activity, CalendarViewActivity.class);
                    break;
                default:
                    break;
            }
        }
    }
}
