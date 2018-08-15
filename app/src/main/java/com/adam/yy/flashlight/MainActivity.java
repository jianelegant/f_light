package com.adam.yy.flashlight;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton mSwitch;
    SwitchCompat mAutoOn;

    MainPresenter mMainPresenter = new MainPresenter();

    WheelView<String> mWheelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLifecycle().addObserver(mMainPresenter);

        loadAd();

        mWheelView = findViewById(R.id.id_wheel);
        mWheelView.setWheelAdapter(new ArrayWheelAdapter(this));
        mWheelView.setSkin(WheelView.Skin.Common);
        mWheelView.setWheelData(Arrays.asList(MainPresenter.LEVELS));
        mWheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int position, String s) {
                setLevel(position);
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
