package com.leo.androidview.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;

import com.leo.androidview.R;
import com.leo.androidview.databinding.ActivityCalendarBinding;

import java.lang.ref.WeakReference;
import java.util.Calendar;

public class CalendarViewActivity extends BaseActivity {
    Calendar mInstance = Calendar.getInstance();
    private ActivityCalendarBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_calendar);
        mBinding.setEventHandler(new EventListener(this));
        mBinding.calendarView.setOnDataClickListener((index, date) -> {
            mInstance.setTime(date);
            Toast.makeText(CalendarViewActivity.this,
                    index + " - " + mInstance.get(Calendar.YEAR) + "/"
                            + (mInstance.get(Calendar.MONTH) + 1) + "/"
                            + mInstance.get(Calendar.DAY_OF_MONTH),
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

    /**
     * 上个月
     */
    private void preMonth() {
        mInstance.setTime(mBinding.calendarView.getTime());
        mInstance.add(Calendar.MONTH, -1);
        mBinding.calendarView.setTime(mInstance.getTime());
    }

    /**
     * 下个月
     */
    private void nextMonth() {
        mInstance.setTime(mBinding.calendarView.getTime());
        mInstance.add(Calendar.MONTH, 1);
        mBinding.calendarView.setTime(mInstance.getTime());
    }

    public static class EventListener {
        private final WeakReference<CalendarViewActivity> activityWR;

        public EventListener(CalendarViewActivity activity) {
            this.activityWR = new WeakReference<>(activity);
        }

        public void onClick(View v) {
            CalendarViewActivity activity = activityWR.get();
            if (null == activity) {
                return;
            }
            switch (v.getId()) {
                case R.id.pre:
                    activity.preMonth();
                    break;
                case R.id.next:
                    activity.nextMonth();
                    break;
                default:
                    break;
            }
        }
    }
}
