package com.leo.weibotext.callback;

import android.view.View;

import com.leo.weibotext.mode.WBClickMode;

public interface IWBClickImpl {
    class IWBClickImplAdapter implements IWBClickImpl {

        @Override
        public void onContentClick(@WBClickMode int wbMode, String str) {

        }

        @Override
        public void onViewClick(View view) {

        }
    }

    void onContentClick(@WBClickMode int wbMode, String str);

    void onViewClick(View view);
}
