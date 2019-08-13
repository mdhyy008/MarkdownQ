package com.dabai.markdownq.utils;
import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

/**
 * Created by 。 on 2018/12/5.
 */

public class CarApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        CrashHandler.getInstance().init(this);
        context = this;
    }
    public static Context getContext(){
        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //因为引用的包过多，实现多包问题
        MultiDex.install(this);

    }
}


