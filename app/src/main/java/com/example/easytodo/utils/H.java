package com.example.easytodo.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class H {
    public interface SimpleAlertCallback {
        public void onOk();
    }

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

    public static void showAlert(Context context, String title, String message, SimpleAlertCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            callback.onOk();
        });
        builder.show();
    }

}
