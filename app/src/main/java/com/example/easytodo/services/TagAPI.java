package com.example.easytodo.services;

import com.example.easytodo.models.Tag;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;



public interface TagAPI {
    @GET("tags/{id}/")
    Call<Tag> getTag(@Path("id") long id);

    @POST("tags/")
    Call<Tag> createTag(@Body Map<String, Object> body);

    @PUT("tags/{id}/")
    Call<Tag> updateTag(@Path("id") long id, @Body Map<String, Object> body);

    @DELETE("tags/{id}/")
    Call<Void> deleteTag(@Path("id") long id);
}
