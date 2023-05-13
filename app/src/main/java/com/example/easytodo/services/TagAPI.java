package com.example.easytodo.services;

import com.example.easytodo.models.Tag;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;



public interface TagAPI {
    @Headers({"Content-Type: application/json"})
    @GET("tags/")
    Call<List<Tag>> getTags();

    @GET("tags/{id}/")
    Call<Tag> getTag(@Path("id") long id);
}
