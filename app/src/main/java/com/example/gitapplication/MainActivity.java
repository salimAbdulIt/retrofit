package com.example.gitapplication;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.example.gitapplication.entity.Repositories;
import com.example.gitapplication.entity.Repository;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity{

    private static final String url = "https://api.github.com/";
    private AutoCompleteTextView nicknameView;
    private ListView listView;
    private Call<List<Repository>> task;
    private String lastNicknameForTask;
    private boolean isTaskFinished = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        nicknameView = findViewById(R.id.nickname);
        listView = findViewById(R.id.list_view);
        Button mEmailSignInButton = findViewById(R.id.submit_button);
        mEmailSignInButton.setOnClickListener(view -> onSubmit());
    }

    @Override
    protected void onStop() {
        super.onStop();
        task.cancel();
        finishTask();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (task == null || !task.isCanceled())
            return;
        startTask(lastNicknameForTask);
    }

    private void onSubmit() {
        getAllRepositories(nicknameView.getText().toString());
    }
    private void finishTask(){
        isTaskFinished = true;
    }

    private void startTask(String nick){
        lastNicknameForTask = nick;
        isTaskFinished = false;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Repositories repositories = retrofit.create(Repositories.class);
        task = repositories.repositories(nick);
        task.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                if (!response.isSuccessful() || response.body() == null){
                    listView.setAdapter(null);
                }else {
                    ArrayList<String> strings = new ArrayList<>();
                    for (Repository repository : response.body()) {
                        strings.add(repository.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, strings);
                    listView.setAdapter(adapter);
                }
                finishTask();
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                listView.setAdapter(null);
                finishTask();
            }
        });
    }

    private void getAllRepositories(String nick) {
        if (!isTaskFinished)
            return;
        startTask(nick);
    }

}

