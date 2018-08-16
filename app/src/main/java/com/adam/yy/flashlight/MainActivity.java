package com.adam.yy.flashlight;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton mSwitch;
    SwitchCompat mAutoOn;

    MainPresenter mMainPresenter = new MainPresenter();

    AppCompatSeekBar mLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        initAutoOn();
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
            mMainPresenter.blingOnOff(true);
            mSwitch.setImageResource(R.drawable.light_on);
        }
    }

    private void loadAd() {
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
