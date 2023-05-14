package com.example.easytodo.services;

import com.example.easytodo.models.Project;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProjectAPI {
    @GET("projects/")
    Call<Project> getProjects();

    @GET("projects/{id}/")
    Call<Project> getProject(@Path("id") long id);

    @POST("projects/")
    Call<Project> createProject(@Body Map<String, Object> body);

    @PUT("projects/{id}/")
    Call<Project> updateProject(@Path("id") long id, @Body Map<String, Object> body);
}
