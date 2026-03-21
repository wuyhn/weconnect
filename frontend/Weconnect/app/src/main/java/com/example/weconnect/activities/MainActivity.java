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
import com.example.weconnect.data.FakePostRepository;
import com.example.weconnect.models.Post;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView ivAdd, ivSearch;
    private FrameLayout btnHome, btnMessages, btnNotifications, btnProfile;
    private RecyclerView rvPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private View statusHeader;
    private FakePostRepository postRepository;
    private ActivityResultLauncher<Intent> createPostLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postRepository = FakePostRepository.getInstance();
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
                        String username = data.getStringExtra("post_username");
                        String time = data.getStringExtra("post_time");
                        String tag = data.getStringExtra("post_tag");
                        String location = data.getStringExtra("post_location");
                        int maxMembers = data.getIntExtra("post_max_members", 0);
                        long now = System.currentTimeMillis();

                        Post newPost = new Post(
                                String.valueOf(now),
                                username,
                                time,
                                content,
                                tag,
                                location,
                                R.drawable.ic_user_placeholder,
                                0,
                                0,
                                0,
                                0,
                                maxMembers,
                                false,
                                now,
                                now + 24L * 60L * 60L * 1000L,
                                false
                        );
                        postRepository.addPost(newPost);
                        refreshPosts();
                        rvPosts.smoothScrollToPosition(0);
                    }
                }
        );
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
            showToast("Trang chu");
        });

        btnMessages.setOnClickListener(v -> {
            highlightTab(btnMessages);
            Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
            startActivity(intent);
        });

        btnNotifications.setOnClickListener(v -> {
            highlightTab(btnNotifications);
            showToast("Thong bao");
        });

        btnProfile.setOnClickListener(v -> {
            highlightTab(btnProfile);
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            intent.putExtra("username", postRepository.getCurrentUsername());
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        postList = postRepository.getActivePosts();
        postAdapter = new PostAdapter(this, postList);
        rvPosts.setAdapter(postAdapter);
    }

    private void refreshPosts() {
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
