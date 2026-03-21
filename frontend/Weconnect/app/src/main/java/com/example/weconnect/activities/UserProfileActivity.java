package com.example.weconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.adapters.UserReviewAdapter;
import com.example.weconnect.data.FakePostRepository;
import com.example.weconnect.data.FakeSocialRepository;
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
    private MaterialButton btnToggleBlock;
    private MaterialButton btnReportUser;
    private MaterialButton btnViewArchive;
    private RecyclerView rvUserReviews;
    private ChipGroup chipGroupUserInterests;

    private String username;
    private FakeSocialRepository socialRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        socialRepository = FakeSocialRepository.getInstance();
        initViews();
        bindFakeUserProfile();
        setupClickListeners();
        bindSocialState();
    }

    private void initViews() {
        ivBackUserProfile = findViewById(R.id.ivBackUserProfile);
        ivUserProfileAvatar = findViewById(R.id.ivUserProfileAvatar);
        tvUserProfileName = findViewById(R.id.tvUserProfileName);
        tvUserReputation = findViewById(R.id.tvUserReputation);
        tvUserAverageRating = findViewById(R.id.tvUserAverageRating);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        btnToggleBlock = findViewById(R.id.btnToggleBlock);
        btnReportUser = findViewById(R.id.btnReportUser);
        btnViewArchive = findViewById(R.id.btnViewArchive);
        rvUserReviews = findViewById(R.id.rvUserReviews);
        chipGroupUserInterests = findViewById(R.id.chipGroupUserInterests);
    }

    private void setupClickListeners() {
        ivBackUserProfile.setOnClickListener(v -> finish());

        btnAddFriend.setOnClickListener(v -> {
            socialRepository.toggleFriend(username);
            bindSocialState();
            FakeSocialRepository.SocialState state = socialRepository.getState(username);
            Toast.makeText(this, state.isFriend() ? "Da ket ban" : "Da huy ket ban", Toast.LENGTH_SHORT).show();
        });

        btnToggleBlock.setOnClickListener(v -> {
            socialRepository.toggleBlocked(username);
            bindSocialState();
            FakeSocialRepository.SocialState state = socialRepository.getState(username);
            Toast.makeText(this, state.isBlocked() ? "Da chan nguoi dung" : "Da bo chan nguoi dung", Toast.LENGTH_SHORT).show();
        });

        btnReportUser.setOnClickListener(v -> showReportDialog());

        btnViewArchive.setOnClickListener(v -> {
            Intent intent = new Intent(this, ArchivePostsActivity.class);
            intent.putExtra("username", tvUserProfileName.getText().toString());
            startActivity(intent);
        });
    }

    private void bindFakeUserProfile() {
        username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            username = FakePostRepository.getInstance().getCurrentUsername();
        }

        List<String> interestTags = new ArrayList<>();

        if ("Quynh Nguyen".equalsIgnoreCase(username)) {
            interestTags.add("Coffee meetup");
            interestTags.add("Gaming");
            interestTags.add("Movies");
        } else if ("Minh Hoang".equalsIgnoreCase(username)) {
            interestTags.add("Football");
            interestTags.add("Badminton");
            interestTags.add("Running");
        } else if ("Lan Anh".equalsIgnoreCase(username)) {
            interestTags.add("Study group");
            interestTags.add("English club");
            interestTags.add("Coffee meetup");
        } else {
            interestTags.add("Coffee meetup");
            interestTags.add("Design and code");
        }

        List<UserReview> reviews = new ArrayList<>();
        reviews.add(new UserReview("Minh Hoang", 4.5f, "Friendly and easy to coordinate with."));
        reviews.add(new UserReview("Lan Anh", 5.0f, "Very active and keeps the group motivated."));
        reviews.add(new UserReview("Hai Dang", 4.0f, "Polite and shows up on time."));

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
        tvUserReputation.setText("Uy tin: " + userProfile.getReputationScore());
        tvUserAverageRating.setText("Rating: " + userProfile.getAverageRating());
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

    private void bindSocialState() {
        FakeSocialRepository.SocialState state = socialRepository.getState(username);

        if (state.isSelfProfile()) {
            btnAddFriend.setText("Day la ho so cua ban");
            btnAddFriend.setEnabled(false);
            btnAddFriend.setAlpha(0.6f);
            btnToggleBlock.setVisibility(View.GONE);
            btnReportUser.setVisibility(View.GONE);
            return;
        }

        btnToggleBlock.setVisibility(View.VISIBLE);
        btnReportUser.setVisibility(View.VISIBLE);
        btnAddFriend.setEnabled(!state.isBlocked());
        btnAddFriend.setAlpha(state.isBlocked() ? 0.6f : 1.0f);

        if (state.isBlocked()) {
            btnAddFriend.setText("Bi chan");
            btnToggleBlock.setText("Bo chan");
        } else if (state.isFriend()) {
            btnAddFriend.setText("Ban be");
            btnToggleBlock.setText("Chan");
        } else {
            btnAddFriend.setText("Ket ban");
            btnToggleBlock.setText("Chan");
        }
    }

    private void showReportDialog() {
        String[] reasons = new String[] {
                "Hanh vi thieu ton trong",
                "Spam hoac lam phien",
                "Thong tin gia mao",
                "Noi dung khong phu hop"
        };

        new AlertDialog.Builder(this)
                .setTitle("Bao cao nguoi dung")
                .setItems(reasons, (dialog, which) ->
                        Toast.makeText(this, "Da gui bao cao: " + reasons[which], Toast.LENGTH_SHORT).show()
                )
                .setNegativeButton("Huy", null)
                .show();
    }
}
