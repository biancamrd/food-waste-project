package com.example.licenta.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CookieInterceptor implements Interceptor {
    private SharedPreferences sharedPreferences;

    public CookieInterceptor(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);


        List<String> cookies = response.headers("Set-Cookie");
        if (cookies != null) {

            for (String cookie : cookies) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("cookie", cookie);
                editor.apply();
            }
        }

        return response;
    }
}


