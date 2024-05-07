package com.example.easytodo.utils;

import android.content.Context;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class H {
    public interface SimpleAlertCallback {
        void onOk();
    }

    public static String currentUTCISO8601() {
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

    public static void showAlert(Context context, String title, String message, SimpleAlertCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", (dialog, which) -> callback.onOk());
        builder.show();
    }

    public static void runDelay(Function<Void, Boolean> func, int delay) {
        Handler handler = new Handler();
        Runnable runnable = () -> func.apply(null);
        handler.postDelayed(runnable, delay);
    }

}
