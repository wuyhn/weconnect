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
                        String tag = data.getStringExtra("post_tag"); // ✅ NHẬN TAG

                        Post newPost = new Post(
                                String.valueOf(System.currentTimeMillis()),
                                username, time, content,
                                tag, // ✅ TRUYỀN TAG VÀO
                                R.drawable.ic_user_placeholder, 0, 0, 0, 0, 0, false
                        );
                        postList.add(0, newPost);
                        postAdapter.notifyItemInserted(0);
                        rvPosts.smoothScrollToPosition(0);
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

        // ✅ Lưu ý: Phải truyền đủ 12 tham số (đã thêm biến tag ở vị trí số 5)
        postList.add(new Post("1", "Quỳnh Nguyễn", "15 phút trước", "Đi cafe không?", "☕ Đi Cafe", R.drawable.ic_user_placeholder, 0, 120, 15, 2, 20, false));

        postList.add(new Post("2", "Minh Hoàng", "1 giờ trước", "Vừa hoàn thành dự án mới! 🚀", "💻 Đồ họa / Code", R.drawable.ic_user_placeholder, R.drawable.ic_launcher_background, 450, 89, 45, 50, true));

        postList.add(new Post("3", "Lan Anh", "3 giờ trước", "Cần tìm quán ăn ngon quận 1! 🍝", "", R.drawable.ic_user_placeholder, 0, 56, 42, 3, 10, false));

        postList.add(new Post("4", "Community Admin", "5 giờ trước", "Chào mừng các bạn đến với WeConnect!", "📢 Thông báo", R.drawable.ic_user_placeholder, 0, 1024, 300, 1500, 5000, true));

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