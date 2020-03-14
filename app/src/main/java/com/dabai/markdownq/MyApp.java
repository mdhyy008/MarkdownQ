package com.dabai.markdownq;

import android.app.Application;

import com.tencent.bugly.Bugly;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //bugly初始化
        Bugly.init(getApplicationContext(), "daa90cc31f", false);
    }
}
