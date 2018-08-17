package com.adam.yy.flashlight;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity implements IMainContract.IView{

    FloatingActionButton mSwitch;
    SwitchCompat mAutoOn;
    SwitchCompat mUseScreen;

    MainPresenter mMainPresenter = new MainPresenter(this);

    AppCompatSeekBar mLevel;

    View mFullWhite;
    boolean mIsOn = false;
    float mOldBrightness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

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

        initAutoOn();
        initUseScreen();
    }

    private void initUseScreen() {
        mUseScreen = findViewById(R.id.id_use_screen);
        if(!mMainPresenter.canFlash()) {
            Util.setUseScreen(true);
        }
        if(Util.getUseScreen()) {
            mUseScreen.setChecked(true);
        } else {
            mUseScreen.setChecked(false);
        }

        mUseScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean use) {
                if(!use && !mMainPresenter.canFlash()) {
                    Util.toast("Can only use screen, not support camera flash");
                    return;
                }
                Util.setUseScreen(use);
            }
        });
    }

    private void initAutoOn() {
        mAutoOn = findViewById(R.id.id_auto_on);
        if(Util.getAutoOn()) {
            mAutoOn.setChecked(true);
            blingOn();
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

    private void setLevel(int level) {
        mMainPresenter.setLevel(level);
    }

    private void onFabBtn() {
        if(mMainPresenter.isBlinging()) {
            mMainPresenter.blingOnOff(false);
            mSwitch.setImageResource(R.drawable.light_off);
        } else {
            onStartBling();
            mMainPresenter.blingOnOff(true);
            mSwitch.setImageResource(R.drawable.light_on);
        }
    }

    private void loadAd() {
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
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
    public void onStartBling() {
        if(Util.getUseScreen()) {
            mOldBrightness = getWindow().getAttributes().screenBrightness;
        }
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

    @Override
    public boolean isOn() {
        return mIsOn;
    }
}
