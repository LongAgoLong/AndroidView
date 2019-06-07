package com.leo.imageview.anycrop.model;

import android.graphics.Matrix;

import com.leo.imageview.anycrop.CropImageView;


public class API18Image extends CropImage {

  API18Image(CropImageView imageView) {
    super(imageView);
  }

  @Override
  public Matrix getMatrix() {
    return cropImageView.getImageMatrix();
  }
}