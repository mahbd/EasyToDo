package com.example.easytodo.services;

import com.example.easytodo.models.Task;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TaskAPI {
    @GET("tasks/")
    Call<Task> getTasks();
    @GET("tasks/{id}/")
    Call<Task> getTask(@Path("id") long id);
}
