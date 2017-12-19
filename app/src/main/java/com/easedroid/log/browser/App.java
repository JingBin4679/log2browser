package com.easedroid.log.browser;

import android.app.Application;

import com.easedroid.logcat.LogcatHelper;

/**
 * Created by bin.jing on 2017/12/18.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogcatHelper.init();
    }
}
