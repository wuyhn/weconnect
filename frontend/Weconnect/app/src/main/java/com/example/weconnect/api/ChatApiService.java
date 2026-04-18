package com.example.weconnect.api;

import com.example.weconnect.models.ApiResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface ChatApiService {

    // Danh sách phòng chat của user
    @GET("api/chat/rooms")
    Call<ApiResponse<List<Map<String, Object>>>> getRooms();

    // Chi tiết phòng chat
    @GET("api/chat/rooms/{id}")
    Call<ApiResponse<Map<String, Object>>> getRoom(@Path("id") long id);

    // Lấy hoặc tạo phòng DM
    @GET("api/chat/direct/{userId}")
    Call<ApiResponse<Map<String, Object>>> getDirectRoom(@Path("userId") long userId);

    // Tạo phòng nhóm bạn bè
    @POST("api/chat/rooms")
    Call<ApiResponse<Map<String, Object>>> createGroupRoom(@Body Map<String, Object> body);

    // Lịch sử tin nhắn
    @GET("api/chat/rooms/{id}/messages")
    Call<ApiResponse<List<Map<String, Object>>>> getMessages(@Path("id") long id);

    // Tin nhắn mới (polling)
    @GET("api/chat/rooms/{id}/messages/new")
    Call<ApiResponse<List<Map<String, Object>>>> getNewMessages(
            @Path("id") long id, @Query("afterId") long afterId);

    // Gửi tin nhắn
    @POST("api/chat/rooms/{id}/messages")
    Call<ApiResponse<Map<String, Object>>> sendMessage(
            @Path("id") long id, @Body Map<String, String> body);
}
