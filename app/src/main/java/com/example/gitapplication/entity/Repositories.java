package com.example.gitapplication.entity;

import android.os.Message;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface Repositories {

    @GET("users/{nick}/repos")
    Observable<List<Repository>> repositories(@Path("nick") String nick);
}
