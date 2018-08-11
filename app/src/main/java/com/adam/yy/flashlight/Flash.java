package com.adam.yy.flashlight;

import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.hardware.Camera.Parameters.FLASH_MODE_AUTO;
import static android.hardware.Camera.Parameters.FLASH_MODE_ON;
import static android.hardware.Camera.Parameters.FLASH_MODE_TORCH;

public class Flash {

    private Camera mCamera;
    private AtomicBoolean isSupport;
    private boolean isOn = false;

    public void turnOn() {
        if (null == mCamera) {
            mCamera = Camera.open();
        }

        Camera.Parameters p = mCamera.getParameters();
        p.setFlashMode(getFlashOnParameter());
        try {
            mCamera.setPreviewTexture(new SurfaceTexture(0));
            mCamera.setParameters(p);
            mCamera.startPreview();
            mCamera.autoFocus(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        isOn = true;
    }

    private String getFlashOnParameter() {
        if (null != mCamera) {
            List<String> flashModes = mCamera.getParameters().getSupportedFlashModes();
            if (flashModes.contains(FLASH_MODE_TORCH)) {
                return FLASH_MODE_TORCH;
            } else if (flashModes.contains(FLASH_MODE_ON)) {
                return FLASH_MODE_ON;
            } else if (flashModes.contains(FLASH_MODE_AUTO)) {
                return FLASH_MODE_AUTO;
            }
        }
        return FLASH_MODE_TORCH;
    }

    public void turnOff() {
        if (null != mCamera) {
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(params);
            mCamera.stopPreview();
            isOn = false;
        }
    }

    public void release() {
        if (null != mCamera) {
            mCamera.release();
            isOn = false;
        }
    }

    public boolean isOn() {
        return isOn;
    }

    public boolean isSupport() {
        if (null == isSupport) {
            isSupport = new AtomicBoolean(false);
            PackageManager packageManager = MainApp.s_GlobalCtx.getPackageManager();
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                isSupport.set(true);
            }
        }
        return isSupport.get();
    }
}
