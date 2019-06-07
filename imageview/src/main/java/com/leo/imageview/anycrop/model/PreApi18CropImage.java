package com.leo.imageview.anycrop.model;

import android.graphics.Matrix;

import com.leo.imageview.anycrop.CropImageView;
import com.leo.imageview.anycrop.CropType;


public class PreApi18CropImage extends CropImage {

  private Matrix matrix;

  PreApi18CropImage(CropImageView imageView) {
    super(imageView);

    init(imageView);
  }

  private void init(CropImageView imageView) {
    if (imageView.getCropType() != CropType.NONE) {
      matrix = new Matrix();
    }
  }

  @Override
  public Matrix getMatrix() {
    return matrix == null ? cropImageView.getImageMatrix() : matrix;
  }
}
