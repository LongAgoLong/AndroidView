package com.leo.calendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.leo.calendarview.callback.OnDataClickListener;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by LEO
 * on 2017/4/29.
 * 签到日历
 */
public class CalendarView extends View implements View.OnTouchListener {
    private static final String TAG = CalendarView.class.getSimpleName();

    private Context context;

    /**
     * 手指按下状态时临时日期
     */
    private Date downDate;
    /**
     * 按下的格子索引
     */
    private int downIndex;
    /**
     * 当前日历显示的年月数据，修改月份改变该值
     */
    private Date time;
    /**
     * 今天的日期
     */
    private Date today;
    /**
     * 日历显示的第一个日期
     */
    private Date showFirstDate;
    /**
     * 日历显示的最后一个日期
     */
    private Date showLastDate;
    private Calendar mCalendar;
    /**
     * 日历显示数字
     */
    private final int[] date = new int[42];
    /**
     * 当前显示的日历起始的索引
     */
    private int curStartIndex, curEndIndex;

    private Parameter parameter;
    private int todayIndex;

    private OnDataClickListener onDataClickListener;

    public CalendarView(Context context) {
        this(context, null, 0);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        mCalendar = Calendar.getInstance();

        // 初始化属性
        parameter = new Parameter();
        if (attrs != null) {
            TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CalendarView, 0, 0);
            parameter.isWithLine = attributes.getBoolean(R.styleable.CalendarView_calendar_isWithLine, false);
            parameter.lineSize = attributes.getDimensionPixelSize(R.styleable.CalendarView_calendar_lineSize, dip2px(context, 1));
            parameter.lineColor = attributes.getColor(R.styleable.CalendarView_calendar_lineColor, 0xff000000);

            parameter.whRatio = attributes.getFloat(R.styleable.CalendarView_calendar_squareWidthHeightRatio, 1.0f);
            parameter.cellCorner = attributes.getDimensionPixelSize(R.styleable.CalendarView_calendar_cellCorner, 0);
            parameter.itemGap = attributes.getDimensionPixelSize(R.styleable.CalendarView_calendar_cellDividerSize, 0);

            parameter.weekTextSize = attributes.getDimensionPixelSize(R.styleable.CalendarView_calendar_weekTextSize, dip2px(context, 14));
            parameter.dayTextSize = attributes.getDimensionPixelSize(R.styleable.CalendarView_calendar_dayTextSize, dip2px(context, 24));
            parameter.monthTextSize = attributes.getDimensionPixelSize(R.styleable.CalendarView_calendar_monthTextSize, dip2px(context, 12));

            parameter.textColorOtherMonth = attributes.getColor(R.styleable.CalendarView_calendar_textColor_otherMonth, Color.BLACK);
            parameter.textColorCurrentMonth = attributes.getColor(R.styleable.CalendarView_calendar_textColor_currentMonth, Color.BLACK);
            parameter.textColorCurrentDay = attributes.getColor(R.styleable.CalendarView_calendar_textColor_currentDay, Color.BLACK);

            parameter.textColorTabWeekend = attributes.getColor(R.styleable.CalendarView_calendar_textColor_tab_weekend, Color.BLACK);
            parameter.textColorTabWorkday = attributes.getColor(R.styleable.CalendarView_calendar_textColor_tab_workday, Color.BLACK);

            parameter.bgColorCurrentMonth = attributes.getColor(R.styleable.CalendarView_calendar_bgColor_currentMonthCell, 0x00ffffff);
            parameter.bgColorLastMonth = attributes.getColor(R.styleable.CalendarView_calendar_bgColor_lastMonthCell, 0x00ffffff);
            parameter.bgColorNextMonth = attributes.getColor(R.styleable.CalendarView_calendar_bgColor_nextMonthCell, 0x00ffffff);
            parameter.bgColorCurrentDay = attributes.getColor(R.styleable.CalendarView_calendar_bgColor_currentDayCell, 0x00ffffff);
            parameter.bgColorTab = attributes.getColor(R.styleable.CalendarView_calendar_bgColor_tab, Color.WHITE);
            parameter.bgColorTabIcon = attributes.getColor(R.styleable.CalendarView_calendar_bgColor_tab_icon, Color.WHITE);

            attributes.recycle();
        }
        parameter.initPaint();

        /*初始化当前日期*/
        time = today = new Date();
        mCalendar.setTime(time);
        setOnTouchListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed && null != parameter && parameter.isWithLine) {
            parameter.initBoxPath();
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // 画框
        if (parameter.isWithLine) {
            canvas.drawPath(parameter.boxPath, parameter.borderPaint);
        }
        // 星期
        int halfItemSpace = parameter.itemGap / 2;
        drawTab(canvas, halfItemSpace);

        // 计算日期
        calculateDate();
        // write date number
        // today index
        todayIndex = -1;
        // 当前显示年份/月份
        mCalendar.setTime(time);
        int currentYear = mCalendar.get(Calendar.YEAR);
        int currentMonth = mCalendar.get(Calendar.MONTH) + 1;
        String curYearAndMonth = currentYear + "" + currentMonth;
        mCalendar.setTime(today);
        String todayYearAndMonth = mCalendar.get(Calendar.YEAR) + ""
                + (mCalendar.get(Calendar.MONTH) + 1);
        if (curYearAndMonth.equals(todayYearAndMonth)) {
            int todayNumber = mCalendar.get(Calendar.DAY_OF_MONTH);
            todayIndex = curStartIndex + todayNumber - 1;
        }

        for (int i = 0; i < 42; i++) {
            drawCell(canvas, parameter.rect, i, date[i] + "", i == todayIndex,
                    currentYear, currentMonth);
        }
        super.onDraw(canvas);
    }

    /**
     * 设置显示月份
     *
     * @param monthDate
     */
    public void setTime(Date monthDate) {
        this.time = monthDate;
        postInvalidate();
    }

    public Date getTime() {
        return time;
    }

    private boolean isLastMonth(int i) {
        return i < curStartIndex;
    }

    private boolean isNextMonth(int i) {
        return i >= curEndIndex;
    }

    private int getXByIndex(int i) {
        return i % 7 + 1; // 1 2 3 4 5 6 7
    }

    private int getYByIndex(int i) {
        return i / 7 + 1; // 1 2 3 4 5 6
    }

    private void drawTab(Canvas canvas, int halfItemSpace) {
        parameter.path.reset();
        parameter.path.moveTo(halfItemSpace, parameter.cellHeight - halfItemSpace);
        parameter.path.lineTo(parameter.width - halfItemSpace, parameter.cellHeight - halfItemSpace);
        parameter.path.lineTo(parameter.width - halfItemSpace, 30);
        parameter.rectF.set(parameter.width - halfItemSpace - 30 * 2, 0, parameter.width - halfItemSpace, 30 * 2);
        parameter.path.arcTo(parameter.rectF, 0, -90);
        parameter.path.lineTo(30, 0);
        parameter.rectF.set(halfItemSpace, 0, halfItemSpace + 30 * 2, 30 * 2);
        parameter.path.arcTo(parameter.rectF, -90, -90);
        parameter.path.close();
        parameter.datePaint.setColor(parameter.bgColorTab);
        canvas.drawPath(parameter.path, parameter.datePaint);

        parameter.datePaint.setColor(parameter.bgColorTabIcon);
        int radius = parameter.dayTextSize;
        for (int i = 0; i < parameter.weekText.length; i++) {
            parameter.rect.set((int) (i * parameter.cellWidth), 0, (int) ((i + 1) * parameter.cellWidth), (int) parameter.cellHeight);
            Paint.FontMetricsInt fontMetrics = parameter.weekPaint.getFontMetricsInt();
            int baseline = (parameter.rect.bottom + parameter.rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            if (i == 0 || i == parameter.weekText.length - 1) {
                parameter.weekPaint.setColor(parameter.textColorTabWeekend);
            } else {
                parameter.weekPaint.setColor(parameter.textColorTabWorkday);
            }
            parameter.rectF.set(parameter.rect.centerX() - radius, parameter.rect.centerY() - radius, parameter.rect.centerX() + radius, parameter.rect.centerY() + radius);
            canvas.drawArc(parameter.rectF, 0, 360, true, parameter.datePaint);
            canvas.drawText(parameter.weekText[i], parameter.rect.centerX(), baseline, parameter.weekPaint);
        }
    }

    /**
     * @param canvas
     * @param index
     * @param text
     */
    private void drawCell(Canvas canvas, Rect targetRect, int index, String text, boolean isCurrentDay,
                          int currentYear, int currentMonth) {
        int x = getXByIndex(index);
        int y = getYByIndex(index);

        targetRect.top = (int) (parameter.cellHeight + (y - 1) * parameter.cellHeight);
        targetRect.bottom = (int) (parameter.cellHeight + y * parameter.cellHeight);
        targetRect.left = (int) (parameter.cellWidth * (x - 1));
        targetRect.right = (int) (parameter.cellWidth * x);

        int halfItemSpace = parameter.itemGap / 2;
        parameter.rectF.set(targetRect.left + halfItemSpace, targetRect.top + halfItemSpace,
                targetRect.right - halfItemSpace, targetRect.bottom - halfItemSpace);

        if (isLastMonth(index)) {
            // 上个月
            int color = parameter.textColorOtherMonth;
            int month;
            if (currentMonth == 1) {
                month = 12;
            } else {
                month = currentMonth - 1;
            }
            parameter.datePaint.setColor(parameter.bgColorLastMonth);
            canvas.drawRoundRect(parameter.rectF, parameter.cellCorner, parameter.cellCorner, parameter.datePaint);
            drawCellText(canvas, targetRect, halfItemSpace, color, month, text);
        } else if (isNextMonth(index)) {
            // 下个月
            int color = parameter.textColorOtherMonth;
            int month;
            if (currentMonth == 12) {
                month = 1;
            } else {
                month = currentMonth + 1;
            }
            parameter.datePaint.setColor(parameter.bgColorNextMonth);
            canvas.drawRoundRect(parameter.rectF, parameter.cellCorner, parameter.cellCorner, parameter.datePaint);
            drawCellText(canvas, targetRect, halfItemSpace, color, month, text);
        } else {
            // 本月
            int color = isCurrentDay ? parameter.textColorCurrentDay : parameter.textColorCurrentMonth;
            parameter.datePaint.setColor(isCurrentDay ? parameter.bgColorCurrentDay : parameter.bgColorCurrentMonth);
            canvas.drawRoundRect(parameter.rectF, parameter.cellCorner, parameter.cellCorner, parameter.datePaint);
            drawCellText(canvas, targetRect, halfItemSpace, color, currentMonth, text);
        }
    }

    private void drawCellText(Canvas canvas, Rect targetRect, int halfItemSpace, int color,
                              int month, String text) {
        parameter.datePaint.setColor(color);
        parameter.datePaint.setTextSize(parameter.dayTextSize);
        parameter.datePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        parameter.rect.set(targetRect.left + halfItemSpace, targetRect.centerY() - parameter.dayTextSize + 15, targetRect.right - halfItemSpace, targetRect.centerY() + 15);
        Paint.FontMetricsInt fontMetrics = parameter.datePaint.getFontMetricsInt();
        int baseline = (parameter.rect.bottom + parameter.rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(text, parameter.rect.centerX(), baseline, parameter.datePaint);

        int bottom = parameter.rect.bottom;
        parameter.datePaint.setTextSize(parameter.monthTextSize);
        parameter.datePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        parameter.rect.top = bottom + 5;
        parameter.rect.bottom = parameter.rect.top + parameter.monthTextSize;
        Paint.FontMetricsInt fontMetrics1 = parameter.datePaint.getFontMetricsInt();
        int baseline1 = (parameter.rect.bottom + parameter.rect.top - fontMetrics1.bottom - fontMetrics1.top) / 2;
        canvas.drawText(month + "月", parameter.rect.centerX(), baseline1, parameter.datePaint);
    }

    private void calculateDate() {
        mCalendar.setTime(time);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthStart = mCalendar.get(Calendar.DAY_OF_WEEK);
        if (monthStart == 1) {
            monthStart = 8;
        }
        monthStart -= 1;  // 以日为开头-1，以星期一为开头-2
        if (monthStart < 3) {// 为上个月最后3天留出位置
            monthStart += 7;
        }
        curStartIndex = monthStart;
        date[monthStart] = 1;
        // last month
        if (monthStart > 0) {
            mCalendar.set(Calendar.DAY_OF_MONTH, 0);
            int dayInmonth = mCalendar.get(Calendar.DAY_OF_MONTH);
            for (int i = monthStart - 1; i >= 0; i--) {
                date[i] = dayInmonth;
                dayInmonth--;
            }
            mCalendar.set(Calendar.DAY_OF_MONTH, date[0]);
        }
        showFirstDate = mCalendar.getTime();
        // this month
        mCalendar.setTime(time);
        mCalendar.add(Calendar.MONTH, 1);
        mCalendar.set(Calendar.DAY_OF_MONTH, 0);
        int monthDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        for (int i = 1; i < monthDay; i++) {
            date[monthStart + i] = i + 1;
        }
        curEndIndex = monthStart + monthDay;
        // next month
        for (int i = monthStart + monthDay; i < 42; i++) {
            date[i] = i - (monthStart + monthDay) + 1;
        }
        if (curEndIndex < 42) {
            // 显示了下一月的
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        mCalendar.set(Calendar.DAY_OF_MONTH, date[41]);
        showLastDate = mCalendar.getTime();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        parameter.width = getExpectSize(getScreenWidth(getContext()), widthMeasureSpec);
        parameter.cellWidth = (float) parameter.width / 7;
        float height = parameter.cellWidth / parameter.whRatio;
        int i = parameter.monthTextSize + parameter.dayTextSize + parameter.itemGap * 7;
        parameter.cellHeight = Math.max(height, i);
        parameter.height = getExpectSize((int) (parameter.cellHeight * 7), heightMeasureSpec);
        setMeasuredDimension(parameter.width, parameter.height);
    }

    private int getExpectSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(size, specSize);
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 属性设置
     */
    private class Parameter {
        public String[] weekText;
        private int width;
        private int height;

        private boolean isWithLine;
        private int lineSize;
        private int lineColor;
        private float whRatio;

        /**
         * tab-周末文本颜色
         */
        private int textColorTabWeekend;
        /**
         * tab-工作日文本颜色
         */
        private int textColorTabWorkday;

        /**
         * 其他月份文本颜色
         */
        private int textColorOtherMonth;
        /**
         * 当前月份文本颜色
         */
        private int textColorCurrentMonth;
        /**
         * 当前日期文本颜色
         */
        private int textColorCurrentDay;
        /**
         * 上一个月日期背景颜色
         */
        private int bgColorLastMonth;
        /**
         * 下一个月日期背景颜色
         */
        private int bgColorNextMonth;
        /**
         * 当前月份日期背景颜色
         */
        private int bgColorCurrentMonth;
        /**
         * 今天日期背景颜色
         */
        private int bgColorCurrentDay;
        /**
         * tab栏背景色
         */
        private int bgColorTab;
        /**
         * tab每一项背景色
         */
        private int bgColorTabIcon;

        /**
         * tab栏周几文本字体大小
         */
        private int weekTextSize;
        /**
         * 日期文本字体大小
         */
        private int dayTextSize;
        /**
         * 月份文本字体大小
         */
        private int monthTextSize;

        /**
         * 日期方框宽度
         */
        private float cellWidth;
        /**
         * 日期方框高度
         */
        private float cellHeight;
        /**
         * 边框路径
         */
        public Path boxPath;

        public Rect rect;
        public RectF rectF;
        public Path path;
        /**
         * 间隔
         */
        public int itemGap;

        public int cellCorner;

        public Parameter() {
            weekText = context.getResources().getStringArray(R.array.calendar_tab_week);

            rect = new Rect(0, 0, 0, 0);
            rectF = new RectF(0, 0, 0, 0);
            path = new Path();
        }

        private Paint borderPaint;
        private Paint weekPaint;
        private Paint datePaint;

        public void initPaint() {
            borderPaint = new Paint();
            borderPaint.setColor(lineColor);
            borderPaint.setAntiAlias(true);
            borderPaint.setStyle(Paint.Style.STROKE);
            lineSize = Math.max(lineSize, 1);
            borderPaint.setStrokeWidth(lineSize);

            weekPaint = new Paint();
            weekPaint.setTextAlign(Paint.Align.CENTER);
            weekPaint.setAntiAlias(true);
            weekPaint.setTextSize(weekTextSize);
            weekPaint.setTypeface(Typeface.DEFAULT_BOLD);

            datePaint = new Paint();
            datePaint.setTextAlign(Paint.Align.CENTER);
            datePaint.setAntiAlias(true);
            datePaint.setTextSize(dayTextSize);
            datePaint.setTypeface(Typeface.DEFAULT);
        }

        public void initBoxPath() {
            if (boxPath == null) {
                boxPath = new Path();
            } else {
                boxPath.reset();
            }
            boxPath.rLineTo(width, 0);
            boxPath.moveTo(0, cellHeight);
            boxPath.rLineTo(width, 0);
            for (int i = 1; i < 6; i++) {
                boxPath.moveTo(0, cellHeight + i * cellHeight);
                boxPath.rLineTo(width, 0);
                boxPath.moveTo(i * cellWidth, cellHeight);
                boxPath.rLineTo(0, height - cellHeight);
            }
            boxPath.moveTo(6 * cellWidth, cellHeight);
            boxPath.rLineTo(0, height - cellHeight);
        }
    }

    private void setSelectedDateByCoor(float x, float y) {
        if (y > parameter.cellHeight) {
            int m = (int) (Math.floor(x / parameter.cellWidth) + 1);
            int n = (int) (Math.floor((y - (parameter.cellHeight)) / parameter.cellHeight) + 1);
            downIndex = (n - 1) * 7 + m - 1;

            mCalendar.setTime(time);
            if (isLastMonth(downIndex)) {
                mCalendar.add(Calendar.MONTH, -1);
            } else if (isNextMonth(downIndex)) {
                mCalendar.add(Calendar.MONTH, 1);
            }
            mCalendar.set(Calendar.DAY_OF_MONTH, date[downIndex]);
            downDate = mCalendar.getTime();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setSelectedDateByCoor(motionEvent.getX(), motionEvent.getY());
                return true;
            case MotionEvent.ACTION_UP:
                if (downDate != null) {
                    // 响应监听事件
                    if (null != onDataClickListener) {
                        onDataClickListener.OnItemClick(downIndex, downDate);
                    }
                    downDate = null;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
                return false;
        }
        return false;
    }

    /**
     * 设置日期点击事件监听
     *
     * @param onDataClickListener
     */
    public void setOnDataClickListener(OnDataClickListener onDataClickListener) {
        this.onDataClickListener = onDataClickListener;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 得到设备屏幕的宽度
     */
    private int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return Math.min(dm.heightPixels, dm.widthPixels);
    }
}
