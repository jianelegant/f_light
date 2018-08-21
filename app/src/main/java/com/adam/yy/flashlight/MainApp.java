package com.adam.yy.flashlight;

import android.app.Application;
import android.content.Context;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.MobileAds;

public class MainApp extends Application{

    public static Context s_GlobalCtx;

    @Override
    public void onCreate() {
        super.onCreate();
        s_GlobalCtx = this;

        initFlur();
        initAd();
    }

    private void initFlur() {
        new FlurryAgent.Builder()
                .build(this, "RK5296ZK56GQ65X8RK5N");
    }

    private void initAd() {
        MobileAds.initialize(this, "ca-app-pub-5644941632262899~2953475714");
    }
}
