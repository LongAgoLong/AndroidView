package com.leo.androidview;

import android.app.Application;

import com.leo.system.ContextHelp;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ContextHelp.setContext(this);
    }
}
