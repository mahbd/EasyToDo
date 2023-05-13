package com.example.easytodo.services;

import com.example.easytodo.models.Share;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ShareAPI {
    @GET("shares/")
    Call<List<Share>> getShares(@QueryMap Map<String, String> query);
}
