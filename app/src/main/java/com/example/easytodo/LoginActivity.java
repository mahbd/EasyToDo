package com.example.easytodo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easytodo.databinding.ActivityLoginBinding;
import com.example.easytodo.services.GenAPIS;
import com.example.easytodo.services.Token;
import com.example.easytodo.services.UserAPI;
import com.example.easytodo.utils.H;
import com.example.easytodo.utils.SyncHandler;

import java.util.Map;

import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnRegister = findViewById(R.id.btn_register);
        EditText etUsername = findViewById(R.id.et_username);
        EditText etPassword = findViewById(R.id.et_password);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
        }

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });



        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            UserAPI userAPI = GenAPIS.getAPI(UserAPI.class, false);
            Map<String, String> body = Map.of("username", username, "password", password);
            Call<Map<String, String>> tokenCall = userAPI.getToken(body);
            H.enqueueReq(tokenCall, (call, response) -> {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show();
                    Map<String, String> token = response.body();
                    try {
                        prefs.edit().putString("access", token.get("access")).apply();
                        prefs.edit().putString("refresh", token.get("refresh")).apply();
                        Token.access = token.get("access");
                        Token.refresh = token.get("refresh");
                        SyncHandler syncHandler = new SyncHandler(this);
                        syncHandler.sync();

                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    etPassword.setError("Invalid Credentials");
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}