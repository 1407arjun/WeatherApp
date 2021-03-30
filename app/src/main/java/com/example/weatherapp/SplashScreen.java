package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(com.example.weatherapp.SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1750);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout titleLayout = findViewById(R.id.titleLayout);
        ImageView mainIcon = findViewById(R.id.mainIcon);

        mainIcon.animate().translationYBy(-200f).setStartDelay(500).setDuration(400);
        titleLayout.animate().translationYBy(180f).setStartDelay(500).setDuration(400);
        titleLayout.animate().alpha(1f).setStartDelay(700).setDuration(200);
    }
}