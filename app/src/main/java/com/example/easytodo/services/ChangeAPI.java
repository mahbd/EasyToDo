package com.example.easytodo.services;

import com.example.easytodo.models.Change;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ChangeAPI {
    @GET("changes/")
    Call<List<Change>> getChanges();

    @GET("changes/")
    Call<List<Change>> getChangesAfter(@Query("created_at_after") String date);
}
