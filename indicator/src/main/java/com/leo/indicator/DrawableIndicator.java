package com.leo.indicator;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import androidx.annotation.AnimatorRes;
import androidx.annotation.DrawableRes;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by LEO
 * On 2019/6/7
 * Description:Drawable指示器
 */
public class DrawableIndicator extends LinearLayout {
    private final static int DEFAULT_INDICATOR_WIDTH = 5;
    private ViewPager mViewpager;
    private int mIndicatorMargin = -1;
    private int mIndicatorWidth = -1;
    private int mIndicatorHeight = -1;
    private int mAnimatorResId = R.animator.scale_with_alpha;
    private int mAnimatorReverseResId = 0;
    private int mIndicatorResIdSelected = R.drawable.point_radius;
    private int mIndicatorResIdUnselected = R.drawable.point_radius;
    private Animator mAnimatorOut;
    private Animator mAnimatorIn;
    private Animator mImmediateAnimatorOut;
    private Animator mImmediateAnimatorIn;

    private int mLastPosition = -1;

    public DrawableIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public DrawableIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
        handleTypedArray(context, attrs);
        checkIndicatorConfig(context);
    }

    private void handleTypedArray(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DrawableIndicator);
        mIndicatorWidth = a.getDimensionPixelSize(R.styleable.DrawableIndicator_di_width, dp2px(8));
        mIndicatorHeight = a.getDimensionPixelSize(R.styleable.DrawableIndicator_di_height, dp2px(8));
        mIndicatorMargin = a.getDimensionPixelSize(R.styleable.DrawableIndicator_di_margin, dp2px(5));
        mAnimatorResId = a.getResourceId(R.styleable.DrawableIndicator_di_animator,
                R.animator.scale_with_alpha);
        mAnimatorReverseResId = a.getResourceId(R.styleable.DrawableIndicator_di_animator_reverse, 0);
        mIndicatorResIdSelected = a.getResourceId(R.styleable.DrawableIndicator_di_drawable_selected,
                R.drawable.point_radius);
        mIndicatorResIdUnselected = a.getResourceId(R.styleable.DrawableIndicator_di_drawable_unselected,
                mIndicatorResIdSelected);
        a.recycle();
    }

    /**
     * Create and configure Indicator in Java code.
     */
    public void configureIndicator(int indicatorWidth, int indicatorHeight, int indicatorMargin) {
        configureIndicator(indicatorWidth, indicatorHeight, indicatorMargin,
                R.animator.scale_with_alpha, 0, R.drawable.point_radius, R.drawable.point_radius);
    }

    public void configureIndicator(int indicatorWidth, int indicatorHeight, int indicatorMargin,
                                   @AnimatorRes int animatorId, @AnimatorRes int animatorReverseId,
                                   @DrawableRes int indicatorBackgroundId,
                                   @DrawableRes int indicatorUnselectedBackgroundId) {

        mIndicatorWidth = indicatorWidth;
        mIndicatorHeight = indicatorHeight;
        mIndicatorMargin = indicatorMargin;

        mAnimatorResId = animatorId;
        mAnimatorReverseResId = animatorReverseId;
        mIndicatorResIdSelected = indicatorBackgroundId;
        mIndicatorResIdUnselected = indicatorUnselectedBackgroundId;

        checkIndicatorConfig(getContext());
    }

    private void checkIndicatorConfig(Context context) {
        mIndicatorWidth = (mIndicatorWidth < 0) ? dp2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorWidth;
        mIndicatorHeight =
                (mIndicatorHeight < 0) ? dp2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorHeight;
        mIndicatorMargin =
                (mIndicatorMargin < 0) ? dp2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorMargin;

        mAnimatorResId = (mAnimatorResId == 0) ? R.animator.scale_with_alpha : mAnimatorResId;

        mAnimatorOut = createAnimatorOut(context);
        mImmediateAnimatorOut = createAnimatorOut(context);
        mImmediateAnimatorOut.setDuration(0);

        mAnimatorIn = createAnimatorIn(context);
        mImmediateAnimatorIn = createAnimatorIn(context);
        mImmediateAnimatorIn.setDuration(0);

        mIndicatorResIdSelected = (mIndicatorResIdSelected == 0) ? R.drawable.point_radius
                : mIndicatorResIdSelected;
        mIndicatorResIdUnselected =
                (mIndicatorResIdUnselected == 0) ? mIndicatorResIdSelected
                        : mIndicatorResIdUnselected;
    }

    private Animator createAnimatorOut(Context context) {
        return AnimatorInflater.loadAnimator(context, mAnimatorResId);
    }

    private Animator createAnimatorIn(Context context) {
        Animator animatorIn;
        if (mAnimatorReverseResId == 0) {
            animatorIn = AnimatorInflater.loadAnimator(context, mAnimatorResId);
            animatorIn.setInterpolator(new ReverseInterpolator());
        } else {
            animatorIn = AnimatorInflater.loadAnimator(context, mAnimatorReverseResId);
        }
        return animatorIn;
    }

    public void setViewPager(ViewPager viewPager) {
        mViewpager = viewPager;
        if (mViewpager != null && mViewpager.getAdapter() != null) {
            createIndicators();
            mViewpager.removeOnPageChangeListener(mInternalPageChangeListener);
            mViewpager.addOnPageChangeListener(mInternalPageChangeListener);
            mViewpager.getAdapter().registerDataSetObserver(mInternalDataSetObserver);
            mInternalPageChangeListener.onPageSelected(mViewpager.getCurrentItem());
        }
    }

    private final ViewPager.OnPageChangeListener mInternalPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            if (mViewpager.getAdapter() == null || mViewpager.getAdapter().getCount() <= 0) {
                return;
            }

            if (mAnimatorIn.isRunning()) {
                mAnimatorIn.end();
                mAnimatorIn.cancel();
            }

            if (mAnimatorOut.isRunning()) {
                mAnimatorOut.end();
                mAnimatorOut.cancel();
            }

            if (mLastPosition >= 0) {
                View currentIndicator = getChildAt(mLastPosition);
                currentIndicator.setBackgroundResource(mIndicatorResIdUnselected);
                mAnimatorIn.setTarget(currentIndicator);
                mAnimatorIn.start();
            }

            View selectedIndicator = getChildAt(position);
            selectedIndicator.setBackgroundResource(mIndicatorResIdSelected);
            mAnimatorOut.setTarget(selectedIndicator);
            mAnimatorOut.start();

            mLastPosition = position;
        }
    };

    private DataSetObserver mInternalDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();

            int newCount = null == mViewpager.getAdapter() ? 0 : mViewpager.getAdapter().getCount();
            int currentCount = getChildCount();

            if (newCount == currentCount) {  // No change
                return;
            } else if (mLastPosition < newCount) {
                mLastPosition = mViewpager.getCurrentItem();
            } else {
                mLastPosition = -1;
            }

            createIndicators();
        }
    };

    /**
     * @deprecated User ViewPager addOnPageChangeListener
     */
    @Deprecated
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        if (mViewpager == null) {
            throw new NullPointerException("can not find Viewpager , setViewPager first");
        }
        mViewpager.removeOnPageChangeListener(onPageChangeListener);
        mViewpager.addOnPageChangeListener(onPageChangeListener);
    }

    private void createIndicators() {
        removeAllViews();
        int count = null == mViewpager.getAdapter() ? 0 : mViewpager.getAdapter().getCount();
        if (count <= 0) {
            return;
        }
        int currentItem = mViewpager.getCurrentItem();

        for (int i = 0; i < count; i++) {
            if (currentItem == i) {
                addIndicator(mIndicatorResIdSelected, mImmediateAnimatorOut);
            } else {
                addIndicator(mIndicatorResIdUnselected, mImmediateAnimatorIn);
            }
        }
    }

    private void addIndicator(@DrawableRes int backgroundDrawableId, Animator animator) {
        if (animator.isRunning()) {
            animator.end();
            animator.cancel();
        }

        View Indicator = new View(getContext());
        Indicator.setBackgroundResource(backgroundDrawableId);
        addView(Indicator, mIndicatorWidth, mIndicatorHeight);
        LayoutParams lp = (LayoutParams) Indicator.getLayoutParams();
        lp.leftMargin = mIndicatorMargin;
        lp.rightMargin = mIndicatorMargin;
        Indicator.setLayoutParams(lp);

        animator.setTarget(Indicator);
        animator.start();
    }

    private class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float value) {
            return Math.abs(1.0f - value);
        }
    }

    public int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
