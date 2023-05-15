package com.example.easytodo.services;


import androidx.annotation.Nullable;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {
    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, Response response) {
        String newAccessToken = Token.refreshToken();
        return response.request().newBuilder()
                .header("Authorization", "Bearer " + newAccessToken)
                .build();
    }
}
