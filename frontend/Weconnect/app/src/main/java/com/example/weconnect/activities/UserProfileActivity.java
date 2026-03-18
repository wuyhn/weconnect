package com.example.weconnect.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.adapters.UserReviewAdapter;
import com.example.weconnect.models.UserProfile;
import com.example.weconnect.models.UserReview;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView ivBackUserProfile;
    private ImageView ivUserProfileAvatar;
    private TextView tvUserProfileName;
    private TextView tvUserReputation;
    private TextView tvUserAverageRating;
    private MaterialButton btnAddFriend;
    private MaterialButton btnReportUser;
    private RecyclerView rvUserReviews;
    private ChipGroup chipGroupUserInterests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initViews();
        setupClickListeners();
        bindFakeUserProfile();
    }

    private void initViews() {
        ivBackUserProfile = findViewById(R.id.ivBackUserProfile);
        ivUserProfileAvatar = findViewById(R.id.ivUserProfileAvatar);
        tvUserProfileName = findViewById(R.id.tvUserProfileName);
        tvUserReputation = findViewById(R.id.tvUserReputation);
        tvUserAverageRating = findViewById(R.id.tvUserAverageRating);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        btnReportUser = findViewById(R.id.btnReportUser);
        rvUserReviews = findViewById(R.id.rvUserReviews);
        chipGroupUserInterests = findViewById(R.id.chipGroupUserInterests);
    }

    private void setupClickListeners() {
        ivBackUserProfile.setOnClickListener(v -> finish());

        btnAddFriend.setOnClickListener(v ->
                Toast.makeText(this, "Đã gửi lời mời kết bạn", Toast.LENGTH_SHORT).show()
        );

        btnReportUser.setOnClickListener(v ->
                Toast.makeText(this, "Đã gửi báo cáo tới admin", Toast.LENGTH_SHORT).show()
        );
    }

    private void bindFakeUserProfile() {
        String username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            username = "Người dùng";
        }
        List<String> interestTags = new ArrayList<>();

        if ("Quỳnh Nguyễn".equalsIgnoreCase(username)) {
            interestTags.add("☕ Đi Cafe");
            interestTags.add("🎮 Chơi Game");
            interestTags.add("🍿 Xem phim");
        } else if ("Minh Hoàng".equalsIgnoreCase(username)) {
            interestTags.add("⚽ Đá bóng");
            interestTags.add("🏸 Cầu lông");
            interestTags.add("🏃 Chạy bộ");
        } else if ("Lan Anh".equalsIgnoreCase(username)) {
            interestTags.add("📖 Học bài");
            interestTags.add("🇬🇧 Học Tiếng Anh");
            interestTags.add("☕ Đi Cafe");
        } else {
            interestTags.add("☕ Đi Cafe");
            interestTags.add("💻 Đồ họa / Code");
        }
        List<UserReview> reviews = new ArrayList<>();
        reviews.add(new UserReview("Minh Hoàng", 4.5f, "Thân thiện, đúng giờ và dễ hợp tác."));
        reviews.add(new UserReview("Lan Anh", 5.0f, "Rất nhiệt tình, tham gia hoạt động vui vẻ."));
        reviews.add(new UserReview("Hải Đăng", 4.0f, "Ổn áp, giao tiếp tốt và lịch sự."));

        UserProfile userProfile = new UserProfile(
                username,
                R.drawable.ic_user_placeholder,
                4.8f,
                92,
                interestTags,
                reviews
        );

        ivUserProfileAvatar.setImageResource(userProfile.getAvatarResId());
        tvUserProfileName.setText(userProfile.getUsername());
        tvUserReputation.setText("Uy tín: " + userProfile.getReputationScore());
        tvUserAverageRating.setText("⭐ " + userProfile.getAverageRating());
        chipGroupUserInterests.removeAllViews();

        for (String tag : userProfile.getInterestTags()) {
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setClickable(false);
            chip.setCheckable(false);
            chip.setFocusable(false);
            chip.setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Body2);
            chip.setChipBackgroundColorResource(R.color.chip_background_state);
            chip.setTextColor(getResources().getColorStateList(R.color.chip_text_state, getTheme()));
            chip.setChipCornerRadius(getResources().getDimension(R.dimen.profile_interest_chip_radius));
            chip.setChipStrokeWidth(0f);

            chipGroupUserInterests.addView(chip);
        }
        rvUserReviews.setLayoutManager(new LinearLayoutManager(this));
        rvUserReviews.setAdapter(new UserReviewAdapter(userProfile.getReviews()));
    }
}