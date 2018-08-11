package com.adam.yy.flashlight;

import android.app.Application;
import android.content.Context;

public class MainApp extends Application{

    public static Context s_GlobalCtx;

    @Override
    public void onCreate() {
        super.onCreate();
        s_GlobalCtx = this;
    }
}
