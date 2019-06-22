package com.leo.weibotext.enume;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        WBEditMode.ALL,
        WBEditMode.CALL,
        WBEditMode.TOPIC
})
@Retention(RetentionPolicy.SOURCE)
public @interface WBEditMode {
    int ALL = 0;
    int CALL = 1;
    int TOPIC = 2;
}
