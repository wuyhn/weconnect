package com.example.weconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.adapters.PostAdapter;
import com.example.weconnect.api.PostApiService;
import com.example.weconnect.api.RetrofitClient;
import com.example.weconnect.data.FakePostRepository;
import com.example.weconnect.models.ApiResponse;
import com.example.weconnect.models.Post;
import com.example.weconnect.models.PostResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ImageView ivAdd, ivSearch;
    private FrameLayout btnHome, btnMessages, btnNotifications, btnProfile;
    private RecyclerView rvPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private View statusHeader;
    private FakePostRepository postRepository;
    private PostApiService postApiService;
    private ActivityResultLauncher<Intent> createPostLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postRepository = FakePostRepository.getInstance();

        // Load JWT token đã lưu
        RetrofitClient.loadToken(this);
        postApiService = RetrofitClient.getClient().create(PostApiService.class);

        // Sync tên user thật với FakeRepositories (để profile detection hoạt động)
        String realName = RetrofitClient.getUserName(this);
        if (realName != null && !realName.isEmpty()) {
            com.example.weconnect.data.FakeSocialRepository.getInstance().setCurrentUsername(realName);
            postRepository.setCurrentUsername(realName);
        }

        setupActivityResultLauncher();
        initViews();
        setupClickListeners();
        setupRecyclerView();
    }

    private void setupActivityResultLauncher() {
        createPostLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String content = data.getStringExtra("post_content");
                        String tag = data.getStringExtra("post_tag");
                        String location = data.getStringExtra("post_location");
                        int maxMembers = data.getIntExtra("post_max_members", 10);
                        String imageUri = data.getStringExtra("post_image_uri");

                        // Gọi API tạo bài đăng mới
                        createPostViaApi(content, tag, location, maxMembers, imageUri);
                    }
                }
        );
    }

    private void createPostViaApi(String content, String tag, String location, int maxMembers, String imageUri) {
        Map<String, Object> body = new HashMap<>();
        body.put("content", content);
        body.put("interestTag", tag);
        body.put("location", location);
        body.put("maxMembers", maxMembers);
        if (imageUri != null) {
            body.put("imageUrl", imageUri);
        }

        postApiService.createPost(body).enqueue(new Callback<ApiResponse<PostResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostResponse>> call,
                                   Response<ApiResponse<PostResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(MainActivity.this, "Đã tạo bài đăng!", Toast.LENGTH_SHORT).show();
                    loadPostsFromApi();
                } else {
                    Toast.makeText(MainActivity.this, "Không thể tạo bài đăng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PostResponse>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        ivAdd = findViewById(R.id.ivAdd);
        ivSearch = findViewById(R.id.ivSearch);
        btnHome = findViewById(R.id.btnHome);
        btnMessages = findViewById(R.id.btnMessages);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnProfile = findViewById(R.id.btnProfile);
        rvPosts = findViewById(R.id.rvPosts);
        statusHeader = findViewById(R.id.statusHeader);

        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout =
                findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(0xFFFF4D6D);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadPostsFromApi();
            swipeRefreshLayout.postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
        });
    }

    private void setupClickListeners() {
        ivAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
            createPostLauncher.launch(intent);
        });

        ivSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        statusHeader.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
            createPostLauncher.launch(intent);
        });

        btnHome.setOnClickListener(v -> {
            highlightTab(btnHome);
            showToast("Trang ch\u1ee7");
        });

        btnMessages.setOnClickListener(v -> {
            highlightTab(btnMessages);
            Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
            startActivity(intent);
        });

        btnNotifications.setOnClickListener(v -> {
            highlightTab(btnNotifications);
            Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            highlightTab(btnProfile);
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            intent.putExtra("username", RetrofitClient.getUserName(this));
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        rvPosts.setAdapter(postAdapter);

        // Load từ API
        loadPostsFromApi();
    }

    private void loadPostsFromApi() {
        postApiService.getActivePosts().enqueue(new Callback<ApiResponse<List<PostResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PostResponse>>> call,
                                   Response<ApiResponse<List<PostResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<PostResponse> postResponses = response.body().getResult();
                    postList.clear();
                    if (postResponses != null) {
                        for (PostResponse pr : postResponses) {
                            postList.add(pr.toPost());
                        }
                    }
                    postAdapter.notifyDataSetChanged();
                } else {
                    // Fallback: dùng FakePostRepository nếu API lỗi
                    loadPostsFallback();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<PostResponse>>> call, Throwable t) {
                // Fallback về fake data nếu server không kết nối được
                loadPostsFallback();
            }
        });
    }

    private void loadPostsFallback() {
        postList.clear();
        postList.addAll(postRepository.getActivePosts());
        postAdapter.notifyDataSetChanged();
    }

    private void highlightTab(FrameLayout selectedTab) {
        btnHome.setAlpha(0.5f);
        btnMessages.setAlpha(0.5f);
        btnNotifications.setAlpha(0.5f);
        btnProfile.setAlpha(0.5f);
        selectedTab.setAlpha(1.0f);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
