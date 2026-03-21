package com.example.weconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weconnect.R;
import com.example.weconnect.models.Post;
import com.google.android.material.button.MaterialButton;

public class PostDetailActivity extends AppCompatActivity {

    private ImageView ivBackPostDetail;
    private TextView tvPostDetailUsername;
    private TextView tvPostDetailContent;
    private TextView tvPostDetailTag;
    private TextView tvPostDetailLocation;
    private TextView tvPostDetailMembers;
    private TextView tvPostDetailTime;
    private TextView tvPostDetailStatus;
    private MaterialButton btnOpenGroupChat;
    private String username;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        initViews();
        setupClickListeners();
        bindPostData();
    }

    private void initViews() {
        ivBackPostDetail = findViewById(R.id.ivBackPostDetail);
        tvPostDetailUsername = findViewById(R.id.tvPostDetailUsername);
        tvPostDetailContent = findViewById(R.id.tvPostDetailContent);
        tvPostDetailTag = findViewById(R.id.tvPostDetailTag);
        tvPostDetailLocation = findViewById(R.id.tvPostDetailLocation);
        tvPostDetailMembers = findViewById(R.id.tvPostDetailMembers);
        tvPostDetailTime = findViewById(R.id.tvPostDetailTime);
        tvPostDetailStatus = findViewById(R.id.tvPostDetailStatus);
        btnOpenGroupChat = findViewById(R.id.btnOpenGroupChat);
    }

    private void setupClickListeners() {
        ivBackPostDetail.setOnClickListener(v -> finish());

        tvPostDetailUsername.setOnClickListener(v -> {
            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        btnOpenGroupChat.setOnClickListener(v -> openRelatedGroupChat());
    }

    private void bindPostData() {
        post = (Post) getIntent().getSerializableExtra("post");
        if (post == null) {
            finish();
            return;
        }

        username = post.getUsername();
        tvPostDetailUsername.setText(username);
        tvPostDetailContent.setText(post.getContent());
        tvPostDetailMembers.setText("Members: " + post.getMemberCount() + "/" + post.getMaxMembers());
        tvPostDetailTime.setText("Created: " + post.getTimeAgo());
        tvPostDetailStatus.setText("Status: " + post.getStatusLabel());

        if (post.getInterestTag() != null && post.getInterestTag().length() > 0) {
            tvPostDetailTag.setVisibility(View.VISIBLE);
            tvPostDetailTag.setText(post.getInterestTag());
        } else {
            tvPostDetailTag.setVisibility(View.GONE);
        }

        if (post.getLocation() != null && post.getLocation().length() > 0) {
            tvPostDetailLocation.setVisibility(View.VISIBLE);
            tvPostDetailLocation.setText("Location: " + post.getLocation());
        } else {
            tvPostDetailLocation.setVisibility(View.GONE);
        }
    }

    private void openRelatedGroupChat() {
        if (post == null) {
            return;
        }

        Intent intent = new Intent(this, ChatListActivity.class);
        intent.putExtra("highlight_tag", post.getInterestTag());
        startActivity(intent);
        Toast.makeText(this, "Opened chat list for this activity.", Toast.LENGTH_SHORT).show();
    }
}
