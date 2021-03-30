package com.example.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {

    String BASE_URL = "https://api.weatherapi.com/v1/";
    @GET("current.json?")

    Call<JSONProcess> getWeather(@Query("key") String key, @Query("q") String loc);
}
