package com.leo.androidview.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.leo.androidview.R;
import com.leo.androidview.adapter.BeautyAdapter;
import com.leo.androidview.databinding.ActivityRepeatManagerBinding;
import com.leo.recyclerbanner.layoutmanager.RepeatLayoutManager;

public class RepeatManagerActivity extends BaseActivity {

    private ActivityRepeatManagerBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_repeat_manager);
        mBinding.recyclerView.setLayoutManager(new RepeatLayoutManager(this, RecyclerView.HORIZONTAL));
        mBinding.recyclerView.setAdapter(new BeautyAdapter());
        new PagerSnapHelper().attachToRecyclerView(mBinding.recyclerView);
    }

    @Override
    protected void initActionBar() {
        super.initActionBar();
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("RepeatLayoutManager");
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
