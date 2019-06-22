package com.leo.weibotext.enume;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        WBClickMode.CALL,
        WBClickMode.TOPIC,
        WBClickMode.HTML
})
@Retention(RetentionPolicy.SOURCE)
public @interface WBClickMode {
    int CALL = 0;
    int TOPIC = 1;
    int HTML = 2;
}
