package com.yasin.measuredemo;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.yasin.measuredemo.view.MeasureView;

public class MainActivity extends AppCompatActivity {
    private MeasureView mMeasureView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMeasureView= (MeasureView) findViewById(R.id.id_measure_view);
        startAni();
    }

    private void startAni() {
        ObjectAnimator a=ObjectAnimator.ofFloat(mMeasureView,"progress",0f,1f);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.setDuration(3000);
        a.setRepeatCount(ValueAnimator.INFINITE);
        a.setRepeatMode(ValueAnimator.REVERSE);
        a.start();
    }
}
