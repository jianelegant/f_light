package com.adam.yy.flashlight;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.os.Message;

import java.lang.ref.WeakReference;

public class MainPresenter implements GenericLifecycleObserver, IMainContract.IPresenter{

    private static final int HANDLER_MSG_BLING = 0;

    private static final int LEVEL_0 = 0; // aways
    private static final int LEVEL_1 = 1;
    private static final int LEVEL_2 = 2;
    private static final int LEVEL_3 = 3;
    private static final int LEVEL_4 = 4;
    private static final int LEVEL_5 = 5;
    private static final int LEVEL_6 = 6;
    private static final int LEVEL_7 = 7;
    private static final int LEVEL_8 = 8;
    private static final int LEVEL_9 = 9;

    private static final int LEVEL_0_DURATION = 0; // aways
    private static final int LEVEL_1_DURATION = 1000; // 1000 ms
    private static final int LEVEL_2_DURATION = 890;
    private static final int LEVEL_3_DURATION = 780;
    private static final int LEVEL_4_DURATION = 670;
    private static final int LEVEL_5_DURATION = 560;
    private static final int LEVEL_6_DURATION = 450;
    private static final int LEVEL_7_DURATION = 340;
    private static final int LEVEL_8_DURATION = 230;
    private static final int LEVEL_9_DURATION = 120;

    private int mDuration = LEVEL_0_DURATION;

    private Handler mHandler;

    private Flash mFlash = new Flash();

    IMainContract.IView mView;

    private boolean isBlinging = false;

    public MainPresenter(IMainContract.IView iView) {
        mHandler = new Handler(this);
        mView = iView;
    }

    @Override
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
            case LEVEL_3:
                mDuration = LEVEL_3_DURATION;
                break;
            case LEVEL_4:
                mDuration = LEVEL_4_DURATION;
                break;
            case LEVEL_5:
                mDuration = LEVEL_5_DURATION;
                break;
            case LEVEL_6:
                mDuration = LEVEL_6_DURATION;
                break;
            case LEVEL_7:
                mDuration = LEVEL_7_DURATION;
                break;
            case LEVEL_8:
                mDuration = LEVEL_8_DURATION;
                break;
            case LEVEL_9:
                mDuration = LEVEL_9_DURATION;
                break;
            default:
                mDuration = LEVEL_0_DURATION;
                break;
        }
        if(isBlinging) {
            if(canFlash()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startBling();
                    }
                }).start();
            } else {
                startBling();
            }
        }
    }

    private void startBling() {
        if(mHandler.hasMessages(HANDLER_MSG_BLING)) {
            mHandler.removeMessages(HANDLER_MSG_BLING);
        }
        isBlinging = true;
        if(LEVEL_0_DURATION == mDuration) {
            switchOn();
            return;
        }
        bling();
    }

    private void switchOn() {
        if(canFlash()) {
            if (null != mFlash) {
                if (!mFlash.isOn()) {
                    mFlash.turnOn();
                }
            }
        } else {
            mView.onWhiteScreenOn();
        }
    }

    private void switchOff() {
        if(mHandler.hasMessages(HANDLER_MSG_BLING)) {
            mHandler.removeMessages(HANDLER_MSG_BLING);
        }
        if(canFlash()) {
            if (null != mFlash) {
                if (mFlash.isOn()) {
                    mFlash.turnOff();
                }
            }
        } else {
            mView.onWhiteScreenOff();
        }
    }

    private void bling() {
        if(LEVEL_0_DURATION == mDuration) {
            switchOn();
            return;
        }
        mHandler.sendEmptyMessageDelayed(HANDLER_MSG_BLING, mDuration);
        switchOnOff();
    }

    private void switchOnOff() {
        if(canFlash()) {
            if (null != mFlash) {
                if (mFlash.isOn()) {
                    mFlash.turnOff();
                } else {
                    mFlash.turnOn();
                }
            }
        } else {
            if(mView.isOn()) {
                mView.onWhiteScreenOff();
            } else {
                mView.onWhiteScreenOn();
            }
        }
    }

    @Override
    public void blingOnOff(boolean on) {
        if(on) {
            startBling();
        } else {
            stopBling();
        }
    }

    private void stopBling() {
        isBlinging = false;
        switchOff();
        mView.onStopBling();
    }

    @Override
    public boolean isBlinging() {
        return isBlinging;
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

    private boolean canFlash() {
        return mFlash.isSupport() && PerUtil.hasCameraPermission();
    }

    private void onDestroy() {
        mDuration = LEVEL_0_DURATION;
        stopBling();
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
