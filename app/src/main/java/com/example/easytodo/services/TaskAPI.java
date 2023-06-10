package com.example.easytodo.services;

import com.example.easytodo.models.Task;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TaskAPI {
    @GET("tasks/with-me/")
    Call<List<Task>> getTasksWithMe();

    @GET("tasks/{id}/")
    Call<Task> getTask(@Path("id") long id);

    @POST("tasks/")
    Call<Task> createTask(@Body Map<String, Object> body);

    @PUT("tasks/{id}/")
    Call<Task> updateTask(@Path("id") long id, @Body Map<String, Object> body);

    @DELETE("tasks/{id}/")
    Call<Void> deleteTask(@Path("id") long id);
}
