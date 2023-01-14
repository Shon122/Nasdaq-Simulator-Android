package com.gtappdevelopers.bankrehovot;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.widget.TextView;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);
//
//        TextView movingText = new TextView(this);
//        movingText.setText(MainActivity.news);
//        movingText.setX(0);
//        movingText.setY(0);
//
//        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
//        animator.setDuration(1000);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float value = (float) animation.getAnimatedValue();
//                movingText.setX(value * 100);
//                movingText.setY(value * 100);
//            }
//        });
//        animator.start();


    }
}