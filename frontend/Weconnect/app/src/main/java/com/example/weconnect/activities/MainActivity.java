package com.example.weconnect.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import com.example.weconnect.R;
import com.example.weconnect.adapters.PostAdapter;
import com.example.weconnect.models.Post;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView ivAdd, ivSearch;
    private FrameLayout btnHome, btnMessages, btnNotifications, btnProfile;
    private RecyclerView rvPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private View statusHeader;

    // ✅ ActivityResultLauncher để nhận kết quả từ CreatePostActivity
    private ActivityResultLauncher<Intent> createPostLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ Setup Activity Result Launcher TRƯỚC
        setupActivityResultLauncher();

        // --- 1. Initialize Views ---
        initViews();

        // --- 2. Setup Click Listeners ---
        setupClickListeners();

        // --- 3. Setup RecyclerView ---
        setupRecyclerView();
    }

    // ✅ Setup launcher để nhận kết quả từ CreatePostActivity
    private void setupActivityResultLauncher() {
        createPostLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String content = data.getStringExtra("post_content");
                        String username = data.getStringExtra("post_username");
                        String time = data.getStringExtra("post_time");

                        // ✅ Tạo bài viết mới và thêm vào đầu danh sách
                        Post newPost = new Post(
                                String.valueOf(System.currentTimeMillis()), // ID unique
                                username,
                                time,
                                content,
                                R.drawable.ic_user_placeholder,
                                0, // imageResId
                                0, // memberCount
                                0, // likesCount
                                0, // commentsCount
                                0, // maxMembers
                                false // joined
                        );

                        // Thêm vào đầu danh sách
                        postList.add(0, newPost);

                        // Cập nhật RecyclerView
                        postAdapter.notifyItemInserted(0);

                        // Scroll lên đầu để thấy bài viết mới
                        rvPosts.smoothScrollToPosition(0);

                        Toast.makeText(this, "✅ Bài viết đã được thêm!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void initViews() {
        // Header Icons
        ivAdd = findViewById(R.id.ivAdd);
        ivSearch = findViewById(R.id.ivSearch);

        // Navigation Buttons
        btnHome = findViewById(R.id.btnHome);
        btnMessages = findViewById(R.id.btnMessages);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnProfile = findViewById(R.id.btnProfile);


        // Content List
        rvPosts = findViewById(R.id.rvPosts);

        // Status Header
        statusHeader = findViewById(R.id.statusHeader);
    }

    private void setupClickListeners() {
        // --- Header Actions ---
        ivAdd.setOnClickListener(v -> {
            // ✅ Mở trang tạo bài viết bằng launcher
            Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
            createPostLauncher.launch(intent);
        });

        ivSearch.setOnClickListener(v -> showToast("Tìm kiếm"));

        // ✅ Click vào "Bạn đang nghĩ gì?" để tạo bài viết
        statusHeader.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
            createPostLauncher.launch(intent);
        });

        // --- Navigation Actions ---
        btnHome.setOnClickListener(v -> {
            highlightTab(btnHome);
            showToast("Trang chủ");
        });

        btnMessages.setOnClickListener(v -> {
            highlightTab(btnMessages);
            showToast("Tin nhắn");
        });

        btnNotifications.setOnClickListener(v -> {
            highlightTab(btnNotifications);
            showToast("Thông báo");
        });

        btnProfile.setOnClickListener(v -> {
            highlightTab(btnProfile);
            showToast("Hồ sơ");
        });
    }

    private void setupRecyclerView() {
        // Set Layout Manager
        rvPosts.setLayoutManager(new LinearLayoutManager(this));

        // Create Dummy Data
        postList = new ArrayList<>();
        postList.add(new Post("1", "Quỳnh Nguyễn", "15 phút trước", "Hôm nay trời đẹp quá! 🌞 Đi cafe không mọi người ơi?", R.drawable.ic_user_placeholder, 0, 120, 15, 2, 20, false));
        postList.add(new Post("2", "Minh Hoàng", "1 giờ trước", "Vừa hoàn thành dự án mới, cảm thấy thật tuyệt vời! 🚀 #CodingLife #Android", R.drawable.ic_user_placeholder, R.drawable.ic_launcher_background, 450, 89, 45, 50, true));
        postList.add(new Post("3", "Lan Anh", "3 giờ trước", "Có ai biết quán ăn ngon ở quận 1 không ạ? Cần tìm gấp cho tối nay! 🍝", R.drawable.ic_user_placeholder, 0, 56, 42, 3, 10, false));
        postList.add(new Post("4", "Community Admin", "5 giờ trước", "Chào mừng các bạn đến với WeConnect! Hãy chia sẻ những khoảnh khắc đáng nhớ nhé. ❤️", R.drawable.ic_user_placeholder, 0, 1024, 300, 1500, 5000, true));

        // Set Adapter
        postAdapter = new PostAdapter(this, postList);
        rvPosts.setAdapter(postAdapter);
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