package com.example.weconnect.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.example.weconnect.activities.UserProfileActivity;
import com.example.weconnect.R;

public class PostDetailActivity extends AppCompatActivity {

    private ImageView ivBackPostDetail;
    private TextView tvPostDetailUsername;
    private TextView tvPostDetailContent;
    private TextView tvPostDetailTag;
    private TextView tvPostDetailLocation;
    private TextView tvPostDetailMembers;
    private String username;

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
    }

    private void setupClickListeners() {
        ivBackPostDetail.setOnClickListener(v -> finish());

        tvPostDetailUsername.setOnClickListener(v -> {
            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });
    }

    private void bindPostData() {
        username = getIntent().getStringExtra("post_username");
        String content = getIntent().getStringExtra("post_content");
        String tag = getIntent().getStringExtra("post_tag");
        String location = getIntent().getStringExtra("post_location");
        int memberCount = getIntent().getIntExtra("post_member_count", 0);
        int maxMembers = getIntent().getIntExtra("post_max_members", 0);

        if (username == null) username = "";
        if (content == null) content = "";
        if (tag == null) tag = "";
        if (location == null) location = "";

        tvPostDetailUsername.setText(username);
        tvPostDetailContent.setText(content);
        tvPostDetailMembers.setText("👥 " + memberCount + "/" + maxMembers);

        if (tag.length() > 0) {
            tvPostDetailTag.setVisibility(View.VISIBLE);
            tvPostDetailTag.setText(tag);
        } else {
            tvPostDetailTag.setVisibility(View.GONE);
        }

        if (location.length() > 0) {
            tvPostDetailLocation.setVisibility(View.VISIBLE);
            tvPostDetailLocation.setText("📍 " + location);
        } else {
            tvPostDetailLocation.setVisibility(View.GONE);
        }
    }
}