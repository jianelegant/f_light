package com.adam.yy.flashlight;

public interface IMainContract {

    interface IPresenter {
        void setLevel(int level);
        void blingOnOff(boolean on);
        boolean isBlinging();
    }

    interface IView {
        void onWhiteScreenOn();
        void onWhiteScreenOff();
        void onStartBling();
        void onStopBling();
        boolean isOn();
    }
}
