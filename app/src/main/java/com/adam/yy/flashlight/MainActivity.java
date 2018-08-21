package com.adam.yy.flashlight;

import android.app.Activity;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements IMainContract.IView{

    private static final int HANDLER_MSG_CHECK_USE_SCREEN_STATUS = 1;

    FloatingActionButton mSwitch;
    SwitchCompat mAutoOn;
    SwitchCompat mUseScreen;
    Handler mHandler;

    MainPresenter mMainPresenter = new MainPresenter(this);

    AppCompatSeekBar mLevel;

    View mFullWhite;
    boolean mIsOn = false;
    float mOldBrightness;

    private InterstitialAd mInterstitialAd;
    int failRetryTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        mOldBrightness = getWindow().getAttributes().screenBrightness;

        getLifecycle().addObserver(mMainPresenter);

        loadAd();

        mLevel = findViewById(R.id.id_bling_level);
        mLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int level, boolean fromUser) {
                setLevel(level);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSwitch = findViewById(R.id.id_fab);
        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabBtn();
            }
        });

        mFullWhite = findViewById(R.id.id_full_white);

        mHandler = new Handler(this);
        initUseScreen();
    }

    private void initUseScreen() {
        mUseScreen = findViewById(R.id.id_use_screen);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!mMainPresenter.canFlash()) {
                    Util.setUseScreen(true);
                }
                mHandler.sendEmptyMessage(HANDLER_MSG_CHECK_USE_SCREEN_STATUS);

            }
        }).start();

        mUseScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean use) {
                if(!use && !mMainPresenter.canFlash()) {
                    Util.toast("Can only use screen, not support camera flash");
                    mUseScreen.setChecked(true);
                    return;
                }
                if(mMainPresenter.isBlinging()) {
                    blingOff();
                    Util.setUseScreen(use);
                    blingOn();
                } else {
                    Util.setUseScreen(use);
                }
            }
        });
    }

    private void initAutoOn() {
        mAutoOn = findViewById(R.id.id_auto_on);
        if(Util.getAutoOn()) {
            mAutoOn.setChecked(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    blingOn();
                }
            }).start();
        } else {
            mAutoOn.setChecked(false);
        }
        mAutoOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                Util.setAutoOn(check);
            }
        });
    }

    private void blingOn() {
        mMainPresenter.blingOnOff(true);
        mSwitch.setImageResource(R.drawable.light_on);
    }

    private void blingOff() {
        mMainPresenter.blingOnOff(false);
        mSwitch.setImageResource(R.drawable.light_off);
    }

    private void setLevel(int level) {
        mMainPresenter.setLevel(level);
    }

    private void onFabBtn() {
        if(mMainPresenter.isBlinging()) {
            blingOff();
            showInterstitialAd();
        } else {
            blingOn();
        }
    }

    private void showInterstitialAd() {
        if(null != mInterstitialAd && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private void loadAd() {
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        failRetryTimes = 0;
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(int i) {
                if(!MainActivity.this.isFinishing() && failRetryTimes < 3) {
                    if(null != mInterstitialAd) {
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        failRetryTimes++;
                    }
                }
            }
        });
    }

    @Override
    public void onWhiteScreenOn() {
        mIsOn = true;
        mFullWhite.setVisibility(View.VISIBLE);
        mFullWhite.setBackgroundColor(getResources().getColor(android.R.color.white));
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 1F;
        getWindow().setAttributes(layout);
    }

    @Override
    public void onWhiteScreenOff() {
        mIsOn = false;
        mFullWhite.setVisibility(View.VISIBLE);
        mFullWhite.setBackgroundColor(getResources().getColor(android.R.color.black));
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 0F;
        getWindow().setAttributes(layout);
    }

    @Override
    public void onStopBling() {
        mIsOn = false;
        mFullWhite.setVisibility(View.GONE);
        if(Util.getUseScreen()) {
            WindowManager.LayoutParams layout = getWindow().getAttributes();
            layout.screenBrightness = mOldBrightness;
            getWindow().setAttributes(layout);
        }
    }

    private void checkUseScreenStatus() {
        if(Util.getUseScreen()) {
            mUseScreen.setChecked(true);
        } else {
            mUseScreen.setChecked(false);
        }
        initAutoOn();
    }

    @Override
    public boolean isOn() {
        return mIsOn;
    }

    static class Handler extends android.os.Handler {


        private WeakReference<MainActivity> weakActivity;

        public Handler(MainActivity activity) {
            weakActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MSG_CHECK_USE_SCREEN_STATUS:
                    if(null != weakActivity.get()) {
                        weakActivity.get().checkUseScreenStatus();
                    }
                    break;
                default:
                    break;
            }
        }

    }
}
