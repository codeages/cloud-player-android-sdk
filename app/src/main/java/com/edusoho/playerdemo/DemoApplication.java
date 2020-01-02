package com.edusoho.playerdemo;

import android.app.Application;

import com.edusoho.cloud.manager.Downloader;

public class DemoApplication extends Application {

    public static DemoApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Downloader.init(getApplicationContext());
    }
}
