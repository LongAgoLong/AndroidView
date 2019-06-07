package com.leo.imageview.anycrop.model;

import android.os.Build;

import androidx.annotation.NonNull;

import com.leo.imageview.anycrop.CropImageView;

public class CropImageFactory {

  public CropImage getCropImage(@NonNull CropImageView imageView) {
    final boolean preApi18 = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2;
    return preApi18 ? new PreApi18CropImage(imageView) : new API18Image(imageView);
  }
}
