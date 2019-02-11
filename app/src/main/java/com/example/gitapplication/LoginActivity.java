package com.example.gitapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gitapplication.entity.Repository;
import com.example.gitapplication.service.GitService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.READ_CONTACTS;

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
        GitService gitService = new GitService();
        try {
            getAllRepositories(mEmailView.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            listView.setAdapter(null);
        }

    }

    public void getAllRepositories(String nick) throws IOException {
        sendGet(nick);
    }

    private void sendGet(String nick) throws IOException {
        String url = String.format("https://api.github.com/users/%s/repos",nick);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("User-Agent", "Mozilla/5.0");
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Type type = new TypeToken<Repository[]>(){}.getType();
                Repository[] repositories = new Gson().fromJson(new String(response, StandardCharsets.UTF_8), type);
                String[] strings = Arrays.stream(repositories).map(Repository::getName).collect(Collectors.toList()).toArray(new String[0]);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.activity, android.R.layout.simple_list_item_1, strings);
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                listView.setAdapter(null);
            }

            @Override
            public void onRetry(int retryNo) {
            }
        });
    }
}

