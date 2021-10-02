package com.leo.androidview.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;

import com.leo.androidview.R;
import com.leo.androidview.databinding.ActivityCalendarBinding;

import java.util.Calendar;

public class CalendarViewActivity extends BaseActivity {
    private ActivityCalendarBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_calendar);
        mBinding.calendarView.setOnDataClickListener((index, date) -> {
            Calendar instance = Calendar.getInstance();
            instance.setTime(date);

            Toast.makeText(CalendarViewActivity.this,
                    index + " - " + instance.get(Calendar.YEAR) + "/"
                            + (instance.get(Calendar.MONTH) + 1) + "/"
                            + instance.get(Calendar.DAY_OF_MONTH),
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void initActionBar() {
        super.initActionBar();
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("日历控件");
        }
    }
}
