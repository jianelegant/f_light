package com.adam.yy.flashlight;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PerUtil {

    public static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;

    public static boolean hasCameraPermission() {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(MainApp.s_GlobalCtx, CAMERA_PERMISSION);
    }

    public static void requestCameraPremission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{CAMERA_PERMISSION}, requestCode);
    }
}
