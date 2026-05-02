package com.example.weconnect.data.network;

import com.example.weconnect.core.ApiResponse;
import com.example.weconnect.data.model.AuthResponse;
import com.example.weconnect.data.model.LoginRequest;
import com.example.weconnect.data.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("api/auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest loginRequest);

    @POST("api/auth/register")
    Call<ApiResponse<Void>> register(@Body User user);
}
