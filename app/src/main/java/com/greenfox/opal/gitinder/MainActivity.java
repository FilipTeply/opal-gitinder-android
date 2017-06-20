package com.greenfox.opal.gitinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.greenfox.opal.gitinder.model.LoginRequest;
import com.wuman.android.auth.AuthorizationDialogController;
import com.wuman.android.auth.AuthorizationFlow;
import com.wuman.android.auth.DialogFragmentController;
import com.wuman.android.auth.OAuthManager;

import com.wuman.android.auth.OAuthManager.OAuthCallback;
import com.wuman.android.auth.OAuthManager.OAuthFuture;
import java.io.IOException;
import com.greenfox.opal.gitinder.response.LoginResponse;
import com.greenfox.opal.gitinder.service.MockServer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ApiService service;
    Retrofit retrofit;
    boolean connectToBackend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        AuthorizationFlow.Builder builder = new AuthorizationFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                AndroidHttp.newCompatibleTransport(),
                new JacksonFactory(),
                new GenericUrl("https://github.com/login/oauth/access_token"),
                new ClientParametersAuthentication(getResources().getString(R.string.CLIENT_ID), getResources().getString(R.string.CLIENT_SECRET)),
            getResources().getString(R.string.CLIENT_ID),
                "http://github.com/login/oauth/authorize");
            builder.setRequestInitializer(new HttpRequestInitializer() {
                                      @Override
                                      public void initialize(HttpRequest request) throws IOException {
                                        request.getHeaders().setAccept("application/json");
                                      }
                                    });

      AuthorizationFlow flow = builder.build();


        AuthorizationDialogController controller =
                new DialogFragmentController(getFragmentManager()) {
                    @Override
                    public String getRedirectUri() throws IOException {
                        return "http://gitinder.herokuapp.com/callback";
                    }

                    @Override
                    public boolean isJavascriptEnabledForWebView() {
                        return true;
                    }

                    @Override
                    public boolean disableWebViewCache() {
                        return false;
                    }

                    @Override
                    public boolean removePreviousCookie() {
                        return false;
                    }
                };

        OAuthManager oAuthManager = new OAuthManager(flow, controller);
      oAuthManager.authorizeExplicitly("userID", new OAuthCallback<Credential>() {
        @Override
        public void run(OAuthFuture<Credential> future) {
          try {
            Log.d("success", future.getResult().getAccessToken());
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }, null);
      
        if (connectToBackend) {
            retrofit = new Retrofit.Builder()
                .baseUrl("http://gitinder.herokuapp.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
            service = retrofit.create(ApiService.class);
        } else {
            service = new MockServer();
        }
        onLogin("Bond", "abcd1234");
        onLogin("", "");
        checkLogin();
    }

    public void sendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(message);
    }

    public void checkLogin() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String username = preferences.getString("Username", null);

        if (TextUtils.isEmpty(username)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
  
    public void onLogin(String username, String token) {
        LoginRequest testLogin = new LoginRequest(username, token);
        service.login(testLogin).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body().getStatus().equals("ok")) {
                    Log.d("login", response.body().getToken());
                } else {
                    Log.d("login", response.body().getMessage());
                }
            }
    
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d("login", "FAIL! =(");
            }
        });
    }
}
