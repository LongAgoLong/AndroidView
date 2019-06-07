package com.leo.imageview.tag;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.leo.imageview.R;
import com.leo.imageview.aspect.AspectRatioImageView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by LEO
 * On 2019/6/7
 * Description:带标签的ImageView
 */
public class TagImageView extends AspectRatioImageView {

    private int tagTextColor;
    private int tagBgColor;
    private int tagBgRound;

    private int tagMode = TagMode.TEXT;
    private int tagDirection = TagDirection.LEFT_TOP;
    private String tagText = "GIF";
    private Paint textPaint;
    private Paint bgPaint;
    private int textWidth;
    private int textHeight;
    private int textPaddingX;
    private int textPaddingY;

    private RectF tagRect;
    private Rect textMeasureRect;
    private Bitmap tagBitmap;
    private int tagBitmapWidth, tagBitmapHeight;
    private int tagTextSize;

    public TagImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagImageView(Context context) {
        this(context, null, 0);
    }

    public TagImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(dp2px(9));
        textMeasureRect = new Rect();

        tagRect = new RectF(0, 0, 0, 0);
        getAttrs(attrs, defStyleAttr);
    }

    private void getAttrs(AttributeSet attrs, int defStyleAttr) {
        if (null != attrs) {
            TypedArray a = getContext().obtainStyledAttributes(attrs,
                    R.styleable.TagImageView, defStyleAttr, 0);
            tagMode = a.getInt(R.styleable.TagImageView_tag_mode, TagMode.TEXT);
            tagDirection = a.getInt(R.styleable.TagImageView_tag_direction, TagDirection.LEFT_TOP);
            tagBitmapWidth = a.getDimensionPixelSize(R.styleable.TagImageView_tag_src_width, dp2px(38));
            tagBitmapHeight = a.getDimensionPixelSize(R.styleable.TagImageView_tag_src_height, dp2px(38));
            tagBitmap = drawable2Bitmap(a.getDrawable(R.styleable.TagImageView_tag_src), tagBitmapWidth, tagBitmapHeight);
            textPaddingX = a.getDimensionPixelSize(R.styleable.TagImageView_tag_text_padding_x, dp2px(8));
            textPaddingY = a.getDimensionPixelSize(R.styleable.TagImageView_tag_text_padding_y, dp2px(6));
            tagText = a.getString(R.styleable.TagImageView_tag_text);
            tagTextSize = a.getDimensionPixelSize(R.styleable.TagImageView_tag_text_size, dp2px(38));
            tagBgRound = a.getDimensionPixelSize(R.styleable.TagImageView_tag_bg_round, dp2px(5));
            tagTextColor = a.getColor(R.styleable.TagImageView_tag_text_color, Color.WHITE);
            tagBgColor = a.getColor(R.styleable.TagImageView_tag_bg_color, Color.BLACK);
            a.recycle();
        } else {
            tagMode = TagMode.TEXT;
            tagDirection = TagDirection.LEFT_TOP;
            textPaddingX = dp2px(8);
            textPaddingY = dp2px(6);
            tagTextSize = dp2px(38);
            tagTextColor = Color.WHITE;
            tagBgColor = Color.BLACK;
        }
    }

    /**
     * 测绘文本宽高
     */
    private void measureText() {
        if (TextUtils.isEmpty(tagText)) {
            textWidth = textHeight = 0;
        } else {
            textPaint.setTextSize(tagTextSize);
            textPaint.getTextBounds(tagText, 0, tagText.length(), textMeasureRect);
            textHeight = textMeasureRect.height();
            textWidth = (int) textPaint.measureText(tagText);
        }
    }

    public TagImageView setTagMode(@TagMode int tagMode) {
        this.tagMode = tagMode;
        return this;
    }

    public TagImageView setTagDirection(@TagDirection int tagDirection) {
        this.tagDirection = tagDirection;
        return this;
    }

    public TagImageView setTagText(String tagText) {
        this.tagText = tagText;
        return this;
    }

    public TagImageView setTextPaddingX(int textPaddingX) {
        this.textPaddingX = textPaddingX;
        return this;
    }

    public TagImageView setTextPaddingY(int textPaddingY) {
        this.textPaddingY = textPaddingY;
        return this;
    }

    public TagImageView setTagBitmap(Bitmap tagBitmap) {
        this.tagBitmap = tagBitmap;
        return this;
    }

    public TagImageView setTagBitmapWidth(int tagBitmapWidth) {
        this.tagBitmapWidth = tagBitmapWidth;
        return this;
    }

    public TagImageView setTagBitmapHeight(int tagBitmapHeight) {
        this.tagBitmapHeight = tagBitmapHeight;
        return this;
    }

    public TagImageView setTagTextSize(int tagTextSize) {
        this.tagTextSize = tagTextSize;
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (tagMode == TagMode.IMG) {
            drawTagBitmap(canvas);
        } else if (tagMode == TagMode.TEXT) {
            drawTagText(canvas);
        }
    }

    private void drawTagBitmap(Canvas canvas) {
        if (null == tagBitmap) {
            return;
        }
        int measuredWidth = getWidth();
        int measureHeight = getHeight();
        if (tagDirection == TagDirection.LEFT_TOP) {
            tagRect.set(0, 0, tagBitmapWidth, tagBitmapHeight);
        } else if (tagDirection == TagDirection.LEFT_BOTTOM) {
            tagRect.set(0, measureHeight - tagBitmapHeight, tagBitmapWidth, measureHeight);
        } else if (tagDirection == TagDirection.RIGHT_TOP) {
            tagRect.set(measuredWidth - tagBitmapWidth, 0, measuredWidth, tagBitmapHeight);
        } else if (tagDirection == TagDirection.RIGHT_BOTTOM) {
            tagRect.set(measuredWidth - tagBitmapWidth, measureHeight - tagBitmapHeight, measuredWidth, measureHeight);
        }
        canvas.drawBitmap(tagBitmap, null, tagRect, textPaint);
    }

    private void drawTagText(Canvas canvas) {
        if (TextUtils.isEmpty(tagText)) {
            return;
        }
        measureText();
        int measuredHeight = getHeight();
        int measuredWidth = getWidth();
        if (tagDirection == TagDirection.LEFT_TOP) {
            tagRect.set(0, 0, textWidth + textPaddingX, textHeight + textPaddingY);
        } else if (tagDirection == TagDirection.LEFT_BOTTOM) {
            tagRect.set(0, measuredHeight - textHeight - textPaddingY, textWidth + textPaddingX, measuredHeight);
        } else if (tagDirection == TagDirection.RIGHT_TOP) {
            tagRect.set(measuredWidth - textWidth - textPaddingX, 0, measuredWidth, textHeight + textPaddingY);
        } else if (tagDirection == TagDirection.RIGHT_BOTTOM) {
            tagRect.set(measuredWidth - textWidth - textPaddingX, measuredHeight - textHeight - textPaddingY, measuredWidth, measuredHeight);
        }
        bgPaint.setColor(tagBgColor);
        canvas.drawRoundRect(tagRect, tagBgRound, tagBgRound, bgPaint);
        textPaint.setColor(tagTextColor);
        int baseline = getCenterYBaseLine(tagRect, textPaint);
        canvas.drawText(tagText, tagRect.centerX(), baseline, textPaint);
    }
}
