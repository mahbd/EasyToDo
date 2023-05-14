package com.example.easytodo.services;

import com.example.easytodo.models.User;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserAPI {
    @POST("auth/jwt/create/")
    Call<Map<String, String>> getToken(@Body Map<String, String> body);

    @POST("auth/jwt/refresh/")
    Call<JSONObject> refreshToken(@Body Map<String, String> body);

    @POST("auth/users/")
    Call<User> registerUser(@Body Map<String, String> body);

    @GET("users/")
    Call<List<User>> getUsers();
}
