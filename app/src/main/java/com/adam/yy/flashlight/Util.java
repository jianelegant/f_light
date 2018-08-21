package com.adam.yy.flashlight;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

public class Util {

    public static void toast(String msg) {
        if(!TextUtils.isEmpty(msg)) {
            Toast.makeText(MainApp.s_GlobalCtx, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean getAutoOn() {
        return PreferenceManager.getDefaultSharedPreferences(MainApp.s_GlobalCtx).getBoolean(Const.SP_KEY_AUTO_ON, true);
    }

    public static void setAutoOn(boolean autoOn) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainApp.s_GlobalCtx).edit();
        editor.putBoolean(Const.SP_KEY_AUTO_ON, autoOn);
        editor.apply();
    }

    public static boolean getUseFlash() {
        return PreferenceManager.getDefaultSharedPreferences(MainApp.s_GlobalCtx).getBoolean(Const.SP_KEY_USE_CAMERA_FLASH, true);
    }

    public static void setUseFlash(boolean useFlash) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainApp.s_GlobalCtx).edit();
        editor.putBoolean(Const.SP_KEY_USE_CAMERA_FLASH, useFlash);
        editor.apply();
    }
}
