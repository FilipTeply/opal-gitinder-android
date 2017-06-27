package com.greenfox.opal.gitinder.service;

import android.provider.Settings;

import com.google.api.client.util.DateTime;
import com.greenfox.opal.gitinder.model.response.Profile;

import com.greenfox.opal.gitinder.Direction;

import com.greenfox.opal.gitinder.model.LoginRequest;
import com.greenfox.opal.gitinder.model.response.BaseResponse;
import com.greenfox.opal.gitinder.model.response.LoginResponse;
import com.greenfox.opal.gitinder.model.response.Match;
import com.greenfox.opal.gitinder.model.response.MatchesResponse;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import com.greenfox.opal.gitinder.model.response.SwipingResponse;
import com.greenfox.opal.gitinder.model.response.ProfileListResponse;

import retrofit2.Call;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Path;

public class MockServer implements ApiService {

  @Override
  public Call<LoginResponse> login(@Body final LoginRequest loginRequest) {
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

  @Override
  public MockCall<Profile> getProfileInfos(@Header("X-GiTinder-token") final String token) {
    return new MockCall<Profile>() {
      @Override
      public void enqueue(Callback<Profile> callback) {
        Profile response;
        if (token == null || "".equals(token)) {
          response = new Profile("Unauthorized request!");
        } else {
          List<String> repos = new ArrayList<>();
          List<String> languages = new ArrayList<>();
          repos.add("opal-gitinder-android");
          languages.add("Java");
          response = new Profile("happysloth", "happysloth.png", repos, languages);
        }
        callback.onResponse(null, Response.success(response));
      }
    };
  }

  @Override
  public MockCall<SwipingResponse> swiping(@Header(value = "X-GiTinder-token") final String token,
                                           @Path("username") String username,
                                           @Path("direction") Enum<Direction> direction) {
    return new MockCall<SwipingResponse>() {
      @Override
      public void enqueue(Callback callback) {
        BaseResponse response;
        if (token.isEmpty()) {
          response = new SwipingResponse();
        } else {
          response = new SwipingResponse(true);

        }
        callback.onResponse(null, Response.success(response));
      }
    };
  }

    @Override
    public MockCall<MatchesResponse> getMatches(@Header("X-GiTinder-token") final String token) {
      return new MockCall<MatchesResponse>() {
        @Override
        public void enqueue(Callback callback) {
          MatchesResponse response;
        if (token.isEmpty()) {
          response = new MatchesResponse("Unauthorized request!");
        } else {
          ArrayList<Match> matches = new ArrayList<>();
          matches.add(new Match("jondoe", new Timestamp(System.currentTimeMillis())));
          matches.add(new Match("jondoe2", new Timestamp(System.currentTimeMillis())));
          response = new MatchesResponse(matches);
        }
        callback.onResponse(null, Response.success(response));
      }
    };
  }
}
