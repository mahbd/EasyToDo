package com.example.easytodo.services;

import com.example.easytodo.models.Token;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class GenAPIS {
    public static final String BASE_URL = "http://10.0.2.2:8000/api/";
    static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static OkHttpClient.Builder getHttpClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new AuthInterceptor(Token.access));
        return httpClient;
    }

    public static UserAPI getUserAPI() {
        return retrofit.create(UserAPI.class);
    }

    public static ChangeAPI getChangeAPI() {
        Retrofit auth = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getHttpClient().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return auth.create(ChangeAPI.class);
    }

    public static TagAPI getTagAPI() {
        Retrofit auth = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getHttpClient().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return auth.create(TagAPI.class);
    }
}
