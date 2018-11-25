package com.recycler.coverflow;

import android.app.Application;

import com.hai.floatinglayer.util.ScreenUtils;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ScreenUtils.init(this);
    }
}
