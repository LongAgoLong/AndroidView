package com.leo.imageview.tag;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        TagMode.TEXT,
        TagMode.IMG
})
@Retention(RetentionPolicy.SOURCE)
public @interface TagMode {
    int TEXT = 0;
    int IMG = 1;
}
