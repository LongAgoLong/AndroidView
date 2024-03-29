package com.leo.weibotext.span;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.ColorInt;


public class CstMovementMethod extends LinkMovementMethod {
    @ColorInt
    private int mClickBgColorInt;

    private BackgroundColorSpan mBgSpan;
    private ClickableSpan[] mClickLinks;
    private boolean isPassToTv = true;
    private static CstMovementMethod sInstance;
    private Context context;

    /**
     * true：响应textview的点击事件， false：响应设置的clickableSpan事件
     */
    public boolean isPassToTv() {
        return isPassToTv;
    }

    private void setPassToTv(boolean isPassToTv) {
        this.isPassToTv = isPassToTv;
    }

    public static CstMovementMethod getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CstMovementMethod.class) {
                if (null == sInstance) {
                    sInstance = new CstMovementMethod(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public CstMovementMethod(Context context) {
        this.context = context;
        this.mClickBgColorInt = Color.BLUE;
    }

    public void setmClickBgColorInt(@ColorInt int mClickBgColorInt) {
        this.mClickBgColorInt = mClickBgColorInt;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            mClickLinks = buffer.getSpans(off, off, ClickableSpan.class);
            if (mClickLinks.length > 0) {
                // 点击的是Span区域，不要把点击事件传递
                setPassToTv(false);
                Selection.setSelection(buffer, buffer.getSpanStart(mClickLinks[0]), buffer.getSpanEnd(mClickLinks[0]));
                //设置点击区域的背景色
                mBgSpan = new BackgroundColorSpan(mClickBgColorInt);
                buffer.setSpan(mBgSpan, buffer.getSpanStart(mClickLinks[0]), buffer.getSpanEnd(mClickLinks[0]), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                setPassToTv(true);
                // textview选中效果
//                widget.setBackgroundResource(mTextViewBgColorId);
            }

        } else if (action == MotionEvent.ACTION_UP) {
            if (mClickLinks.length > 0) {
                mClickLinks[0].onClick(widget);
                if (mBgSpan != null) {
                    buffer.removeSpan(mBgSpan);
                }

            } else {

            }
            Selection.removeSelection(buffer);
//            widget.setBackgroundResource(TRANS_COLOR);
        } else {
            if (mBgSpan != null) {
                buffer.removeSpan(mBgSpan);
            }
//            widget.setBackgroundResource(TRANS_COLOR);
        }
        return true;
//        return Touch.onTouchEvent(widget, buffer, event);
    }
}
