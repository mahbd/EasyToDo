package com.example.easytodo.services;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final String authToken;

    public AuthInterceptor(String token) {
        this.authToken = token;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + authToken);
        Request newRequest = builder.build();
        return chain.proceed(newRequest);
    }
}
