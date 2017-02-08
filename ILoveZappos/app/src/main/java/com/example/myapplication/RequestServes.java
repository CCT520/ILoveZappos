package com.example.myapplication;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by chenchutian on 2017/1/29.
 */

public interface RequestServes {
    @GET("Search")
    Call<RequestResult> getResult(@Query("term") String term,
                                  @Query("key") String key);

}