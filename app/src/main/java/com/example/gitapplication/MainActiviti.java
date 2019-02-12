package com.example.gitapplication;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.example.gitapplication.entity.Repository;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActiviti extends AppCompatActivity{

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String BASIC_API_URL = "https://api.github.com/";
    private MyTask task;
    private AutoCompleteTextView mEmailView;
    private ListView listView;

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
        getAllRepositories(mEmailView.getText().toString());
    }

    public void getAllRepositories(String nick) {
        this.task = new MyTask(nick);
        this.task.execute();
    }

    class MyTask extends AsyncTask<Void, Void, ArrayAdapter<String>> {

        private String nick;

        MyTask(String nick) {
            this.nick = nick;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayAdapter<String> doInBackground(Void... params) {
            String url = String.format(BASIC_API_URL + "users/%s/repos", nick);
            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            con.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    Repository[] repositories = new Gson().fromJson(response.toString(), Repository[].class);
                    List<String> listForAdapter = new ArrayList<>();
                    for (Repository repository : repositories) {
                        listForAdapter.add(repository.getName());
                    }

                    return new ArrayAdapter<>(MainActiviti.this,
                            android.R.layout.simple_list_item_1, listForAdapter);
                }
            } catch (IOException ignored) {}
            return null;
        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> result) {
            listView.setAdapter(result);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.task.cancel(true);
    }
}

