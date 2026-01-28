package com.example.weconnect.api;
import com.example.weconnect.models.LoginRequest; // Bạn cần tạo file này trong folder models
import com.example.weconnect.models.User;         // Bạn cần tạo file này trong folder models

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface AuthApiService {
    @POST("api/auth/login")
    Call<User> login(@Body LoginRequest loginRequest);

    // API Đăng ký
    @POST("api/auth/register")
    Call<String> register(@Body User user);
}
