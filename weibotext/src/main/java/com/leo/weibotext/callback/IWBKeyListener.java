package com.leo.weibotext.callback;

import android.view.KeyEvent;
import android.view.View;

public interface IWBKeyListener {
    boolean onKey(View v, int keyCode, KeyEvent event);
}
