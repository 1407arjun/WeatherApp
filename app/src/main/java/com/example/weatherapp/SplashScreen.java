package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity{

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView imageView = findViewById(R.id.imageView);
                ImageView imageView3 = findViewById(R.id.imageView3);
                LinearLayout linearLayout = findViewById(R.id.layout);

                imageView.animate().translationYBy(50f).setStartDelay(100).setDuration(500);
                imageView3.animate().alpha(0f).setDuration(100);
                linearLayout.animate().translationYBy(-100f).setStartDelay(100).setDuration(500);
                Intent intent=new Intent(com.example.weatherapp.SplashScreen.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },5000);

    }
}
