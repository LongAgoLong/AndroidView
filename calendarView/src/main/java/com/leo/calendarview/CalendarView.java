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
     * 当前日历显示的月
     */
    private Date curDate;
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
    private Calendar calendar;
    /**
     * 日历显示数字
     */
    private final int[] date = new int[42];
    /**
     * 当前显示的日历起始的索引
     */
    private int curStartIndex, curEndIndex;

    private Surface surface;
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

        // 初始化属性
        surface = new Surface();
        if (attrs != null) {
            TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CalendarView, 0, 0);
            surface.isWithLine = attributes.getBoolean(R.styleable.CalendarView_calendar_isWithLine, false);
            surface.lineSize = attributes.getDimensionPixelSize(R.styleable.CalendarView_calendar_lineSize, dip2px(context, 1));
            surface.lineColor = attributes.getColor(R.styleable.CalendarView_calendar_lineColor, 0xff000000);

            surface.whRatio = attributes.getFloat(R.styleable.CalendarView_calendar_squareWidhtHeightRatio, 1.0f);
            surface.cellCorner = attributes.getDimensionPixelSize(R.styleable.CalendarView_calendar_cellCorner, 0);
            surface.itemSpace = attributes.getDimensionPixelSize(R.styleable.CalendarView_calendar_cellDividerSize, 0);

            surface.textSize = attributes.getDimensionPixelSize(R.styleable.CalendarView_calendar_dayTextSize, dip2px(context, 14));
            surface.textColorOtherMonth = attributes.getColor(R.styleable.CalendarView_calendar_textColor_otherMonth, Color.BLACK);
            surface.textColorCurrentMonth = attributes.getColor(R.styleable.CalendarView_calendar_textColor_currentMonth, Color.BLACK);
            surface.textColorCurrentDay = attributes.getColor(R.styleable.CalendarView_calendar_textColor_currentDay, Color.BLACK);

            surface.textColorTabWeekend = attributes.getColor(R.styleable.CalendarView_calendar_textColor_tab_weekend, Color.BLACK);
            surface.textColorTabWorkday = attributes.getColor(R.styleable.CalendarView_calendar_textColor_tab_workday, Color.BLACK);

            surface.cellBgColorCurrentMonth = attributes.getColor(R.styleable.CalendarView_calendar_bgColor_currentMonthCell, 0x00ffffff);
            surface.cellBgColorLastMonth = attributes.getColor(R.styleable.CalendarView_calendar_bgColor_lastMonthCell, 0x00ffffff);
            surface.cellBgColorNextMonth = attributes.getColor(R.styleable.CalendarView_calendar_bgColor_nextMonthCell, 0x00ffffff);
            surface.cellBgColorCurrentDay = attributes.getColor(R.styleable.CalendarView_calendar_bgColor_currentDayCell, 0x00ffffff);
            surface.bgColorTab = attributes.getColor(R.styleable.CalendarView_calendar_bgColor_tab, Color.WHITE);
            surface.bgColorTabIcon = attributes.getColor(R.styleable.CalendarView_calendar_bgColor_tab_icon, Color.WHITE);

            attributes.recycle();
        }
        surface.initPaint();

        /*初始化当前日期*/
        curDate = today = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        setOnTouchListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed && null != surface && surface.isWithLine) {
            surface.initBoxPath();
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // 画框
        if (surface.isWithLine) {
            canvas.drawPath(surface.boxPath, surface.borderPaint);
        }
        // 星期
        int halfItemSpace = surface.itemSpace / 2;
        // 绘制tab
        drawTab(canvas, halfItemSpace);

        // 计算日期
        calculateDate();
        // write date number
        // today index
        todayIndex = -1;
        // 当前显示年份/月份
        calendar.setTime(curDate);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        String curYearAndMonth = currentYear + "" + currentMonth;
        calendar.setTime(today);
        String todayYearAndMonth = calendar.get(Calendar.YEAR) + ""
                + (calendar.get(Calendar.MONTH) + 1);
        if (curYearAndMonth.equals(todayYearAndMonth)) {
            int todayNumber = calendar.get(Calendar.DAY_OF_MONTH);
            todayIndex = curStartIndex + todayNumber - 1;
        }

        for (int i = 0; i < 42; i++) {
            drawCell(canvas, surface.rect, i, date[i] + "", i == todayIndex,
                    currentYear, currentMonth);
        }
        super.onDraw(canvas);
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
        surface.path.reset();
        surface.path.moveTo(halfItemSpace, surface.cellHeight - halfItemSpace);
        surface.path.lineTo(surface.width - halfItemSpace, surface.cellHeight - halfItemSpace);
        surface.path.lineTo(surface.width - halfItemSpace, 30);
        surface.rectF.set(surface.width - halfItemSpace - 30 * 2, 0, surface.width - halfItemSpace, 30 * 2);
        surface.path.arcTo(surface.rectF, 0, -90);
        surface.path.lineTo(30, 0);
        surface.rectF.set(halfItemSpace, 0, halfItemSpace + 30 * 2, 30 * 2);
        surface.path.arcTo(surface.rectF, -90, -90);
        surface.path.close();
        surface.datePaint.setColor(surface.bgColorTab);
        canvas.drawPath(surface.path, surface.datePaint);

        surface.datePaint.setColor(surface.bgColorTabIcon);
        int radius = surface.textSize;
        for (int i = 0; i < surface.weekText.length; i++) {
            surface.rect.set((int) (i * surface.cellWidth), 0, (int) ((i + 1) * surface.cellWidth), (int) surface.cellHeight);
            Paint.FontMetricsInt fontMetrics = surface.weekPaint.getFontMetricsInt();
            int baseline = (surface.rect.bottom + surface.rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            if (i == 0 || i == surface.weekText.length - 1) {
                surface.weekPaint.setColor(surface.textColorTabWeekend);
            } else {
                surface.weekPaint.setColor(surface.textColorTabWorkday);
            }
            surface.rectF.set(surface.rect.centerX() - radius, surface.rect.centerY() - radius, surface.rect.centerX() + radius, surface.rect.centerY() + radius);
            canvas.drawArc(surface.rectF, 0, 360, true, surface.datePaint);
            canvas.drawText(surface.weekText[i], surface.rect.centerX(), baseline, surface.weekPaint);
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

        targetRect.top = (int) (surface.cellHeight + (y - 1) * surface.cellHeight);
        targetRect.bottom = (int) (surface.cellHeight + y * surface.cellHeight);
        targetRect.left = (int) (surface.cellWidth * (x - 1));
        targetRect.right = (int) (surface.cellWidth * x);

        int halfItemSpace = surface.itemSpace / 2;
        surface.rectF.set(targetRect.left + halfItemSpace, targetRect.top + halfItemSpace,
                targetRect.right - halfItemSpace, targetRect.bottom - halfItemSpace);

        if (isLastMonth(index)) {
            // 上个月
            int color = surface.textColorOtherMonth;
            int month;
            if (currentMonth == 1) {
                month = 12;
            } else {
                month = currentMonth - 1;
            }
            surface.datePaint.setColor(surface.cellBgColorLastMonth);
            canvas.drawRoundRect(surface.rectF, surface.cellCorner, surface.cellCorner, surface.datePaint);
            drawCellText(canvas, targetRect, halfItemSpace, color, month, text);
        } else if (isNextMonth(index)) {
            // 下个月
            int color = surface.textColorOtherMonth;
            int month;
            if (currentMonth == 12) {
                month = 1;
            } else {
                month = currentMonth + 1;
            }
            surface.datePaint.setColor(surface.cellBgColorNextMonth);
            canvas.drawRoundRect(surface.rectF, surface.cellCorner, surface.cellCorner, surface.datePaint);
            drawCellText(canvas, targetRect, halfItemSpace, color, month, text);
        } else {
            // 本月
            int color = isCurrentDay ? surface.textColorCurrentDay : surface.textColorCurrentMonth;
            surface.datePaint.setColor(isCurrentDay ? surface.cellBgColorCurrentDay : surface.cellBgColorCurrentMonth);
            canvas.drawRoundRect(surface.rectF, surface.cellCorner, surface.cellCorner, surface.datePaint);
            drawCellText(canvas, targetRect, halfItemSpace, color, currentMonth, text);
        }
    }

    private void drawCellText(Canvas canvas, Rect targetRect, int halfItemSpace, int color,
                              int month, String text) {
        surface.datePaint.setColor(color);
        surface.datePaint.setTextSize(surface.daySize);
        surface.datePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        surface.rect1.set(targetRect.left + halfItemSpace, targetRect.centerY() - surface.daySize + 15, targetRect.right - halfItemSpace, targetRect.centerY() + 15);
        Paint.FontMetricsInt fontMetrics = surface.datePaint.getFontMetricsInt();
        int baseline = (surface.rect1.bottom + surface.rect1.top - fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(text, surface.rect1.centerX(), baseline, surface.datePaint);

        int bottom = surface.rect1.bottom;
        surface.datePaint.setTextSize(surface.monthSize);
        surface.datePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        surface.rect1.top = bottom + 5;
        surface.rect1.bottom = surface.rect1.top + surface.monthSize;
        Paint.FontMetricsInt fontMetrics1 = surface.datePaint.getFontMetricsInt();
        int baseline1 = (surface.rect1.bottom + surface.rect1.top - fontMetrics1.bottom - fontMetrics1.top) / 2;
        canvas.drawText(month + "月", surface.rect1.centerX(), baseline1, surface.datePaint);

    }

    private void calculateDate() {
        calendar.setTime(curDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int monthStart = dayInWeek;
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
            calendar.set(Calendar.DAY_OF_MONTH, 0);
            int dayInmonth = calendar.get(Calendar.DAY_OF_MONTH);
            for (int i = monthStart - 1; i >= 0; i--) {
                date[i] = dayInmonth;
                dayInmonth--;
            }
            calendar.set(Calendar.DAY_OF_MONTH, date[0]);
        }
        showFirstDate = calendar.getTime();
        // this month
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
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
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, date[41]);
        showLastDate = calendar.getTime();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        surface.width = getExpectSize(getScreenWidth(getContext()), widthMeasureSpec);
        surface.cellWidth = (float) surface.width / 7;
        float height = surface.cellWidth / surface.whRatio;
        int i = surface.monthSize + surface.daySize + surface.itemSpace * 7;
        surface.cellHeight = Math.max(height, i);
        surface.height = getExpectSize((int) (surface.cellHeight * 7), heightMeasureSpec);
        setMeasuredDimension(surface.width, surface.height);
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
    private class Surface {
        public String[] weekText;
        public String[] monthText = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        private int width;
        private int height;

        private boolean isWithLine;
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
        private int cellBgColorLastMonth;
        /**
         * 下一个月日期背景颜色
         */
        private int cellBgColorNextMonth;
        /**
         * 当前月份日期背景颜色
         */
        private int cellBgColorCurrentMonth;
        /**
         * 今天日期背景颜色
         */
        private int cellBgColorCurrentDay;
        /**
         * tab栏背景色
         */
        private int bgColorTab;
        /**
         * tab每一项背景色
         */
        private int bgColorTabIcon;

        private int textSize;
        private int lineSize;
        private int lineColor;

        private float cellWidth; // 日期方框宽度
        private float cellHeight; // 日期方框高度

        public Path boxPath; // 边框路径

        public Rect rect, rect1;
        public RectF rectF;
        public Path path;
        public int itemSpace;
        public int tipSize;
        public int monthSize;
        public int daySize;

        public int cellCorner;

        public Surface() {
            weekText = context.getResources().getStringArray(R.array.calendar_tab_week);

            rect = new Rect(0, 0, 0, 0);
            rect1 = new Rect(0, 0, 0, 0);
            rectF = new RectF(0, 0, 0, 0);
            path = new Path();
            tipSize = dip2px(getContext(), 8);
            monthSize = dip2px(getContext(), 12);
            daySize = dip2px(getContext(), 24);
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
            float weekTextSize = textSize;
            weekPaint.setTextSize(weekTextSize);
            weekPaint.setTypeface(Typeface.DEFAULT_BOLD);
            datePaint = new Paint();
            datePaint.setTextAlign(Paint.Align.CENTER);
            datePaint.setAntiAlias(true);
            datePaint.setTextSize(textSize);
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
        if (y > surface.cellHeight) {
            int m = (int) (Math.floor(x / surface.cellWidth) + 1);
            int n = (int) (Math.floor((y - (surface.cellHeight)) / surface.cellHeight) + 1);
            downIndex = (n - 1) * 7 + m - 1;

            calendar.setTime(curDate);
            if (isLastMonth(downIndex)) {
                calendar.add(Calendar.MONTH, -1);
            } else if (isNextMonth(downIndex)) {
                calendar.add(Calendar.MONTH, 1);
            }
            calendar.set(Calendar.DAY_OF_MONTH, date[downIndex]);
            downDate = calendar.getTime();
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
