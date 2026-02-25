package com.example.weconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weconnect.R;
import com.google.android.material.button.MaterialButton;

public class CreatePostActivity extends AppCompatActivity {

    private static final String TAG = "CreatePostActivity";

    private ImageView ivClose, ivUserAvatar, ivAddImage, ivAddLocation, ivTagInterest;
    private TextView tvUserName;
    private EditText etPostContent;
    private MaterialButton btnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        Log.d(TAG, "✅ Activity Created");

        initViews();
        setupClickListeners();
        setupBackPressHandler();
    }

    private void initViews() {
        Log.d(TAG, "Initializing views...");

        // Header
        ivClose = findViewById(R.id.ivClose);

        // User info
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        tvUserName = findViewById(R.id.tvUserName);

        // Content
        etPostContent = findViewById(R.id.etPostContent);

        // Media tools
        ivAddImage = findViewById(R.id.ivAddImage);
        ivAddLocation = findViewById(R.id.ivAddLocation);
        ivTagInterest = findViewById(R.id.ivTagInterest);

        // Post button
        btnPost = findViewById(R.id.btnPost);

        // DEBUG
        Log.d(TAG, "ivClose null? " + (ivClose == null));
        Log.d(TAG, "ivClose clickable? " + (ivClose != null && ivClose.isClickable()));
        Log.d(TAG, "btnPost null? " + (btnPost == null));
    }

    private void setupClickListeners() {
        Log.d(TAG, "Setting up click listeners...");

        // ✅ Nút Close - QUAN TRỌNG: Quay về MainActivity
        // ✅ Nút Close - ĐÚNG THỨ TỰ
        if (ivClose != null) {
            // ✅ BƯỚC 1: Set clickable TRƯỚC
            ivClose.setClickable(true);
            ivClose.setFocusable(true);
            ivClose.setEnabled(true);

            // ✅ BƯỚC 2: Set listener SAU
            ivClose.setOnClickListener(v -> {
                Log.e(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                Log.e(TAG, "🔴 CLOSE CLICKED! 🔴");
                Log.e(TAG, "🔴 CLOSE CLICKED! 🔴");
                Log.e(TAG, "🔴 CLOSE CLICKED! 🔴");
                Log.e(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

                Toast.makeText(this, "ĐÓNG!", Toast.LENGTH_LONG).show();
                finish();
            });

            Log.e(TAG, "✅ Close listener registered");
            Log.e(TAG, "ivClose clickable: " + ivClose.isClickable());
            Log.e(TAG, "ivClose enabled: " + ivClose.isEnabled());

        } else {
            Log.e(TAG, "❌ ivClose is NULL!");
        }

        // ✅ Nút Đăng - Đăng bài viết
        if (btnPost != null) {
            btnPost.setOnClickListener(v -> {
                Log.d(TAG, "🔴 POST CLICKED! 🔴");
                String content = etPostContent.getText().toString().trim();

                if (content.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập nội dung bài viết", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Truyền data về MainActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("post_content", content);
                resultIntent.putExtra("post_username", tvUserName.getText().toString());
                resultIntent.putExtra("post_time", "Vừa xong");
                setResult(RESULT_OK, resultIntent);

                Toast.makeText(this, "✅ Đã đăng bài viết thành công!", Toast.LENGTH_SHORT).show();

                // Quay về MainActivity
                finish();
            });

            Log.d(TAG, "✅ Post listener registered");
        } else {
            Log.e(TAG, "❌ btnPost is NULL!");
        }

        // ✅ Thêm ảnh
        if (ivAddImage != null) {
            ivAddImage.setOnClickListener(v -> {
                Log.d(TAG, "Add image clicked");
                Toast.makeText(this, "📷 Chức năng thêm ảnh (Coming soon)", Toast.LENGTH_SHORT).show();
            });
        }

        // ✅ Thêm địa điểm
        if (ivAddLocation != null) {
            ivAddLocation.setOnClickListener(v -> {
                Log.d(TAG, "Add location clicked");
                Toast.makeText(this, "📍 Chức năng thêm địa điểm (Coming soon)", Toast.LENGTH_SHORT).show();
            });
        }

        // ✅ Tag sở thích
        if (ivTagInterest != null) {
            ivTagInterest.setOnClickListener(v -> {
                Log.d(TAG, "Tag interest clicked");
                Toast.makeText(this, "🏷️ Chức năng tag sở thích (Coming soon)", Toast.LENGTH_SHORT).show();
            });
        }
        // TEST BUTTON


    }

    private void setupBackPressHandler() {
        Log.d(TAG, "Setting up back press handler...");

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "System back pressed");
                handleBackPress();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        Log.d(TAG, "✅ Back press handler registered");
    }

    private void handleBackPress() {
        Log.d(TAG, "handleBackPress called");

        String content = etPostContent.getText().toString().trim();

        if (!content.isEmpty()) {
            Log.d(TAG, "Content exists, showing dialog");
            new AlertDialog.Builder(this)
                    .setTitle("Hủy bài viết?")
                    .setMessage("Bạn có chắc muốn hủy bài viết này không?")
                    .setPositiveButton("Hủy bỏ", (dialog, which) -> {
                        Log.d(TAG, "User confirmed close");
                        finish();
                    })
                    .setNegativeButton("Tiếp tục viết", (dialog, which) -> {
                        Log.d(TAG, "User canceled close");
                    })
                    .show();
        } else {
            Log.d(TAG, "No content, closing immediately");
            finish();
        }
    }
}