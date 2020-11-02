package com.soa.unlam.ea2.api;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.soa.unlam.R;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventRetrofit {

    //Pasar como parámetro el token
        private static final String TOKEN_EVENT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MDY3NTE5MjYsInR5cGUiOiJpbmljaWFsIiwidXNlciI6eyJlbWFpbCI6ImFhQGEiLCJkbmkiOjIxMzIxfX0.s0xbCGiU3hngbcLe23W7OwoAymiXg11miVWnOfHPMzc";
        //private static final String BASE_URL = "https://files.000webhost.com/Last/Last/v1";     ///Last/Last/v1/registerUser.php
        private static EventRetrofit mInstance;
        private Retrofit retrofit;

        private EventRetrofit() {

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(
                            new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request original = chain.request();

                                    Request.Builder requestBuilder = original.newBuilder()
                                            .addHeader("token_api",TOKEN_EVENT)
                                            .method(original.method(), original.body());

                                    Request request = requestBuilder.build();
                                    return chain.proceed(request);
                                }
                            }
                    ).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://so-unlam.net.ar/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }

        public static synchronized EventRetrofit getInstance() {
            if (mInstance == null) {
                mInstance = new EventRetrofit();
            }
            return mInstance;
        }

        public RetrofitInterface getApi() {return retrofit.create(RetrofitInterface.class);}
}

