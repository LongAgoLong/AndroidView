package com.leo.weibotext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.leo.weibotext.callback.IWBClickImpl;
import com.leo.weibotext.callback.WBImpl;
import com.leo.weibotext.enume.WBClickMode;
import com.leo.weibotext.enume.WBTextMode;
import com.leo.weibotext.span.CstClickableSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeiboTextView extends androidx.appcompat.widget.AppCompatTextView {
    @WBTextMode
    private int wbMode = WBTextMode.ALL;
    private int wbColor = Color.BLUE;
    private boolean wbCallClickEnable;
    private boolean wbTopicClickEnable;
    private String wbHtmlReplace;
    private WBImpl wbImpl = WBImpl.DEFAULT;
    private IWBClickImpl iWBClickImpl;

    public WeiboTextView(Context context) {
        super(context);
        initAttr(null, 0);
    }

    public WeiboTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs, 0);
    }

    public WeiboTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs, defStyleAttr);
    }

    private void initAttr(AttributeSet attrs, int defStyleAttr) {
        if (null != attrs) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WeiboTextView, defStyleAttr, 0);
            wbMode = a.getInt(R.styleable.WeiboTextView_wb_text_mode, WBTextMode.ALL);
            wbColor = a.getColor(R.styleable.WeiboTextView_wb_text_color, Color.BLUE);
            wbCallClickEnable = a.getBoolean(R.styleable.WeiboTextView_wb_text_call_click_enable, false);
            wbTopicClickEnable = a.getBoolean(R.styleable.WeiboTextView_wb_text_topic_click_enable, false);
            wbHtmlReplace = a.getString(R.styleable.WeiboTextView_wb_text_html_replace);
            a.recycle();
        }
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                TextView tv = (TextView) v;
                CharSequence text = tv.getText();
                if (text instanceof SpannableString) {
                    if (action == MotionEvent.ACTION_UP) {
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        x -= tv.getTotalPaddingLeft();
                        y -= tv.getTotalPaddingTop();
                        x += tv.getScrollX();
                        y += tv.getScrollY();
                        Layout layout = tv.getLayout();
                        int line = layout.getLineForVertical(y);
                        int off = layout.getOffsetForHorizontal(line, x);
                        ClickableSpan[] link = ((SpannableString) text).getSpans(off, off, ClickableSpan.class);
                        if (link.length != 0) {
                            link[0].onClick(tv);
                        } else {
                            //do textview click event
                            if (null != iWBClickImpl) {
                                iWBClickImpl.onViewClick(WeiboTextView.this);
                            }
                        }
                    }
                }
                return true;
            }
        });
    }

    public void setWbContent(String str) {
        int strLen = str.length();
        SpannableStringBuilder spannableString = new SpannableStringBuilder(str);
        //设置正则
        Pattern pattern;
        switch (wbMode) {
            case WBTextMode.ALL:
                pattern = Pattern.compile(wbImpl.getAllRegular());
                break;
            case WBTextMode.CALL:
                pattern = Pattern.compile(wbImpl.getCallRegular());
                break;
            case WBTextMode.TOPIC:
                pattern = Pattern.compile(wbImpl.getTopicRegular());
                break;
            case WBTextMode.HTML:
                pattern = Pattern.compile(wbImpl.getHtmlRegular());
                break;
            case WBTextMode.CALL_AND_TOPIC:
                pattern = Pattern.compile(wbImpl.getCallAndTopicRegular());
                break;
            case WBTextMode.CALL_AND_HTML:
                pattern = Pattern.compile(wbImpl.getCallAndHtmlRegular());
                break;
            case WBTextMode.TOPIC_AND_HTML:
                pattern = Pattern.compile(wbImpl.getTopicAndHtmlRegular());
                break;
            default:
                pattern = Pattern.compile(wbImpl.getAllRegular());
                break;
        }
        Matcher matcher = pattern.matcher(spannableString);
        // 要实现文字的点击效果，这里需要做特殊处理
        if (matcher.find()) {
            setMovementMethod(LinkMovementMethod.getInstance());
            // 重置正则位置
            matcher.reset();
        }
        switch (wbMode) {
            case WBTextMode.ALL:
                dealWAllRegular(matcher, strLen, spannableString);
                break;
            case WBTextMode.CALL:
                dealWCallRegular(matcher, spannableString);
                break;
            case WBTextMode.TOPIC:
                dealWTopicRegular(matcher, spannableString);
                break;
            case WBTextMode.HTML:
                dealWHtmlRegular(matcher, strLen, spannableString);
                break;
            case WBTextMode.CALL_AND_TOPIC:
                dealWCallAndTopicRegular(matcher, spannableString);
                break;
            case WBTextMode.CALL_AND_HTML:
                dealWCallAndHtmlRegular(matcher, strLen, spannableString);
                break;
            case WBTextMode.TOPIC_AND_HTML:
                dealWTopicAndHtmlRegular(matcher, strLen, spannableString);
                break;
            default:
                break;
        }
    }

    private void dealWTopicAndHtmlRegular(Matcher matcher, int strLen, SpannableStringBuilder spannableString) {
        while (matcher.find()) {
            final String topic = matcher.group(1);
            final String url = matcher.group(2);
            // 处理话题##符号
            if (!TextUtils.isEmpty(topic)) {
                int start = matcher.start(1) - (strLen - spannableString.length());
                int end = start + topic.length();
                if (end <= spannableString.length()) {
                    dealWithTopic(spannableString, topic, start, end);
                }
            }
            // 处理url地址
            if (!TextUtils.isEmpty(url)) {
                int start = matcher.start(2) - (strLen - spannableString.length());
                int end = start + url.length();
                if (end <= spannableString.length()) {
                    dealWithHtml(spannableString, url, start, end);
                }
            }
        }
        setText(spannableString);
    }

    private void dealWCallAndHtmlRegular(Matcher matcher, int strLen, SpannableStringBuilder spannableString) {
        while (matcher.find()) {
            final String at = matcher.group(1);
            final String url = matcher.group(2);
            // 处理@符号
            if (!TextUtils.isEmpty(at)) {
                //获取匹配位置
                int start = matcher.start(1) - (strLen - spannableString.length());
                int end = start + at.length();
                if (end <= spannableString.length()) {
                    dealWithCall(spannableString, at, start, end);
                }
            }
            // 处理url地址
            if (!TextUtils.isEmpty(url)) {
                int start = matcher.start(2) - (strLen - spannableString.length());
                int end = start + url.length();
                if (end <= spannableString.length()) {
                    dealWithHtml(spannableString, url, start, end);
                }
            }
        }
        setText(spannableString);
    }

    private void dealWCallAndTopicRegular(Matcher matcher, SpannableStringBuilder spannableString) {
        while (matcher.find()) {
            final String at = matcher.group(1);
            final String topic = matcher.group(2);
            // 处理@符号
            if (!TextUtils.isEmpty(at)) {
                //获取匹配位置
                int start = matcher.start(1);
                int end = start + at.length();
                if (end <= spannableString.length()) {
                    dealWithCall(spannableString, at, start, end);
                }
            }
            // 处理话题##符号
            if (!TextUtils.isEmpty(topic)) {
                int start = matcher.start(2);
                int end = start + topic.length();
                if (end <= spannableString.length()) {
                    dealWithTopic(spannableString, topic, start, end);
                }
            }
        }
        setText(spannableString);
    }

    private void dealWHtmlRegular(Matcher matcher, int strLen, SpannableStringBuilder spannableString) {
        while (matcher.find()) {
            final String url = matcher.group();
            // 处理url地址
            if (!TextUtils.isEmpty(url)) {
                int start = matcher.start() - (strLen - spannableString.length());
                int end = start + url.length();
                if (end <= spannableString.length()) {
                    dealWithHtml(spannableString, url, start, end);
                }
            }
        }
        setText(spannableString);
    }

    private void dealWTopicRegular(Matcher matcher, SpannableStringBuilder spannableString) {
        while (matcher.find()) {
            final String topic = matcher.group();
            // 处理话题##符号
            if (!TextUtils.isEmpty(topic)) {
                int start = matcher.start();
                int end = start + topic.length();
                if (end <= spannableString.length()) {
                    dealWithTopic(spannableString, topic, start, end);
                }
            }
        }
        setText(spannableString);
    }

    private void dealWCallRegular(Matcher matcher, SpannableStringBuilder spannableString) {
        while (matcher.find()) {
            // 根据group的括号索引，可得出具体匹配哪个正则(0代表全部，1代表第一个括号)
            final String at = matcher.group();
            // 处理@符号
            if (!TextUtils.isEmpty(at)) {
                //获取匹配位置
                int start = matcher.start();
                int end = start + at.length();
                if (end <= spannableString.length()) {
                    dealWithCall(spannableString, at, start, end);
                }
            }
        }
        setText(spannableString);
    }

    private void dealWAllRegular(Matcher matcher, int strLen, SpannableStringBuilder spannableString) {
        while (matcher.find()) {
            // 根据group的括号索引，可得出具体匹配哪个正则(0代表全部，1代表第一个括号)
            final String at = matcher.group(1);
            final String topic = matcher.group(2);
            final String url = matcher.group(3);
            // 处理@符号
            if (!TextUtils.isEmpty(at)) {
                //获取匹配位置
                int start = matcher.start(1) - (strLen - spannableString.length());
                int end = start + at.length();
                if (end <= spannableString.length()) {
                    dealWithCall(spannableString, at, start, end);
                }
            }
            // 处理话题##符号
            if (!TextUtils.isEmpty(topic)) {
                int start = matcher.start(2) - (strLen - spannableString.length());
                int end = start + topic.length();
                if (end <= spannableString.length()) {
                    dealWithTopic(spannableString, topic, start, end);
                }
            }
            // 处理url地址
            if (!TextUtils.isEmpty(url)) {
                int start = matcher.start(3) - (strLen - spannableString.length());
                int end = start + url.length();
                if (end <= spannableString.length()) {
                    dealWithHtml(spannableString, url, start, end);
                }
            }
        }
        setText(spannableString);
    }

    private void dealWithCall(SpannableStringBuilder spannableString, final String at, int start, int end) {
        if (wbCallClickEnable) {
            CstClickableSpan clickableSpan = new CstClickableSpan(wbColor) {
                @Override
                public void onClick(View widget) {
                    if (null != iWBClickImpl) {
                        iWBClickImpl.onContentClick(WBClickMode.CALL, at);
                    }
                }
            };
            spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(wbColor);
            spannableString.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void dealWithTopic(SpannableStringBuilder spannableString, final String topic, int start, int end) {
        if (wbTopicClickEnable) {
            CstClickableSpan clickableSpan = new CstClickableSpan(wbColor) {
                @Override
                public void onClick(View widget) {
                    if (null != iWBClickImpl) {
                        iWBClickImpl.onContentClick(WBClickMode.TOPIC, topic);
                    }
                }
            };
            spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(wbColor);
            spannableString.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void dealWithHtml(SpannableStringBuilder spannableString, final String url, int start, int end) {
        CstClickableSpan clickableSpan = new CstClickableSpan(wbColor) {
            @Override
            public void onClick(View widget) {
                if (null != iWBClickImpl) {
                    iWBClickImpl.onContentClick(WBClickMode.HTML, url);
                }
            }
        };
        if (!TextUtils.isEmpty(wbHtmlReplace)) {
            SpannableString spannableString1 = new SpannableString(wbHtmlReplace);
            spannableString.replace(start, end, spannableString1);
            spannableString.setSpan(clickableSpan, start, start + spannableString1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public int getWbMode() {
        return wbMode;
    }

    public void setWbMode(int wbMode) {
        this.wbMode = wbMode;
    }

    public int getWbColor() {
        return wbColor;
    }

    public void setWbColor(int wbColor) {
        this.wbColor = wbColor;
    }

    public boolean isWbCallClickEnable() {
        return wbCallClickEnable;
    }

    public void setWbCallClickEnable(boolean wbCallClickEnable) {
        this.wbCallClickEnable = wbCallClickEnable;
    }

    public boolean isWbTopicClickEnable() {
        return wbTopicClickEnable;
    }

    public void setWbTopicClickEnable(boolean wbTopicClickEnable) {
        this.wbTopicClickEnable = wbTopicClickEnable;
    }

    public String getWbHtmlReplace() {
        return wbHtmlReplace;
    }

    public void setWbHtmlReplace(String wbHtmlReplace) {
        this.wbHtmlReplace = wbHtmlReplace;
    }

    public WBImpl getWbImpl() {
        return wbImpl;
    }

    public void setWbImpl(WBImpl wbImpl) {
        this.wbImpl = wbImpl;
    }

    public IWBClickImpl getiWBClickImpl() {
        return iWBClickImpl;
    }

    public void setiWBClickImpl(IWBClickImpl onWBClick) {
        this.iWBClickImpl = onWBClick;
    }
}
