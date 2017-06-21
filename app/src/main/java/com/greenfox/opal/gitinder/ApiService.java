package com.greenfox.opal.gitinder;

import com.greenfox.opal.gitinder.model.Profile;

import com.greenfox.opal.gitinder.model.LoginRequest;
import com.greenfox.opal.gitinder.model.response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @PUT("/profiles/{username}/{direction}")
    Call<Profile> swiping(@Header(value = "X-GiTinder-token") String token, @Path("username") String username, @Path("direction") Enum<Direction> direction);
}
