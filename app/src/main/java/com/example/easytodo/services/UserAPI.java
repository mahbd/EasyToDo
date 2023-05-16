package com.example.easytodo.services;

import com.example.easytodo.models.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserAPI {
    @POST("auth/jwt/create/")
    Call<Map<String, String>> getToken(@Body Map<String, String> body);

    @POST("auth/jwt/refresh/")
    Call<Map<String, String>> refreshToken(@Body Map<String, String> body);

    @POST("auth/jwt/verify/")
    Call<Map<String, String>> verifyToken(@Body Map<String, String> body);

    @POST("auth/users/")
    Call<User> registerUser(@Body Map<String, String> body);

    @GET("users/{id}/")
    Call<User> getUser(@Path("id") long id);
}
