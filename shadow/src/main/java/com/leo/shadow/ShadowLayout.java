package com.leo.shadow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

public class ShadowLayout extends FrameLayout {

    private int mShadowColor;
    private int mShadowRadius;
    private int mCornerRadius;
    private int mDx;
    private int mDy;

    private boolean mInvalidateShadowOnSizeChanged = true;
    private boolean mForceInvalidateShadow = false;
    private BitmapDrawable bitmapDrawable;

    public ShadowLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return 0;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0 && (getBackground() == null || mInvalidateShadowOnSizeChanged || mForceInvalidateShadow)) {
            mForceInvalidateShadow = false;
            setBackgroundCompat(w, h);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mForceInvalidateShadow) {
            mForceInvalidateShadow = false;
            setBackgroundCompat(right - left, bottom - top);
        }
    }

    public void setInvalidateShadowOnSizeChanged(boolean invalidateShadowOnSizeChanged) {
        mInvalidateShadowOnSizeChanged = invalidateShadowOnSizeChanged;
    }

    public void invalidateShadow() {
        mForceInvalidateShadow = true;
        requestLayout();
        invalidate();
    }

    private void initView(Context context, AttributeSet attrs) {
        initAttributes(context, attrs);
        int absDx = Math.abs(mDx);
        int absDy = Math.abs(mDy);
        int leftPadding = Math.max(mShadowRadius - absDx, 0);
        int rightPadding = Math.max(mShadowRadius + absDx, 0);
        int topPadding = Math.max(mShadowRadius - absDy, 0);
        int bottomPadding = Math.max(mShadowRadius + absDy, 0);
        setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
    }

    @SuppressWarnings("deprecation")
    private void setBackgroundCompat(int w, int h) {
        if (!isInEditMode()) {
            bitmapDrawable = ShadowHelper.createShadow(getContext(),
                    w, h, mCornerRadius, mShadowRadius,
                    mDx, mDy, mShadowColor);
            setBackgroundCompat(bitmapDrawable);
        }
    }

    private void setBackgroundCompat(@Nullable Drawable drawable) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable);
        } else {
            setBackground(drawable);
        }
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray attr = getTypedArray(context, attrs, R.styleable.ShadowLayout);
        if (attr == null) {
            return;
        }
        try {
            mCornerRadius = attr.getDimensionPixelSize(R.styleable.ShadowLayout_sl_cornerRadius, getResources().getDimensionPixelSize(R.dimen.default_corner_radius));
            mShadowRadius = attr.getDimensionPixelSize(R.styleable.ShadowLayout_sl_shadowRadius, getResources().getDimensionPixelSize(R.dimen.default_shadow_radius));
            mDx = attr.getDimensionPixelSize(R.styleable.ShadowLayout_sl_dx, 0);
            mDy = attr.getDimensionPixelSize(R.styleable.ShadowLayout_sl_dy, 0);
            mShadowColor = attr.getColor(R.styleable.ShadowLayout_sl_shadowColor, getResources().getColor(R.color.default_shadow_color));
        } finally {
            attr.recycle();
        }
    }

    private TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    /**
     * 设置阴影显示与否
     *
     * @param isVisibility
     */
    public void setShadowVisibility(boolean isVisibility) {
        if (isVisibility) {
            setBackgroundCompat(bitmapDrawable);
        } else {
            setBackgroundCompat(null);
        }
    }
}
