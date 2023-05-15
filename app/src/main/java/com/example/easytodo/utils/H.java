package com.example.easytodo.utils;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class H {
    public static final String BASE_URL = "http://10.0.2.2:8000/api/";
    public static final String WS_URL = "ws://10.0.2.2:8000/ws/core/";
    public interface SimpleAlertCallback {
        void onOk();
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
        builder.setPositiveButton("Ok", (dialog, which) -> callback.onOk());
        builder.show();
    }

    public static void runDelay(Function<Void, Boolean> func, int delay) {
        Handler handler = new Handler();
        Runnable runnable = () -> func.apply(null);
        handler.postDelayed(runnable, delay);
    }

    public static WebSocket createWebSocket(String token, Function<String, Boolean> onMessage) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(WS_URL)
                .build();

        return client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
                webSocket.send("{\"access_token\": \"" + token + "\"}");
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                onMessage.apply(text);
            }
        });
    }

}
