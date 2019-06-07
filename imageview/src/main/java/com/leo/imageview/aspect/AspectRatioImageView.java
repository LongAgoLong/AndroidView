package com.leo.imageview.aspect;

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
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.leo.imageview.R;

/**
 * Created by LEO
 * On 2019/6/7
 * Description:可设置比例的图片
 */
public class AspectRatioImageView extends ImageView {
    private String tag = "GIF";
    private Paint paint;
    private int textWidth;
    private int textHeight;
    private int paddingX;
    private int paddingY;
    private Paint bgPaint;

    private boolean isGif;
    private RectF targetRect;
    private Rect rect;

    private float viewAspectRatio;

    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AspectRatioImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.AspectRatioImageView, defStyleAttr, 0);
        viewAspectRatio = a.getFloat(R.styleable.AspectRatioImageView_imgAspectRatio, 0);
        a.recycle();

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(Color.parseColor("#ff658AB5"));

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setColor(Color.parseColor("#ffffffff"));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(dp2px( 9));
        rect = new Rect();
        paint.getTextBounds(tag, 0, tag.length(), rect);
        textHeight = rect.height();
        textWidth = (int) paint.measureText(tag);
        paddingX = dp2px(8);
        paddingY = dp2px(6);

        targetRect = new RectF(0, 0, 0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (viewAspectRatio != 0) {
            int widthSize = getExpectSize(dp2px( 80), widthMeasureSpec);
            int heightSize = (int) (widthSize / viewAspectRatio);
            setMeasuredDimension(widthSize, heightSize);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isGif)
            drawTag(canvas);
    }

    private void drawTag(Canvas canvas) {
        int measuredHeight = getHeight();
        int measuredWidth = getWidth();
        targetRect.set(measuredWidth - textWidth - paddingX, measuredHeight - textHeight - paddingY, measuredWidth, measuredHeight);

        canvas.drawRoundRect(targetRect, 5, 5, bgPaint);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = (int) ((targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2);
        canvas.drawText(tag, targetRect.centerX(), baseline, paint);
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
        }
        return result;
    }

    public void setViewAspectRatio(float viewAspectRatio) {
        this.viewAspectRatio = viewAspectRatio;
    }

    public void setText(@NonNull String tag) {
        this.tag = tag;
        paint.getTextBounds(tag, 0, tag.length(), rect);
        textHeight = rect.height();
        textWidth = (int) paint.measureText(tag);
    }

    public void setIsGif(boolean gif) {
        isGif = gif;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    protected int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取竖直居中基准线
     *
     * @param targetRect
     * @param paint
     * @return
     */
    protected int getCenterYBaseLine(RectF targetRect, Paint paint) {
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = (int) ((targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2);
        return baseline;
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @param width
     * @param height
     * @return
     */
    protected Bitmap drawable2Bitmap(Drawable drawable, int width, int height) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
