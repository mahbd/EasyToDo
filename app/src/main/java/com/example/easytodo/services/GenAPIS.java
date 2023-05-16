package com.example.easytodo.services;

import static com.example.easytodo.utils.H.BASE_URL;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class GenAPIS {
    public static OkHttpClient.Builder getHttpClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new AuthInterceptor(Token.access))
                .authenticator(new TokenAuthenticator());
        return httpClient;
    }

    public static <T> T getAPI(Class<T> api, boolean auth) {
        if (auth) {
            return new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getHttpClient().build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(api);
        } else {
            return new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(api);
        }
    }

    public static <T> T getAPI(Class<T> api) {
        return getAPI(api, true);
    }
}
