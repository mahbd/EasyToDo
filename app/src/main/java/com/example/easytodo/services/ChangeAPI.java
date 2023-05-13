package com.example.easytodo.services;

import com.example.easytodo.models.Change;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ChangeAPI {
    @GET("changes/")
    Call<List<Change>> getChanges();

    @GET("changes/?created_at_after={date}")
    Call<List<Change>> getChangesAfter(@Path("date") String date);
}
