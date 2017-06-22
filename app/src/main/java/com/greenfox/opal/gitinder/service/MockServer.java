package com.greenfox.opal.gitinder.service;

import com.greenfox.opal.gitinder.ApiService;
import com.greenfox.opal.gitinder.model.LoginRequest;
import com.greenfox.opal.gitinder.model.response.Profile;
import com.greenfox.opal.gitinder.model.response.LoginResponse;
import com.greenfox.opal.gitinder.model.response.ProfileListResponse;

import java.util.ArrayList;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Path;

public class MockServer implements ApiService {
    @Override
    public MockCall<LoginResponse> login(@Body final LoginRequest loginRequest) {
        return new MockCall<LoginResponse>() {
            @Override
            public void enqueue(Callback<LoginResponse> callback) {
                LoginResponse response;
                if (loginRequest.getUsername().isEmpty() || loginRequest.getAccessToken().isEmpty()) {
                    String message = "Missing parameter(s):";
                    if (loginRequest.getUsername().isEmpty()) {
                        message += " username";
                    }
                    if (loginRequest.getAccessToken().isEmpty()) {
                        message += " accessToken";
                    }
                    message += "!";
                    response = new LoginResponse(message);
                } else {
                    response = new LoginResponse(loginRequest.getUsername(), loginRequest.getAccessToken());
                }
                callback.onResponse(null, Response.success(response));
            }
        };
    }

    @Override
    public MockCall<ProfileListResponse> getListOfTinders(@Header("X-GiTinder-token") final String token, @Path("page") Integer page) {
        return new MockCall<ProfileListResponse>() {
            @Override
            public void enqueue(Callback<ProfileListResponse> callback) {
                ProfileListResponse response;
                if (token == null || "".equals(token)) {
                    response = new ProfileListResponse("Unauthorized request!");
                } else {
                    ArrayList<Profile> list = new ArrayList<>();
                    ArrayList<String> repos = new ArrayList<>();
                    ArrayList<String> languages = new ArrayList<>();
                    repos.add("opal-gitinder-android");
                    languages.add("Java");
                    list.add(new Profile("garlyle", "funny.jpg", repos, languages));
                    list.add(new Profile("balintvecsey", "quiet.jpg", repos, languages));
                    list.add(new Profile("dorinagy", "smiley.jpg", repos, languages));
                    response = new ProfileListResponse(list, list.size(), 42);
                }
                callback.onResponse(null, Response.success(response));
            }
        };
    }
}
