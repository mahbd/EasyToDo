package com.example.easytodo.utils;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class H {
    public interface EnqueueReq<T> {
        void onResponse(@NonNull Call<T> call, @NonNull Response<T> response);
    }

    public static <T> void enqueueReq(Call<T> call, EnqueueReq<T> onResponse) {
        call.enqueue(new Callback<T>() {
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

}
