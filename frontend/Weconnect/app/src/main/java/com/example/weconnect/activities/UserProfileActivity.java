package com.example.weconnect.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
    private MaterialButton btnAddFriend;
    private MaterialButton btnMessage;
    private MaterialButton btnViewArchive;
    private MaterialButton btnRateUser;
    private MaterialButton btnReportUser;
    private LinearLayout layoutSocialButtons;
    private LinearLayout layoutRateReport;
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
    private TextView tvInterestsTitle;
    private View cardInterests;
    private TextView tvActivePostsTitle;
    private View cardCreatePostProfile;
    private TextView tvCreatePostHint;
    private TextView tvReviewsTitle;

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
        btnAddFriend = findViewById(R.id.btnAddFriend);
        btnMessage = findViewById(R.id.btnMessage);
        btnViewArchive = findViewById(R.id.btnViewArchive);
        btnRateUser = findViewById(R.id.btnRateUser);
        btnReportUser = findViewById(R.id.btnReportUser);
        layoutSocialButtons = findViewById(R.id.layoutSocialButtons);
        layoutRateReport = findViewById(R.id.layoutRateReport);
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
        tvInterestsTitle = findViewById(R.id.tvInterestsTitle);
        cardInterests = findViewById(R.id.cardInterests);
        tvActivePostsTitle = findViewById(R.id.tvActivePostsTitle);
        cardCreatePostProfile = findViewById(R.id.cardCreatePostProfile);
        tvCreatePostHint = findViewById(R.id.tvCreatePostHint);
        tvReviewsTitle = findViewById(R.id.tvReviewsTitle);
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
                Intent intent = new Intent(this, ChatListActivity.class);
                startActivity(intent);
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

        LinearLayout menuLogout = findViewById(R.id.menuLogout);
        menuLogout.setOnClickListener(v -> {
            drawerLayoutProfile.closeDrawer(Gravity.END);
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc muốn đăng xuất?")
                    .setPositiveButton("Đăng xuất", (d, w) -> {
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });

        LinearLayout menuBlockedUsers = findViewById(R.id.menuBlockedUsers);
        menuBlockedUsers.setOnClickListener(v -> {
            drawerLayoutProfile.closeDrawer(Gravity.END);
            startActivity(new Intent(this, BlockedUsersActivity.class));
        });

        LinearLayout menuLegal = findViewById(R.id.menuLegal);
        menuLegal.setOnClickListener(v -> {
            drawerLayoutProfile.closeDrawer(Gravity.END);
            startActivity(new Intent(this, LegalActivity.class));
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
        reviews.add(new UserReview("Minh Hoàng", "Coffee Meetup", "Tích cực", "Thân thiện, dễ phối hợp."));
        reviews.add(new UserReview("Lan Anh", "Design and Code Crew", "Đáng tin cậy", "Rất năng động, truyền cảm hứng cho nhóm."));
        reviews.add(new UserReview("Hải Đăng", "Đá bóng cuối tuần", "Hợp tác tốt", "Lịch sự, đúng giờ."));

        UserProfile userProfile = new UserProfile(
                username,
                R.drawable.ic_user_placeholder,
                0f,
                92,
                interestTags,
                reviews
        );

        ivUserProfileAvatar.setImageResource(userProfile.getAvatarResId());
        tvUserProfileName.setText(userProfile.getUsername());
        tvUserReputation.setText(String.valueOf(userProfile.getReputationScore()));
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
            layoutRateReport.setVisibility(View.GONE);
            btnViewArchive.setVisibility(View.VISIBLE);
            ivMenuProfile.setVisibility(View.VISIBLE);
            footerNavigationProfile.setVisibility(View.VISIBLE);

            // Avatar click → show options
            ivUserProfileAvatar.setOnClickListener(v -> showAvatarOptionsSheet());

            // Show create post section for self profile
            cardCreatePostProfile.setVisibility(View.VISIBLE);
            cardCreatePostProfile.setOnClickListener(v -> {
                startActivity(new Intent(this, CreatePostActivity.class));
            });

            // Show interests and posts
            tvInterestsTitle.setVisibility(View.VISIBLE);
            cardInterests.setVisibility(View.VISIBLE);
            tvActivePostsTitle.setVisibility(View.VISIBLE);
            rvActivePostsProfile.setVisibility(View.VISIBLE);
            return;
        }

        // Hồ sơ người khác: hiện close, ẩn menu
        ivBackUserProfile.setVisibility(View.VISIBLE);
        ivMenuProfile.setVisibility(View.GONE);
        tvFriendCount.setVisibility(View.GONE);
        btnViewArchive.setVisibility(View.GONE);
        layoutSocialButtons.setVisibility(View.VISIBLE);
        layoutRateReport.setVisibility(View.VISIBLE);
        footerNavigationProfile.setVisibility(View.GONE);
        cardCreatePostProfile.setVisibility(View.GONE);

        // Default: show interests and posts (will be hidden for blocked)
        tvInterestsTitle.setVisibility(View.VISIBLE);
        cardInterests.setVisibility(View.VISIBLE);
        tvActivePostsTitle.setVisibility(View.VISIBLE);
        rvActivePostsProfile.setVisibility(View.VISIBLE);
        tvReviewsTitle.setVisibility(View.VISIBLE);
        rvUserReviews.setVisibility(View.VISIBLE);

        // Rate & Report click listeners
        btnRateUser.setOnClickListener(v -> showRateUserDialog());
        btnReportUser.setOnClickListener(v -> showReportUserDialog());

        // Avatar click disabled for other profile
        ivUserProfileAvatar.setOnClickListener(null);
        ivUserProfileAvatar.setClickable(false);

        FakeSocialRepository.FriendStatus status = state.getFriendStatus();

        switch (status) {
            case BLOCKED:
                btnAddFriend.setText("Đã chặn");
                btnAddFriend.setEnabled(false);
                btnAddFriend.setAlpha(0.5f);
                btnMessage.setVisibility(View.GONE);
                // Hide interests and active posts for blocked user
                tvInterestsTitle.setVisibility(View.GONE);
                cardInterests.setVisibility(View.GONE);
                tvActivePostsTitle.setVisibility(View.GONE);
                rvActivePostsProfile.setVisibility(View.GONE);
                tvNoActivePosts.setVisibility(View.GONE);
                tvReviewsTitle.setVisibility(View.GONE);
                rvUserReviews.setVisibility(View.GONE);
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
        com.google.android.material.bottomsheet.BottomSheetDialog sheet =
                new com.google.android.material.bottomsheet.BottomSheetDialog(this);

        android.widget.LinearLayout root = new android.widget.LinearLayout(this);
        root.setOrientation(android.widget.LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(R.color.card_surface, null));
        root.setPadding(0, 0, 0, 48);

        // Header
        android.widget.TextView header = new android.widget.TextView(this);
        header.setText("Tuỳ chọn bạn bè");
        header.setTextSize(20);
        header.setTextColor(getResources().getColor(R.color.primary_pink, null));
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setGravity(Gravity.CENTER);
        header.setPadding(0, 48, 0, 24);
        root.addView(header);

        // Divider
        View div = new View(this);
        div.setBackgroundColor(0xFFE8E4DE);
        div.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 2));
        root.addView(div);

        // Unfriend option
        android.widget.LinearLayout unfriendRow = createOptionRow(
                "👋", "Huỷ kết bạn", "Xoá " + username + " khỏi danh sách bạn bè",
                getResources().getColor(R.color.text_primary, null));
        unfriendRow.setOnClickListener(v -> {
            sheet.dismiss();
            new AlertDialog.Builder(this)
                    .setTitle("Huỷ kết bạn")
                    .setMessage("Bạn có chắc muốn huỷ kết bạn với " + username + "?")
                    .setPositiveButton("Huỷ kết bạn", (d, w) -> {
                        socialRepository.unfriend(username);
                        bindSocialState();
                        Toast.makeText(this, "Đã huỷ kết bạn", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
        root.addView(unfriendRow);

        // Divider
        View div2 = new View(this);
        div2.setBackgroundColor(0xFFE8E4DE);
        div2.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 1));
        root.addView(div2);

        // Block option
        android.widget.LinearLayout blockRow = createOptionRow(
                "🚫", "Chặn người dùng", username + " sẽ không thể liên hệ với bạn",
                0xFFFF4D6D);
        blockRow.setOnClickListener(v -> {
            sheet.dismiss();
            new AlertDialog.Builder(this)
                    .setTitle("Chặn người dùng")
                    .setMessage("Bạn có chắc muốn chặn " + username + "? Người này sẽ không thể liên hệ với bạn.")
                    .setPositiveButton("Chặn", (d, w) -> {
                        socialRepository.blockUser(username);
                        bindSocialState();
                        Toast.makeText(this, "Đã chặn người dùng", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
        root.addView(blockRow);

        sheet.setContentView(root);
        sheet.show();
    }

    private android.widget.LinearLayout createOptionRow(String icon, String title, String subtitle, int titleColor) {
        android.widget.LinearLayout row = new android.widget.LinearLayout(this);
        row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(64, 36, 64, 36);
        row.setBackgroundResource(android.R.drawable.list_selector_background);
        row.setClickable(true);

        android.widget.TextView tvIcon = new android.widget.TextView(this);
        tvIcon.setText(icon);
        tvIcon.setTextSize(24);
        row.addView(tvIcon);

        android.widget.LinearLayout textCol = new android.widget.LinearLayout(this);
        textCol.setOrientation(android.widget.LinearLayout.VERTICAL);
        textCol.setPadding(32, 0, 0, 0);

        android.widget.TextView tvTitle = new android.widget.TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(16);
        tvTitle.setTextColor(titleColor);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        textCol.addView(tvTitle);

        android.widget.TextView tvSubtitle = new android.widget.TextView(this);
        tvSubtitle.setText(subtitle);
        tvSubtitle.setTextSize(12);
        tvSubtitle.setTextColor(getResources().getColor(R.color.text_secondary, null));
        textCol.addView(tvSubtitle);

        row.addView(textCol);
        return row;
    }

    private void showRateUserDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog sheet =
                new com.google.android.material.bottomsheet.BottomSheetDialog(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(R.color.card_surface, null));
        root.setPadding(64, 48, 64, 48);

        // Header
        TextView header = new TextView(this);
        header.setText("⭐ Đánh giá " + username);
        header.setTextSize(20);
        header.setTextColor(getResources().getColor(R.color.primary_pink, null));
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setGravity(Gravity.CENTER);
        root.addView(header);

        // Rating stars
        RatingBar ratingBar = new RatingBar(this, null, android.R.attr.ratingBarStyle);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1f);
        ratingBar.setRating(0f);
        LinearLayout.LayoutParams ratingParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ratingParams.gravity = Gravity.CENTER;
        ratingParams.topMargin = 32;
        ratingBar.setLayoutParams(ratingParams);
        root.addView(ratingBar);

        // Comment input
        EditText etComment = new EditText(this);
        etComment.setHint("Nhận xét (không bắt buộc)");
        etComment.setBackground(new ColorDrawable(Color.TRANSPARENT));
        etComment.setBackgroundResource(android.R.drawable.edit_text);
        etComment.setPadding(24, 24, 24, 24);
        etComment.setTextSize(14);
        etComment.setMinLines(2);
        LinearLayout.LayoutParams commentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        commentParams.topMargin = 32;
        etComment.setLayoutParams(commentParams);
        root.addView(etComment);

        // Submit button
        MaterialButton btnSubmit = new MaterialButton(this);
        btnSubmit.setText("Gửi đánh giá");
        btnSubmit.setAllCaps(false);
        btnSubmit.setCornerRadius(48);
        btnSubmit.setBackgroundTintList(getResources().getColorStateList(R.color.primary_pink, null));
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 120);
        btnParams.topMargin = 32;
        btnSubmit.setLayoutParams(btnParams);
        btnSubmit.setOnClickListener(v -> {
            if (ratingBar.getRating() == 0) {
                Toast.makeText(this, "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
                return;
            }
            sheet.dismiss();
            Toast.makeText(this, "Đã gửi đánh giá " + (int) ratingBar.getRating() + " sao cho " + username, Toast.LENGTH_SHORT).show();
        });
        root.addView(btnSubmit);

        sheet.setContentView(root);
        sheet.show();
    }

    private void showReportUserDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog sheet =
                new com.google.android.material.bottomsheet.BottomSheetDialog(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(R.color.card_surface, null));
        root.setPadding(64, 48, 64, 48);

        // Header
        TextView header = new TextView(this);
        header.setText("🚩 Báo cáo " + username);
        header.setTextSize(20);
        header.setTextColor(getResources().getColor(R.color.danger_red, null));
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setGravity(Gravity.CENTER);
        root.addView(header);

        // Subtitle
        TextView subtitle = new TextView(this);
        subtitle.setText("Chọn lý do báo cáo:");
        subtitle.setTextSize(14);
        subtitle.setTextColor(getResources().getColor(R.color.text_secondary, null));
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subParams.topMargin = 24;
        subtitle.setLayoutParams(subParams);
        root.addView(subtitle);

        String[] reasons = {
                "Hành vi không phù hợp",
                "Nội dung phản cảm",
                "Lừa đảo / spam",
                "Quấy rối người khác",
                "Thông tin giả mạo",
                "Lý do khác"
        };

        final int[] selectedIndex = {-1};
        final TextView[] reasonViews = new TextView[reasons.length];

        for (int i = 0; i < reasons.length; i++) {
            final int index = i;
            TextView tvReason = new TextView(this);
            tvReason.setText(reasons[i]);
            tvReason.setTextSize(15);
            tvReason.setTextColor(getResources().getColor(R.color.text_primary, null));
            tvReason.setPadding(32, 28, 32, 28);
            tvReason.setBackgroundResource(android.R.drawable.list_selector_background);
            tvReason.setClickable(true);

            LinearLayout.LayoutParams reasonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            reasonParams.topMargin = 8;
            tvReason.setLayoutParams(reasonParams);

            tvReason.setOnClickListener(v -> {
                // Deselect previous
                if (selectedIndex[0] >= 0) {
                    reasonViews[selectedIndex[0]].setTextColor(getResources().getColor(R.color.text_primary, null));
                    reasonViews[selectedIndex[0]].setTypeface(null, android.graphics.Typeface.NORMAL);
                }
                selectedIndex[0] = index;
                tvReason.setTextColor(getResources().getColor(R.color.danger_red, null));
                tvReason.setTypeface(null, android.graphics.Typeface.BOLD);
            });

            reasonViews[i] = tvReason;
            root.addView(tvReason);
        }

        // Submit button
        MaterialButton btnSubmit = new MaterialButton(this);
        btnSubmit.setText("Gửi báo cáo");
        btnSubmit.setAllCaps(false);
        btnSubmit.setCornerRadius(48);
        btnSubmit.setBackgroundTintList(getResources().getColorStateList(R.color.danger_red, null));
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 120);
        btnParams.topMargin = 32;
        btnSubmit.setLayoutParams(btnParams);
        btnSubmit.setOnClickListener(v -> {
            if (selectedIndex[0] < 0) {
                Toast.makeText(this, "Vui lòng chọn lý do báo cáo", Toast.LENGTH_SHORT).show();
                return;
            }
            sheet.dismiss();
            Toast.makeText(this, "Đã gửi báo cáo về " + username + ". Cảm ơn bạn!", Toast.LENGTH_SHORT).show();
        });
        root.addView(btnSubmit);

        sheet.setContentView(root);
        sheet.show();
    }

    private void showAvatarOptionsSheet() {
        com.google.android.material.bottomsheet.BottomSheetDialog sheet =
                new com.google.android.material.bottomsheet.BottomSheetDialog(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(R.color.card_surface, null));
        root.setPadding(0, 0, 0, 48);

        // Header
        TextView header = new TextView(this);
        header.setText("Ảnh đại diện");
        header.setTextSize(20);
        header.setTextColor(getResources().getColor(R.color.primary_pink, null));
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setGravity(Gravity.CENTER);
        header.setPadding(0, 48, 0, 24);
        root.addView(header);

        // Divider
        View div = new View(this);
        div.setBackgroundColor(0xFFE8E4DE);
        div.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2));
        root.addView(div);

        // View avatar option
        LinearLayout viewRow = createOptionRow(
                "📷", "Xem ảnh đại diện", "Xem ảnh toàn màn hình",
                getResources().getColor(R.color.text_primary, null));
        viewRow.setOnClickListener(v -> {
            sheet.dismiss();
            // Show full-screen avatar dialog
            Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(ivUserProfileAvatar.getDrawable());
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setBackgroundColor(Color.BLACK);
            imageView.setOnClickListener(v2 -> dialog.dismiss());
            dialog.setContentView(imageView);
            dialog.show();
        });
        root.addView(viewRow);

        // Divider
        View div2 = new View(this);
        div2.setBackgroundColor(0xFFE8E4DE);
        div2.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        root.addView(div2);

        // Choose from gallery option
        LinearLayout galleryRow = createOptionRow(
                "🖼", "Chọn ảnh đại diện từ thư viện", "Chọn ảnh mới từ bộ sưu tập",
                getResources().getColor(R.color.text_primary, null));
        galleryRow.setOnClickListener(v -> {
            sheet.dismiss();
            Intent pickIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickIntent, 1001);
        });
        root.addView(galleryRow);

        sheet.setContentView(root);
        sheet.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            android.net.Uri selectedImage = data.getData();
            if (selectedImage != null) {
                ivUserProfileAvatar.setImageURI(selectedImage);
                Toast.makeText(this, "Đã cập nhật ảnh đại diện!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
