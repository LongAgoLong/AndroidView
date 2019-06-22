package com.leo.weibotext.span;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;


/**
 * Created by NyatoAndroid02 on 2016/3/7.
 * <p/>
 * 继承ClickableSpan复写updateDrawState方法，自定义所需样式
 *
 * @author Rabbit_Lee
 */

public class CstClickableSpan extends ClickableSpan {
    @ColorInt
    private int textColor;
    private boolean underLine;

    public CstClickableSpan(@ColorInt int color) {
        this(color, false);
    }

    public CstClickableSpan(@ColorInt int textColor, boolean underLine) {
        this.textColor = textColor;
        this.underLine = underLine;
    }

    @Override
    public void onClick(View widget) {

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(textColor);
        ds.setUnderlineText(underLine);
    }

}