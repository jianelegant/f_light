package com.adam.yy.flashlight;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

    private static final int HANDLER_MSG_CHECK_USE_FLASH_STATUS = 1;
    private static final int HANDLER_MSG_INIT_AUTO_ON = 2;
    private static final int REQUEST_CODE = 1988;

    FloatingActionButton mSwitch;
    SwitchCompat mAutoOn;
    SwitchCompat mUseFlash;
    Handler mHandler;

    MainPresenter mMainPresenter = new MainPresenter(this);

    AppCompatSeekBar mLevel;

    View mFullWhite;
    boolean mIsOn = false;
    float mOldBrightness;

    private InterstitialAd mInterstitialAd;
    int failRetryTimes = 0;

    private AlertDialog mPermiDenyDialog;

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
        initUseFlashBySP();
        initUseFlash();
    }

    private void initUseFlashBySP() {
        mUseFlash = findViewById(R.id.id_use_flash);
        mAutoOn = findViewById(R.id.id_auto_on);
        checkUseFlashStatus();
        initAutoOnBySp();
    }

    private void initAutoOnBySp() {
        if(Util.getAutoOn()) {
            mAutoOn.setChecked(true);
        } else {
            mAutoOn.setChecked(false);
        }
    }

    private void initUseFlash() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!mMainPresenter.canFlash()) {
                    Util.setUseFlash(false);
                }
                mHandler.sendEmptyMessage(HANDLER_MSG_CHECK_USE_FLASH_STATUS);
                mHandler.sendEmptyMessage(HANDLER_MSG_INIT_AUTO_ON);

            }
        }).start();

        mUseFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean use) {
                if(use && mMainPresenter.isNoPermission()) {
                    mUseFlash.setChecked(false);
                    requestCameraPremission();
                    return;
                }
                onUseScreenCheckChange(use);
            }
        });
    }

    private void onUseScreenCheckChange(boolean use) {
        if(!use && !mMainPresenter.canFlash()) {
            Util.toast("Can not use Camera flash, not support");
            mUseFlash.setChecked(false);
            return;
        }
        if(mMainPresenter.isBlinging()) {
            blingOff();
            Util.setUseFlash(use);
            blingOn();
        } else {
            Util.setUseFlash(use);
        }
    }

    private void onPermissionGranted() {
        if(!mMainPresenter.canFlash()) {
            Util.toast("Can only use screen, not support camera flash");
            mUseFlash.setChecked(false);
            return;
        }
        blingOff();
        Util.setUseFlash(true);
        mUseFlash.setChecked(true);
        blingOn();
    }

    private void requestCameraPremission() {
        PerUtil.requestCameraPremission(this, REQUEST_CODE);
    }

    private void initAutoOn() {
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
    @MainThread
    public void onWhiteScreenOn() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIsOn = true;
                mFullWhite.setVisibility(View.VISIBLE);
                mFullWhite.setBackgroundColor(getResources().getColor(android.R.color.white));
                WindowManager.LayoutParams layout = getWindow().getAttributes();
                layout.screenBrightness = 1F;
                getWindow().setAttributes(layout);
            }
        });
    }

    @Override
    @MainThread
    public void onWhiteScreenOff() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIsOn = false;
                mFullWhite.setVisibility(View.VISIBLE);
                mFullWhite.setBackgroundColor(getResources().getColor(android.R.color.black));
                WindowManager.LayoutParams layout = getWindow().getAttributes();
                layout.screenBrightness = 0F;
                getWindow().setAttributes(layout);
            }
        });
    }

    @Override
    @MainThread
    public void onStopBling() {
        mIsOn = false;
        mFullWhite.setVisibility(View.GONE);
        if(!Util.getUseFlash()) {
            WindowManager.LayoutParams layout = getWindow().getAttributes();
            layout.screenBrightness = mOldBrightness;
            getWindow().setAttributes(layout);
        }
    }

    @MainThread
    private void checkUseFlashStatus() {
        if(Util.getUseFlash()) {
            mUseFlash.setChecked(true);
        } else {
            mUseFlash.setChecked(false);
        }
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
                case HANDLER_MSG_CHECK_USE_FLASH_STATUS:
                    if(null != weakActivity.get()) {
                        weakActivity.get().checkUseFlashStatus();
                    }
                    break;
                case HANDLER_MSG_INIT_AUTO_ON:
                    if(null != weakActivity.get()) {
                        weakActivity.get().initAutoOn();
                    }
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(REQUEST_CODE == requestCode) {
            if(permissions.length > 0) {
                if(PerUtil.CAMERA_PERMISSION.equals(permissions[0]) && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                    onPermissionGranted();
                } else {
                    Util.toast("Permission denied");
                    showDenyDialog();
                }
            }
        }
    }

    private void showDenyDialog() {
        if(null == mPermiDenyDialog) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage(R.string.deny_msg)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.go_app_info, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            goAppinfoPage();
                        }
                    });
            mPermiDenyDialog = builder.create();
        }
        mPermiDenyDialog.show();
    }

    private void goAppinfoPage() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
