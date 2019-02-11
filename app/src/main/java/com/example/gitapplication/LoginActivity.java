package com.example.gitapplication;

import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.example.gitapplication.entity.Repositories;
import com.example.gitapplication.entity.Repository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{


    public static LoginActivity activity;
    private AutoCompleteTextView mEmailView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        listView = (ListView) findViewById(R.id.list_view);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        try {
            getAllRepositories(mEmailView.getText().toString());
        } catch (IOException e) {
            e.printStackTrace();
            listView.setAdapter(null);
        }

    }

    public void getAllRepositories(String nick) throws IOException {
        sendGet(nick);
    }

    private void sendGet(String nick) throws IOException {
        String url = "https://api.github.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Repositories repositories = retrofit.create(Repositories.class);
        Call<List<Repository>> repositories1 = repositories.repositories(nick);

        repositories1.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                Type type = new TypeToken<Repository[]>(){}.getType();
                if (!response.isSuccessful() || response.body() == null){
                    listView.setAdapter(null);
                }else {
                    String[] strings = response.body().stream().map(Repository::getName).toArray(String[]::new);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.activity, android.R.layout.simple_list_item_1, strings);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                listView.setAdapter(null);
            }
        });
    }
}

