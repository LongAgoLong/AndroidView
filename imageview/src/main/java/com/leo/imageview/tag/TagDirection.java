package com.leo.imageview.tag;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        TagDirection.LEFT_TOP,
        TagDirection.LEFT_BOTTOM,
        TagDirection.RIGHT_TOP,
        TagDirection.RIGHT_BOTTOM,
})
@Retention(RetentionPolicy.SOURCE)
public @interface TagDirection {
    int LEFT_TOP = 0;
    int LEFT_BOTTOM = 1;
    int RIGHT_TOP = 2;
    int RIGHT_BOTTOM = 3;
}
