package com.example.weconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.adapters.PostAdapter;
import com.example.weconnect.adapters.UserReviewAdapter;
import com.example.weconnect.data.FakePostRepository;
import com.example.weconnect.data.FakeSocialRepository;
import com.example.weconnect.models.UserProfile;
import com.example.weconnect.models.Post;
import com.example.weconnect.models.UserReview;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView ivBackUserProfile;
    private ImageView ivMenuProfile;
    private ImageView ivUserProfileAvatar;
    private TextView tvUserProfileName;
    private TextView tvUserReputation;
    private TextView tvUserAverageRating;
    private MaterialButton btnAddFriend;
    private MaterialButton btnMessage;
    private MaterialButton btnViewArchive;
    private LinearLayout layoutSocialButtons;
    private TextView tvFriendCount;
    private RecyclerView rvUserReviews;
    private ChipGroup chipGroupUserInterests;
    private View footerNavigationProfile;

    private DrawerLayout drawerLayoutProfile;
    private LinearLayout menuEditProfile;
    private LinearLayout menuChangePassword;
    private LinearLayout menuDeleteAccount;

    private RecyclerView rvActivePostsProfile;
    private TextView tvNoActivePosts;

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
        setupDrawerMenu();
        bindActivePosts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh state khi quay lại (vd: sau khi chấp nhận kết bạn từ thông báo)
        bindSocialState();
    }

    private void initViews() {
        ivBackUserProfile = findViewById(R.id.ivBackUserProfile);
        ivMenuProfile = findViewById(R.id.ivMenuProfile);
        ivUserProfileAvatar = findViewById(R.id.ivUserProfileAvatar);
        tvUserProfileName = findViewById(R.id.tvUserProfileName);
        tvUserReputation = findViewById(R.id.tvUserReputation);
        tvUserAverageRating = findViewById(R.id.tvUserAverageRating);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        btnMessage = findViewById(R.id.btnMessage);
        btnViewArchive = findViewById(R.id.btnViewArchive);
        layoutSocialButtons = findViewById(R.id.layoutSocialButtons);
        tvFriendCount = findViewById(R.id.tvFriendCount);
        rvUserReviews = findViewById(R.id.rvUserReviews);
        chipGroupUserInterests = findViewById(R.id.chipGroupUserInterests);
        footerNavigationProfile = findViewById(R.id.footerNavigationProfile);

        drawerLayoutProfile = findViewById(R.id.drawerLayoutProfile);
        menuEditProfile = findViewById(R.id.menuEditProfile);
        menuChangePassword = findViewById(R.id.menuChangePassword);
        menuDeleteAccount = findViewById(R.id.menuDeleteAccount);

        rvActivePostsProfile = findViewById(R.id.rvActivePostsProfile);
        tvNoActivePosts = findViewById(R.id.tvNoActivePosts);
    }

    private void setupClickListeners() {
        ivBackUserProfile.setOnClickListener(v -> finish());

        btnViewArchive.setOnClickListener(v -> {
            Intent intent = new Intent(this, ArchivePostsActivity.class);
            intent.putExtra("username", tvUserProfileName.getText().toString());
            startActivity(intent);
        });

        // Bottom navigation click listeners
        View btnHome = findViewById(R.id.btnHomeProfile);
        View btnMessages = findViewById(R.id.btnMessagesProfile);
        View btnNotifications = findViewById(R.id.btnNotificationsProfile);

        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }
        if (btnMessages != null) {
            btnMessages.setOnClickListener(v -> {
                Toast.makeText(this, "Tin nhắn", Toast.LENGTH_SHORT).show();
            });
        }
        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(v -> {
                startActivity(new Intent(this, NotificationsActivity.class));
            });
        }
    }

    private void setupDrawerMenu() {
        drawerLayoutProfile.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        ivMenuProfile.setOnClickListener(v ->
                drawerLayoutProfile.openDrawer(Gravity.END)
        );

        menuEditProfile.setOnClickListener(v -> {
            drawerLayoutProfile.closeDrawer(Gravity.END);
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        menuChangePassword.setOnClickListener(v -> {
            drawerLayoutProfile.closeDrawer(Gravity.END);
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        menuDeleteAccount.setOnClickListener(v -> {
            drawerLayoutProfile.closeDrawer(Gravity.END);
            showDeleteAccountDialog();
        });
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xoá tài khoản")
                .setMessage("Bạn có chắc chắn muốn xoá tài khoản? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    Toast.makeText(this, "Đã gửi yêu cầu xoá tài khoản", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void bindFakeUserProfile() {
        username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            username = socialRepository.getCurrentUsername();
        }

        List<String> interestTags = new ArrayList<>();

        if (socialRepository.getCurrentUsername().equalsIgnoreCase(username)) {
            interestTags.add("Cà phê");
            interestTags.add("Chơi game");
            interestTags.add("Xem phim");
        } else if ("Minh Hoàng".equalsIgnoreCase(username)) {
            interestTags.add("Đá bóng");
            interestTags.add("Cầu lông");
            interestTags.add("Chạy bộ");
        } else if ("Lan Anh".equalsIgnoreCase(username)) {
            interestTags.add("Học nhóm");
            interestTags.add("Câu lạc bộ tiếng Anh");
            interestTags.add("Cà phê");
        } else if ("Thu Hương".equalsIgnoreCase(username)) {
            interestTags.add("Yoga");
            interestTags.add("Nấu ăn");
            interestTags.add("Du lịch");
        } else {
            interestTags.add("Cà phê");
            interestTags.add("Lập trình");
        }

        List<UserReview> reviews = new ArrayList<>();
        reviews.add(new UserReview("Minh Hoàng", 4.5f, "Thân thiện, dễ phối hợp."));
        reviews.add(new UserReview("Lan Anh", 5.0f, "Rất năng động, truyền cảm hứng cho nhóm."));
        reviews.add(new UserReview("Hải Đăng", 4.0f, "Lịch sự, đúng giờ."));

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
        tvUserReputation.setText(String.valueOf(userProfile.getReputationScore()));
        tvUserAverageRating.setText(String.valueOf(userProfile.getAverageRating()));
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

    private void bindActivePosts() {
        List<Post> activePosts = FakePostRepository.getInstance().getActivePostsForUser(username);

        if (activePosts.isEmpty()) {
            tvNoActivePosts.setVisibility(View.VISIBLE);
            rvActivePostsProfile.setVisibility(View.GONE);
        } else {
            tvNoActivePosts.setVisibility(View.GONE);
            rvActivePostsProfile.setVisibility(View.VISIBLE);
            rvActivePostsProfile.setLayoutManager(new LinearLayoutManager(this));
            rvActivePostsProfile.setAdapter(new PostAdapter(this, activePosts));
        }
    }

    private void bindSocialState() {
        FakeSocialRepository.SocialState state = socialRepository.getState(username);

        if (state.isSelfProfile()) {
            // Hồ sơ của mình: ẩn close, hiện menu ☰, hiện bạn bè + kho lưu trữ
            ivBackUserProfile.setVisibility(View.GONE);
            ivMenuProfile.setVisibility(View.VISIBLE);
            tvFriendCount.setVisibility(View.VISIBLE);
            tvFriendCount.setText("👥 Bạn bè: " + socialRepository.getFriendCount());
            layoutSocialButtons.setVisibility(View.GONE);
            btnViewArchive.setVisibility(View.VISIBLE);
            ivMenuProfile.setVisibility(View.VISIBLE);
            footerNavigationProfile.setVisibility(View.VISIBLE);
            return;
        }

        // Hồ sơ người khác: hiện close, ẩn menu
        ivBackUserProfile.setVisibility(View.VISIBLE);
        ivMenuProfile.setVisibility(View.GONE);
        tvFriendCount.setVisibility(View.GONE);
        btnViewArchive.setVisibility(View.GONE);
        layoutSocialButtons.setVisibility(View.VISIBLE);
        footerNavigationProfile.setVisibility(View.GONE);

        FakeSocialRepository.FriendStatus status = state.getFriendStatus();

        switch (status) {
            case BLOCKED:
                btnAddFriend.setText("Đã chặn");
                btnAddFriend.setEnabled(false);
                btnAddFriend.setAlpha(0.5f);
                btnMessage.setVisibility(View.GONE);
                break;

            case FRIEND:
                btnAddFriend.setText("Bạn bè");
                btnAddFriend.setEnabled(true);
                btnAddFriend.setAlpha(1.0f);
                btnMessage.setVisibility(View.VISIBLE);

                btnAddFriend.setOnClickListener(v -> showFriendOptionsMenu());
                btnMessage.setOnClickListener(v -> {
                    Intent intent = new Intent(this, ConversationActivity.class);
                    intent.putExtra("chat_name", username);
                    startActivity(intent);
                });
                break;

            case PENDING_SENT:
                btnAddFriend.setText("Đã gửi lời mời");
                btnAddFriend.setEnabled(false);
                btnAddFriend.setAlpha(0.6f);
                btnMessage.setVisibility(View.GONE);
                break;

            case PENDING_RECEIVED:
                btnAddFriend.setText("Chấp nhận kết bạn");
                btnAddFriend.setEnabled(true);
                btnAddFriend.setAlpha(1.0f);
                btnMessage.setVisibility(View.GONE);

                btnAddFriend.setOnClickListener(v -> {
                    socialRepository.acceptFriendRequest(username);
                    bindSocialState();
                    Toast.makeText(this, "Đã chấp nhận kết bạn!", Toast.LENGTH_SHORT).show();
                });
                break;

            default: // NONE
                btnAddFriend.setText("+ Thêm bạn bè");
                btnAddFriend.setEnabled(true);
                btnAddFriend.setAlpha(1.0f);
                btnMessage.setVisibility(View.GONE);

                btnAddFriend.setOnClickListener(v -> {
                    socialRepository.sendFriendRequest(username);
                    bindSocialState();
                    Toast.makeText(this, "Đã gửi lời mời kết bạn", Toast.LENGTH_SHORT).show();
                });
                break;
        }
    }

    private void showFriendOptionsMenu() {
        String[] items = {"Huỷ kết bạn", "Chặn người dùng"};
        new AlertDialog.Builder(this)
                .setTitle("Tuỳ chọn")
                .setItems(items, (dialog, which) -> {
                    if (which == 0) {
                        socialRepository.unfriend(username);
                        bindSocialState();
                        Toast.makeText(this, "Đã huỷ kết bạn", Toast.LENGTH_SHORT).show();
                    } else {
                        socialRepository.blockUser(username);
                        bindSocialState();
                        Toast.makeText(this, "Đã chặn người dùng", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}
