package com.gtappdevelopers.bankrehovot;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class HomePage extends AppCompatActivity {
    private TextView movingTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);
        MainActivity.backToUsers=0;
        MainActivity.acceptedPerms=true;
//
//        movingTextView = findViewById(R.id.moving_text);
//        String longString = MainActivity.news;
//        displayMovingText(longString);
    }

    private void displayMovingText(String text) {
//        if (text.length() > 50) {
        movingTextView.setSelected(true);
        movingTextView.setText(text);
//        } else {
//            movingTextView.setText("News are currently unavailable...");
//            TranslateAnimation animation = new TranslateAnimation(-1000, 1000, 0, 0);
//            animation.setDuration(5000);
//            animation.setRepeatCount(Animation.INFINITE);
//            animation.setRepeatMode(Animation.RESTART);
//            movingTextView.startAnimation(animation);
//        }
    }

    public void moveToInvest(View view) {

        Intent intent = new Intent(this, InvestActivity.class);


        startActivity(intent);
        finish();
    }

    public void moveToTrades(View view) {

        Intent intent = new Intent(this, TradesActivity.class);


        startActivity(intent);
        finish();
    }

    public void moveToUserList(View view) {

        Intent intent = new Intent(this, UserList.class);


        startActivity(intent);
        finish();
    }

    public void moveToStockGame(View view) {

        Intent intent = new Intent(this, StockGame.class);


        startActivity(intent);
        finish();
    }

    public void moveToMyProfile(View view) {
        MainActivity.viewingUser = MainActivity.currentUser;
        Intent intent = new Intent(this, MyProfile.class);


        startActivity(intent);
        finish();
    }

    @Override //when user wants to go back
    public void onBackPressed() {
        // Handle back button press event here

    }


}