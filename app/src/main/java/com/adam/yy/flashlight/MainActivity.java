package com.adam.yy.flashlight;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton mSwitch;

    Flash mFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFlash = new Flash();

        mSwitch = findViewById(R.id.id_fab);
        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mFlash.isSupport()) {
                    Snackbar.make(view, R.string.not_support, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(mFlash.isOn()) {
                    mFlash.turnOff();
                } else {
                    mFlash.turnOn();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFlash.release();
    }
}
