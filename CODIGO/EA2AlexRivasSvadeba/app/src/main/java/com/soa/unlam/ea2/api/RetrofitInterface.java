package com.soa.unlam.ea2.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.soa.unlam.ea2.models.Token;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RetrofitInterface {
        @POST("api/register")
        @Headers({"content-type: application/json"})
        Call<RegistrarResponse> register(@Body RegistrarRequest request);

        @POST("api/login")
        @Headers({"content-type: application/json"})
        Call<LoginResponse> login(@Body LoginRequest request);

        @POST("api/event")
        @Headers({"Content-Type: application/json"})
        Call<EventResponse> regEvent (@Body EventRequest request, @Header("Authorization") String token_api);


        //@GET y trae el token
}