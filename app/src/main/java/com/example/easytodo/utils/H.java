package com.example.easytodo.utils;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class H {
    public interface EnqueueReq<T> {
        void onResponse(@NonNull Call<T> call, @NonNull Response<T> response);
    }

    public static <T> void enqueueReq(Call<T> call, EnqueueReq<T> onResponse) {
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                onResponse.onResponse(call, response);
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                // TODO: Handle this
            }
        });
    }

    public static String currentUTCISO8601() {
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

}
