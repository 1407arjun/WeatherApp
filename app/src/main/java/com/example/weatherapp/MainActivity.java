package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.content.Context;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

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
import java.util.concurrent.ExecutionException;

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
    String loc = "", icon = "", tempC = "", tempF = "", message = "";
    int time = 0;
    boolean isOpen = false;

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                String result = "";
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }catch (MalformedURLException e) {
                Log.i("Error", "102");
                return "0";
                //e.printStackTrace();
            } catch (IOException e) {
                Log.i("Error", "103");
                return "1";
                //e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (result.equals("0")){
                textInputLayout.setError("Invalid location, Error 102");
                resultTextView.setText("");
            }else if (result.equals("1")){
                textInputLayout.setError("Invalid location, Error 103");
                resultTextView.setText("");
            }else{
                Log.i("Weather", result);
                textInputLayout.setError(null);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject current = new JSONObject(jsonObject.getString("current"));
                    JSONObject condition = new JSONObject(current.getString("condition"));
                    JSONObject location = new JSONObject(jsonObject.getString("location"));
                    message = condition.getString("text");
                    icon = condition.getString("icon");
                    String name = location.getString("name"), region = location.getString("region"), country = location.getString("country");
                    if (region.equals("")) {
                        loc = name + ", " + country;
                    }else{
                        loc = name +", " + region + ", " + country;
                    }
                    time = Integer.parseInt(location.getString("localtime").split(" ")[1].split(":")[0]);
                    tempC = current.getString("temp_c") + "°";
                    tempF = current.getString("temp_f") + "°";
                    weatherSetter();
                } catch (JSONException e) {
                    Log.i("Error", "104");
                    textInputLayout.setError("Could not retrieve weather info. Try again, Error 104");
                    resultTextView.setText("");
                    //e.printStackTrace();
                }
            }
        }
    }

    public void findWeather(View view){
        DownloadTask task = new DownloadTask();
        String urlEncoder;
        try {
            urlEncoder = URLEncoder.encode(editText.getText().toString(), "UTF-8");
            task.execute("https://api.weatherapi.com/v1/current.json?key=ee1e6fc97eae412a8f3125336211202&q=" + urlEncoder);
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

        unitSwitch = findViewById(R.id.unitSwitch);
        unitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                editText.clearFocus();
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

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (MalformedURLException e) {
                Log.i("Error", "105");
                //e.printStackTrace();
            } catch (IOException e) {
                Log.i("Error", "106");
                //e.printStackTrace();
            }
            return null;
        }
    }

    public void weatherSetter(){
        exitButton.setVisibility(View.VISIBLE);
        if (message != "" &&  icon != ""){
            manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            ImageDownloader imageDownloader = new ImageDownloader();
            Bitmap weatherIcon;
            try {
                weatherIcon = imageDownloader.execute("https:" + icon).get();
                iconImageView.setImageBitmap(weatherIcon);
            } catch (ExecutionException e) {
                Log.i("Error", "107");
                //e.printStackTrace();
            } catch (InterruptedException e) {
                Log.i("Error", "108");
                //e.printStackTrace();
            }
            if (unitSwitch.isChecked()){
                tempTextView.setText(tempF);
            }else{
                tempTextView.setText(tempC);
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
            int y = linearLayout.getBottom();

            int startRadius = 0;
            int endRadius = (int) Math.hypot(constraintLayout.getWidth(), constraintLayout.getHeight());

            Animator anim = ViewAnimationUtils.createCircularReveal(weatherLayout, x, y, startRadius, endRadius);
            weatherLayout.setVisibility(View.VISIBLE);
            anim.setDuration(600);
            isOpen = true;
            anim.start();
        }
    }
    public void close(View view){
        if (isOpen){
            int x = linearLayout.getWidth()/2;
            int y = linearLayout.getBottom();

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
}