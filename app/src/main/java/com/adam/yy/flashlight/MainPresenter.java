package com.adam.yy.flashlight;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.os.Message;
import android.support.design.widget.Snackbar;

import java.lang.ref.WeakReference;

public class MainPresenter implements GenericLifecycleObserver{

    private static final int HANDLER_MSG_BLING = 0;

    private static final int LEVEL_0 = 0; // aways
    private static final int LEVEL_1 = 1;
    private static final int LEVEL_2 = 2;

    private static final int LEVEL_0_DURATION = 0; // aways
    private static final int LEVEL_1_DURATION = 1000; // 1000 ms
    private static final int LEVEL_2_DURATION = 500;

    private int mDuration = LEVEL_0_DURATION;

    private Handler mHandler;

    private Flash mFlash = new Flash();

    public MainPresenter() {
        mHandler = new Handler(this);
    }

    public void setLevel(int level) {
        switch (level) {
            case LEVEL_0:
                mDuration = LEVEL_0_DURATION;
                break;
            case LEVEL_1:
                mDuration = LEVEL_1_DURATION;
                break;
            case LEVEL_2:
                mDuration = LEVEL_2_DURATION;
                break;
            default:
                mDuration = LEVEL_0_DURATION;
                break;
        }
        startBling();
    }

    private void startBling() {
        if(mHandler.hasMessages(HANDLER_MSG_BLING)) {
            mHandler.removeMessages(HANDLER_MSG_BLING);
        }
        if(LEVEL_0_DURATION == mDuration) {
            switchOn();
            return;
        }
        bling();
    }

    private void switchOn() {
        if(null != mFlash) {
            if(!mFlash.isOn()) {
                mFlash.turnOn();
            }
        }
    }

    private void switchOff() {
        if(mHandler.hasMessages(HANDLER_MSG_BLING)) {
            mHandler.removeMessages(HANDLER_MSG_BLING);
        }
        if(null != mFlash) {
            if(mFlash.isOn()) {
                mFlash.turnOff();
            }
        }
    }

    private void bling() {
        mHandler.sendEmptyMessageDelayed(HANDLER_MSG_BLING, mDuration);
        switchOnOff();
    }

    public void switchOnOff() {
        if(null != mFlash) {
            if(mFlash.isOn()) {
                mFlash.turnOff();
            } else {
                mFlash.turnOn();
            }
        }
    }

    @Override
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
        switch (event) {
            case ON_DESTROY:
                onDestroy();
                break;
            default:
                break;
        }
    }

    private void release() {
        if(null != mFlash) {
            mFlash.release();
        }
    }

    private void onDestroy() {
        mDuration = LEVEL_0_DURATION;
        switchOff();
        release();
    }

    static class Handler extends android.os.Handler{

        WeakReference<MainPresenter> mWeakPresenter;

        public Handler(MainPresenter mainPresenter) {
            mWeakPresenter = new WeakReference<>(mainPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MSG_BLING:
                    if(null != mWeakPresenter.get()) {
                        mWeakPresenter.get().bling();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
