package com.example.weatherapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {

    Button button;
    EditText editText;
    TextInputLayout textInputLayout;
    TextView resultTextView;
    ImageView iconImageView;
    TextView locTextView;
    TextView tempTextView;
    ConstraintLayout constraintLayout;
    LinearLayout linearLayout;
    LinearLayout weatherLayout;
    SwitchMaterial unitSwitch;
    InputMethodManager manager;
    FloatingActionButton exitButton;
    boolean isOpen = false;

    public void findWeather(View view){
        try {
            String urlEncoder = URLEncoder.encode(editText.getText().toString(), "UTF-8");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            API myApi = retrofit.create(API.class);
            Call<JSONProcess> call = myApi.getWeather("ee1e6fc97eae412a8f3125336211202", urlEncoder);
            call.enqueue(new Callback<JSONProcess>() {
                @Override
                public void onResponse(Call<JSONProcess> call, Response<JSONProcess> response) {
                    textInputLayout.setError(null);
                    JSONProcess weatherInfo = response.body();
                    assert weatherInfo != null;
                    weatherSetter(weatherInfo.getCurrent().getTempC(), weatherInfo.getCurrent().getTempF(), weatherInfo.getCurrent().getCondition().getIcon(), weatherInfo.getCurrent().getCondition().getText(), weatherInfo.getLocation().getName(), weatherInfo.getLocation().getRegion(), weatherInfo.getLocation().getCountry(), weatherInfo.getLocation().getLocaltime());
                    Log.i("info", weatherInfo.getLocation().getName());
                }
                @Override
                public void onFailure(Call<JSONProcess> call, Throwable t) {
                    textInputLayout.setError("Invalid location, Error 102");
                    Log.i("error", t.getMessage());
                }
            });
        }catch (UnsupportedEncodingException e) {
            Log.i("Error", "101");
            textInputLayout.setError("Invalid Location, Error 101");
            resultTextView.setText("");
            //e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInputLayout = findViewById(R.id.filledTextField);
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.goButton);
        button.setEnabled(false);
        resultTextView = findViewById(R.id.resultTextView);
        locTextView = findViewById(R.id.locTextView);
        tempTextView = findViewById(R.id.tempTextView);
        iconImageView = findViewById(R.id.iconImageView);
        constraintLayout = findViewById(R.id.constriantLayout);
        linearLayout = findViewById(R.id.linearLayout);
        weatherLayout = findViewById(R.id.weatherLayout);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        exitButton = findViewById(R.id.floatingActionButton);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                editText.clearFocus();
            }
        });

        unitSwitch = findViewById(R.id.unitSwitch);
        unitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                //editText.clearFocus();
                if (isChecked){
                    unitSwitch.setText("Unit °F ");
                }else{
                    unitSwitch.setText("Unit °C ");
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()){
                    textInputLayout.setError("Location cannot be empty");
                    button.setEnabled(false);
                }else{
                    button.setEnabled(true);
                }
            }
        });
    }

    public void weatherSetter(Double tempC, Double tempF, String icon, String message, String name, String region, String country, String timestr){
        if (message != "" &&  icon != ""){
            editText.setEnabled(false);
            manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            Picasso.with(MainActivity.this).load("https:" + icon).into(iconImageView);
            String loc;
            if (region.equals("")) {
                loc = name + ", " + country;
            }else{
                loc = name +", " + region + ", " + country;
            }
            int time = Integer.parseInt(timestr.split(" ")[1].split(":")[0]);
            if (unitSwitch.isChecked()){
                tempTextView.setText(tempF + "°");
            }else{
                tempTextView.setText(tempC+ "°");
            }
            resultTextView.setText(message);
            locTextView.setText(loc);
            editText.clearFocus();
            if (time >= 6 && time < 17) {
                weatherLayout.setBackgroundResource(R.drawable.sun);
            }else if (time >= 17 && time < 20){
                weatherLayout.setBackgroundResource(R.drawable.moon);
            }else{
                weatherLayout.setBackgroundResource(R.drawable.blood);
            }

            int x = linearLayout.getWidth()/2;
            int y = linearLayout.getBottom() - 20;

            int startRadius = 0;
            int endRadius = (int) Math.hypot(constraintLayout.getWidth(), constraintLayout.getHeight());

            Animator anim = ViewAnimationUtils.createCircularReveal(weatherLayout, x, y, startRadius, endRadius);
            weatherLayout.setVisibility(View.VISIBLE);
            anim.setDuration(500);
            isOpen = true;
            anim.start();
            exitButton.setVisibility(View.VISIBLE);
        }
    }
    public void close(View view){
        if (isOpen){
            editText.setEnabled(true);
            int x = linearLayout.getWidth()/2;
            int y = linearLayout.getBottom() - 20;

            int endRadius = 0;
            int startRadius = (int) Math.hypot(constraintLayout.getWidth(), constraintLayout.getHeight());

            Animator anim = ViewAnimationUtils.createCircularReveal(weatherLayout, x, y, startRadius, endRadius);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    weatherLayout.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            anim.setDuration(500);
            isOpen = false;
            anim.start();
            exitButton.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onBackPressed()
    {
        if (isOpen){
            View view = findViewById(R.id.floatingActionButton);
            close(view);
        }else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Exit Application?");
            alertDialogBuilder
                    .setMessage("Leaving so early :(, see you soon!")
                    .setCancelable(true)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    moveTaskToBack(true);
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(1);
                                }
                            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}