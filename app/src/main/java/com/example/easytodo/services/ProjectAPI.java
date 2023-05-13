package com.example.easytodo.services;

import com.example.easytodo.models.Project;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProjectAPI {
    @GET("projects/")
    Call<Project> getProjects();

    @GET("projects/{id}/")
    Call<Project> getProject(@Path("id") long id);
}
