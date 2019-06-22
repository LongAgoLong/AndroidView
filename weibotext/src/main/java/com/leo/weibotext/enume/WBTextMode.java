package com.leo.weibotext.enume;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        WBTextMode.ALL,
        WBTextMode.CALL,
        WBTextMode.TOPIC,
        WBTextMode.HTML,
        WBTextMode.CALL_AND_TOPIC,
        WBTextMode.CALL_AND_HTML,
        WBTextMode.TOPIC_AND_HTML
})
@Retention(RetentionPolicy.SOURCE)
public @interface WBTextMode {
    int ALL = 0;
    int CALL = 1;
    int TOPIC = 2;
    int HTML = 3;
    int CALL_AND_TOPIC = 4;
    int CALL_AND_HTML = 5;
    int TOPIC_AND_HTML = 6;
}
