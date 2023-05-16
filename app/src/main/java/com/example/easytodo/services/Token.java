package com.example.easytodo.services;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.example.easytodo.R;
import com.example.easytodo.utils.H;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;

import retrofit2.Call;

public class Token {
    public static String access;
    public static String refresh;

    public static void refreshToken(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        UserAPI userAPI = GenAPIS.getAPI(UserAPI.class, false);
        Map<String, String> body = Map.of("refresh", prefs.getString("refresh", ""));
        Call<Map<String, String>> tokenCall = userAPI.refreshToken(body);

        H.enqueueReq(tokenCall, (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show();
                Map<String, String> token = response.body();
                try {
                    prefs.edit().putString("access", token.get("access")).apply();
                    prefs.edit().putString("refresh", token.get("refresh")).apply();
                    Token.access = token.get("access");
                    Token.refresh = token.get("refresh");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (response.code() == 400 && response.errorBody() != null) {
                    try {
                        JsonObject error = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                        System.out.println(error);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                prefs.edit().putString("access", "").apply();
                prefs.edit().putString("refresh", "").apply();
                Token.access = "";
                Token.refresh = "";
                Navigation.findNavController((Activity) context, R.id.fragment_container).navigate(R.id.nav_login_form);
            }
        });
    }

    public static String refreshToken() {
        UserAPI userAPI = GenAPIS.getAPI(UserAPI.class, false);
        Map<String, String> body = Map.of("refresh", Token.refresh);
        Call<Map<String, String>> tokenCall = userAPI.refreshToken(body);

        try {
            Map<String, String> token = tokenCall.execute().body();
            if (token != null) {
                Token.access = token.get("access");
                Token.refresh = token.get("refresh");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Token.access;
    }
}
