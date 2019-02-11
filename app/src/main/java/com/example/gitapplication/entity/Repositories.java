package com.example.gitapplication.entity;

import android.os.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Repositories {

    @GET("users/{nick}/repos")
    Call<List<Repository>> repositories(@Path("nick") String nick);
}
