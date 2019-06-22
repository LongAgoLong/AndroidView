package com.leo.weibotext.callback;

import android.view.View;

import com.leo.weibotext.enume.WBClickMode;

public interface OnWBClickImpl {
    class OnWBClickImplAdapter implements OnWBClickImpl {

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
