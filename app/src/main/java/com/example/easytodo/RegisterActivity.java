package com.example.easytodo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easytodo.models.User;
import com.example.easytodo.services.GenAPIS;
import com.example.easytodo.services.UserAPI;
import com.example.easytodo.utils.FormHandler;
import com.example.easytodo.utils.H;

import java.util.Map;
import java.util.Objects;

import retrofit2.Call;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnRegRegister = findViewById(R.id.btn_reg_register);
        Button btnRegLogin = findViewById(R.id.btn_reg_login);
        EditText etRegUsername = findViewById(R.id.et_reg_username);
        EditText etRegEmail = findViewById(R.id.et_reg_email);
        EditText etRegFirstName = findViewById(R.id.et_reg_first_name);
        EditText etRegLastName = findViewById(R.id.et_reg_last_name);
        EditText etRegPassword = findViewById(R.id.et_reg_password);
        EditText etRegPassword2 = findViewById(R.id.et_reg_password2);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
        }

        FormHandler.Field[] fields = new FormHandler.Field[]{
                new FormHandler.Field(etRegUsername, "text", "username"),
                new FormHandler.Field(etRegEmail, "email", "email"),
                new FormHandler.Field(etRegFirstName, "text", "first_name"),
                new FormHandler.Field(etRegLastName, "text", "last_name"),
                new FormHandler.Field(etRegPassword, "password", "password"),
        };
        FormHandler<User> formHandler = new FormHandler<>(fields);

        btnRegRegister.setOnClickListener(v -> {
            Map<String, String> body = formHandler.getFormData();
            String confirm_password = etRegPassword2.getText().toString();

            if (!Objects.equals(body.get("password"), confirm_password)) {
                etRegPassword2.setError("Passwords do not match");
                return;
            }

            UserAPI userAPI = GenAPIS.getAPI(UserAPI.class, false);
            Call<User> registerUser = userAPI.registerUser(body);
            H.enqueueReq(registerUser, (call, response) -> {
                if (response.isSuccessful()) {
                    formHandler.clearFields();
                    etRegPassword2.setText("");
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    formHandler.set400Error(response);
                }
            });
        });

        btnRegLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}