package com.example.weconnect.activities;

import android.content.Intent;
import android.os.Bundle;
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

    private EditText etPostContent;
    private TextView tvUserName;
    private ImageView ivClose, ivAddImage, ivAddLocation, ivTagInterest;
    private MaterialButton btnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // 1. Ánh xạ View
        ivClose = findViewById(R.id.ivClose);
        etPostContent = findViewById(R.id.etPostContent);
        tvUserName = findViewById(R.id.tvUserName);
        btnPost = findViewById(R.id.btnPost);
        ivAddImage = findViewById(R.id.ivAddImage);
        ivAddLocation = findViewById(R.id.ivAddLocation);
        ivTagInterest = findViewById(R.id.ivTagInterest);

        // 2. Click cho nút Close - Cực kỳ quan trọng
//        ivClose.setOnClickListener(v -> handleExit());
        ivClose.setOnClickListener(v -> {
            // Log để kiểm tra xem có nhận lệnh không
            android.util.Log.d("QUYNH_DEBUG", "Nút Close đã được bấm!");
            finish();
        });
        // 3. Click nút Đăng
        btnPost.setOnClickListener(v -> {
            String content = etPostContent.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(this, "Hãy nhập gì đó...", Toast.LENGTH_SHORT).show();
            } else {
                Intent result = new Intent();
                result.putExtra("post_content", content);
                result.putExtra("post_username", tvUserName.getText().toString());
                result.putExtra("post_time", "Vừa xong");
                setResult(RESULT_OK, result);
                finish();
            }
        });

        // 4. Các icon phụ
        ivAddImage.setOnClickListener(v -> Toast.makeText(this, "Thêm ảnh", Toast.LENGTH_SHORT).show());
        ivAddLocation.setOnClickListener(v -> Toast.makeText(this, "Vị trí", Toast.LENGTH_SHORT).show());
        ivTagInterest.setOnClickListener(v -> Toast.makeText(this, "Sở thích", Toast.LENGTH_SHORT).show());

        // 5. Xử lý nút back hệ thống
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleExit();
            }
        });
    }

    private void handleExit() {
        if (!etPostContent.getText().toString().trim().isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Hủy bài viết?")
                    .setMessage("Nội dung sẽ không được lưu.")
                    .setPositiveButton("Hủy bỏ", (d, w) -> finish())
                    .setNegativeButton("Viết tiếp", null)
                    .show();
        } else {
            finish();
        }
    }
}