/*
 * Copyright 2015 Cesar Diez Sanchez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leo.imageview.anycrop;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.leo.imageview.R;
import com.leo.imageview.anycrop.model.CropImage;
import com.leo.imageview.anycrop.model.CropImageFactory;

public class CropImageView extends ImageView {

    private CropImage cropImage;
    private int cropType = CropType.NONE;

    public CropImageView(Context context) {
        super(context);

        initImageView();
    }

    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initImageView();
        if (null != attrs) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CropImageView, defStyle, 0);
            cropType = a.getInt(R.styleable.CropImageView_crop_direction, CropType.NONE);
            a.recycle();
        }
        setCropType(cropType);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CropImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initImageView();
    }

    /**
     * Set crop type for the {@link ImageView}
     *
     * @param cropType A {@link CropType} desired scaling mode.
     */
    public void setCropType(@CropType int cropType) {
        this.cropType = cropType;

        setWillNotCacheDrawing(false);

        requestLayout();
        invalidate();
    }

    /**
     * Return the current crop type in use by this ImageView.
     *
     * @return a {@link CropType} in use by this ImageView
     */
    public
    @CropType
    int getCropType() {
        return cropType;
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        final boolean changed = super.setFrame(l, t, r, b);
        if (!isInEditMode() && cropImage != null && getDrawable() != null) {
            cropImage.computeImageTransformation();
        }
        return changed;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initImageView();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        initImageView();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        initImageView();
    }

    private void initImageView() {
        setScaleType(ScaleType.MATRIX);

        if (getDrawable() != null) {
            cropImage = new CropImageFactory().getCropImage(this);
        }
    }

}
