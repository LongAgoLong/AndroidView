package com.leo.weibotext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.leo.weibotext.callback.IWBClickImpl;
import com.leo.weibotext.callback.IWBKeyListener;
import com.leo.weibotext.callback.IWBTouchListener;
import com.leo.weibotext.callback.WBImpl;
import com.leo.weibotext.enume.WBClickMode;
import com.leo.weibotext.enume.WBEditMode;
import com.leo.weibotext.span.CstClickableSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeiboEditText extends androidx.appcompat.widget.AppCompatEditText {
    @WBEditMode
    private int wbMode = WBEditMode.ALL;
    private int wbColor = Color.BLUE;
    private boolean wbCallClickEnable;
    private boolean wbTopicClickEnable;
    private WBImpl wbImpl = WBImpl.DEFAULT;
    // 唯一性校验
    private boolean isWbUniquenessCheck;
    private IWBClickImpl iWBClickImpl;
    private IWBKeyListener iWBKeyListener;
    private IWBTouchListener iWBTouchListener;

    public WeiboEditText(Context context) {
        super(context);
        initAttr(null, 0);
    }

    public WeiboEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs, 0);
    }

    public WeiboEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs, defStyleAttr);
    }

    private void initAttr(AttributeSet attrs, int defStyleAttr) {
        if (null != attrs) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WeiboEditText, defStyleAttr, 0);
            wbMode = a.getInt(R.styleable.WeiboEditText_wb_edit_mode, WBEditMode.ALL);
            wbColor = a.getColor(R.styleable.WeiboEditText_wb_edit_color, Color.BLUE);
            wbCallClickEnable = a.getBoolean(R.styleable.WeiboEditText_wb_edit_call_click_enable, false);
            wbTopicClickEnable = a.getBoolean(R.styleable.WeiboEditText_wb_edit_topic_click_enable, false);
            isWbUniquenessCheck = a.getBoolean(R.styleable.WeiboEditText_wb_edit_uniqueness_check, false);
            a.recycle();
        }
        // 设置回车可完整删除@
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return (keyCode == KeyEvent.KEYCODE_DEL
                        && event.getAction() == KeyEvent.ACTION_DOWN
                        && matchDelete())
                        || (null != iWBKeyListener && iWBKeyListener.onKey(v, keyCode, event));
            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return setScrollEditText(event) || (null != iWBTouchListener && iWBTouchListener.onTouch(v, event));
            }
        });
    }

    /**
     * 设置wb内容
     *
     * @param str
     */
    public void setWbContent(String str) {
        setText(match(str));
        requestFocus();
        setSelection(str.length());
    }

    /**
     * @param message 格式："@" + str + "\t"
     */
    public boolean appendCall(String message) {
        String releasemsg = getText().toString();
        if (isWbUniquenessCheck) {
            SpannableString spannableString = new SpannableString(releasemsg);
            Pattern pattern = Pattern.compile(message);
            Matcher matcher = pattern.matcher(spannableString);
            if (matcher.find()) {
                return false;
            }
        }
        int start = getSelectionStart();
        String s1 = releasemsg.substring(0, start);
        String s2 = releasemsg.substring(start);
        String s = s1 + message + s2;
        setText(match(s));
        requestFocus();
        setSelection(start + message.length());
        return true;
    }

    /**
     * @param message："#" + str + "#"
     */
    public boolean appendTopic(String message) {
        String releasemsg = getText().toString();
        if (isWbUniquenessCheck) {
            SpannableString spannableString = new SpannableString(releasemsg);
            Pattern pattern = Pattern.compile(message);
            Matcher matcher = pattern.matcher(spannableString);
            if (matcher.find()) {
                return false;
            }
        }
        int start = getSelectionStart();
        String s1 = releasemsg.substring(0, start);
        String s2 = releasemsg.substring(start);
        String s = s1 + message + s2;
        setText(match(s));
        requestFocus();
        setSelection(start + message.length());
        return true;
    }

    /**
     * 匹配@#
     *
     * @param source
     * @return
     */
    private SpannableString match(String source) {
        SpannableString spannableString = new SpannableString(source);
        Pattern pattern = Pattern.compile(wbImpl.getCallAndTopicRegular());
        Matcher matcher = pattern.matcher(spannableString);
        while (matcher.find()) {
            final String at = matcher.group(1);
            final String topic = matcher.group(2);
            if (!TextUtils.isEmpty(at) && (wbMode == WBEditMode.ALL || wbMode == WBEditMode.CALL)) {
                int start = matcher.start();
                int end = start + at.length();
                if (end <= spannableString.length()) {
                    if (wbCallClickEnable) {
                        CstClickableSpan clickableSpan = new CstClickableSpan(wbColor) {
                            @Override
                            public void onClick(View widget) {
                                if (null != iWBClickImpl) {
                                    iWBClickImpl.onContentClick(WBClickMode.TOPIC, at);
                                }
                            }
                        };
                        spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(wbColor);
                        spannableString.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
            if (!TextUtils.isEmpty(topic) && (wbMode == WBEditMode.ALL || wbMode == WBEditMode.TOPIC)) {
                int start = matcher.start(2);
                int end = start + topic.length();
                if (end <= spannableString.length()) {
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
            }
        }
        return spannableString;
    }

    /**
     * EditText 以@xx ##结尾，删除@xx ##
     *
     * @return
     */
    private boolean matchDelete() {
        try {
            int selectionStart = getSelectionStart();
            String str = getText().toString();
            String s = str.substring(0, selectionStart);
            String s_last = str.substring(selectionStart);
            if (!TextUtils.isEmpty(s)) {
                SpannableStringBuilder spannableString = new SpannableStringBuilder(s);
                Pattern pattern = Pattern.compile(wbImpl.getCallAndTopicRegular());
                Matcher matcher = pattern.matcher(spannableString);
                int start = 0;
                int end = 0;
                String s1 = null;
                while (matcher.find()) {
                    String at = matcher.group(1);
                    String topic = matcher.group(2);
                    String str1 = null;
                    if (!TextUtils.isEmpty(at)) {
                        str1 = at;
                    } else if (!TextUtils.isEmpty(topic)) {
                        str1 = topic;
                    }
                    if (!TextUtils.isEmpty(str1)) {
                        s1 = str1;
                        start = matcher.start();
                        end = start + str1.length();
                    }
                }
                if (!TextUtils.isEmpty(s1) && s.endsWith(s1)) {
                    spannableString.delete(start, end);
                    String s2 = spannableString.toString() + s_last;
                    setText(match(s2));
                    setSelection(selectionStart - s1.length());
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置scrollview中的Edittext可滚动
     *
     * @param event
     */
    private boolean setScrollEditText(MotionEvent event) {
        if (canVerticalScroll()) {
            // 告诉父view，我的事件自己处理
            getParent().requestDisallowInterceptTouchEvent(true);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // 告诉父view，你可以处理了
                getParent().requestDisallowInterceptTouchEvent(false);
            }
            return true;
        }
        return false;
    }

    /**
     * EditText竖直方向是否可以滚动
     *
     * @return true：可以滚动   false：不可以滚动
     */
    private boolean canVerticalScroll() {
        //滚动的距离
        int scrollY = getScrollY();
        //控件内容的总高度
        int scrollRange = getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = getHeight() - getCompoundPaddingTop() - getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;
        if (scrollDifference == 0) {
            return false;
        }
        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    public int getWbMode() {
        return wbMode;
    }

    public void setWbMode(@WBEditMode int wbMode) {
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

    public WBImpl getWbImpl() {
        return wbImpl;
    }

    public void setWbImpl(WBImpl wbImpl) {
        this.wbImpl = wbImpl;
    }

    public boolean isWbUniquenessCheck() {
        return isWbUniquenessCheck;
    }

    public void setWbUniquenessCheck(boolean wbUniquenessCheck) {
        isWbUniquenessCheck = wbUniquenessCheck;
    }

    public IWBClickImpl getWBClickImpl() {
        return iWBClickImpl;
    }

    public void setWBClickImpl(IWBClickImpl iWBClickImpl) {
        this.iWBClickImpl = iWBClickImpl;
    }

    public IWBKeyListener getWBKeyListener() {
        return iWBKeyListener;
    }

    public void setiWBKeyListener(IWBKeyListener iWBKeyListener) {
        this.iWBKeyListener = iWBKeyListener;
    }

    public IWBTouchListener getiWBTouchListener() {
        return iWBTouchListener;
    }

    public void setiWBTouchListener(IWBTouchListener iWBTouchListener) {
        this.iWBTouchListener = iWBTouchListener;
    }
}
