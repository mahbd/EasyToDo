package com.example.easytodo.services;

import com.example.easytodo.models.Token;
import com.example.easytodo.models.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserAPI {
    @POST("auth/jwt/create/")
    Call<Token> getToken(@Body Map<String, String> body);

    @POST("auth/users/")
    Call<User> registerUser(@Body Map<String, String> body);
}
