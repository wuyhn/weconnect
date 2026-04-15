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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.adapters.PostAdapter;
import com.example.weconnect.adapters.UserReviewAdapter;
import com.example.weconnect.api.PostApiService;
import com.example.weconnect.api.ReviewApiService;
import com.example.weconnect.api.RetrofitClient;
import com.example.weconnect.data.FakePostRepository;
import com.example.weconnect.data.FakeSocialRepository;
import com.example.weconnect.models.ApiResponse;
import com.example.weconnect.models.UserProfile;
import com.example.weconnect.models.Post;
import com.example.weconnect.models.PostResponse;
import com.example.weconnect.models.UserReview;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    // Related posts (from other users matching interest tags)
    private TextView tvRelatedPostsTitle;
    private TextView tvNoRelatedPosts;
    private RecyclerView rvRelatedPosts;

    private String username;
    private FakeSocialRepository socialRepository;
    private PostApiService postApiService;
    private ReviewApiService reviewApiService;
    private ActivityResultLauncher<Intent> createPostLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        socialRepository = FakeSocialRepository.getInstance();
        postApiService = RetrofitClient.getClient().create(PostApiService.class);
        reviewApiService = RetrofitClient.getClient().create(ReviewApiService.class);
        setupCreatePostLauncher();
        initViews();
        bindFakeUserProfile();
        setupClickListeners();
        bindSocialState();
        setupDrawerMenu();
        bindActivePosts();
        loadRelatedPosts();
    }

    private void setupCreatePostLauncher() {
        createPostLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String content = data.getStringExtra("post_content");
                        String tag = data.getStringExtra("post_tag");
                        String location = data.getStringExtra("post_location");
                        int maxMembers = data.getIntExtra("post_max_members", 10);
                        String imageUri = data.getStringExtra("post_image_uri");
                        long endTimeMillis = data.getLongExtra("post_end_time",
                                System.currentTimeMillis() + 24L * 60L * 60L * 1000L);
                        createPostViaApi(content, tag, location, maxMembers, imageUri, endTimeMillis);
                    }
                }
        );
    }

    private void createPostViaApi(String content, String tag, String location,
                                  int maxMembers, String imageUri, long endTimeMillis) {
        Map<String, Object> body = new HashMap<>();
        body.put("content", content);
        body.put("interestTag", tag);
        body.put("location", location);
        body.put("maxMembers", maxMembers);
        if (imageUri != null) {
            body.put("imageUrl", imageUri);
        }
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        body.put("startTime", isoFormat.format(new Date()));
        body.put("endTime", isoFormat.format(new Date(endTimeMillis)));

        postApiService.createPost(body).enqueue(new Callback<ApiResponse<PostResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostResponse>> call,
                                   Response<ApiResponse<PostResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(UserProfileActivity.this, "Đã tạo bài đăng!", Toast.LENGTH_SHORT).show();
                    bindActivePosts();
                } else {
                    String errorMsg = "Không thể tạo bài đăng";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Toast.makeText(UserProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PostResponse>> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh state khi quay lại (vd: sau khi chấp nhận kết bạn từ thông báo)
        bindSocialState();
        // Refresh bài viết khi quay lại (vd: sau khi tạo bài mới)
        bindActivePosts();
        loadRelatedPosts();
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
        tvRelatedPostsTitle = findViewById(R.id.tvRelatedPostsTitle);
        tvNoRelatedPosts = findViewById(R.id.tvNoRelatedPosts);
        rvRelatedPosts = findViewById(R.id.rvRelatedPosts);
    }

    private void setupClickListeners() {
        ivBackUserProfile.setOnClickListener(v -> finish());

        btnViewArchive.setOnClickListener(v -> {
            Intent intent = new Intent(this, ArchivePostsActivity.class);
            intent.putExtra("username", tvUserProfileName.getText().toString());
            long archiveUserId = getIntent().getLongExtra("user_id", -1);
            if (archiveUserId <= 0) {
                android.content.SharedPreferences prefs = getSharedPreferences("weconnect_prefs", MODE_PRIVATE);
                archiveUserId = prefs.getLong("user_id", -1);
            }
            intent.putExtra("user_id", archiveUserId);
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
        boolean viewOther = getIntent().getBooleanExtra("view_other", false);
        long targetUserId = getIntent().getLongExtra("user_id", -1);
        
        if (username == null || username.isEmpty()) {
            if (targetUserId > 0) {
                // Try to load username from user ID
                username = "Người dùng #" + targetUserId;
            } else {
                username = socialRepository.getCurrentUsername();
            }
        }

        ivUserProfileAvatar.setImageResource(R.drawable.ic_user_placeholder);
        tvUserProfileName.setText(username);
        tvUserReputation.setText("92");
        
        rvUserReviews.setLayoutManager(new LinearLayoutManager(this));
        // Load reviews from backend
        loadReviewsFromBackend();

        // Load interests from backend
        loadInterestsFromBackend();
    }

    private void loadInterestsFromBackend() {
        com.example.weconnect.api.RetrofitClient.loadToken(this);
        String token = com.example.weconnect.api.RetrofitClient.getAuthToken();

        if (token == null) {
            // Fallback: load from SharedPreferences
            loadInterestsFromLocal();
            return;
        }

        com.example.weconnect.api.UserApiService apiService = 
                com.example.weconnect.api.RetrofitClient.getClient()
                        .create(com.example.weconnect.api.UserApiService.class);

        // Determine if viewing own profile or someone else's
        boolean isOwnProfile = socialRepository.getCurrentUsername().equalsIgnoreCase(username);

        if (isOwnProfile) {
            apiService.getInterests().enqueue(new retrofit2.Callback<com.example.weconnect.models.ApiResponse<java.util.List<String>>>() {
                @Override
                public void onResponse(retrofit2.Call<com.example.weconnect.models.ApiResponse<java.util.List<String>>> call,
                                       retrofit2.Response<com.example.weconnect.models.ApiResponse<java.util.List<String>>> response) {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().getResult() != null) {
                        displayInterests(response.body().getResult());
                    } else {
                        loadInterestsFromLocal();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<com.example.weconnect.models.ApiResponse<java.util.List<String>>> call, Throwable t) {
                    loadInterestsFromLocal();
                }
            });
        } else {
            // For other users, try to load from their profile
            long targetUserId = getIntent().getLongExtra("user_id", -1);
            if (targetUserId > 0) {
                apiService.getUserProfile(targetUserId).enqueue(new retrofit2.Callback<com.example.weconnect.models.ApiResponse<java.util.Map<String, Object>>>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.example.weconnect.models.ApiResponse<java.util.Map<String, Object>>> call,
                                           retrofit2.Response<com.example.weconnect.models.ApiResponse<java.util.Map<String, Object>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getResult() != null) {
                            java.util.Map<String, Object> profile = response.body().getResult();
                            String interestTags = profile.get("interestTags") != null
                                    ? profile.get("interestTags").toString() : "";
                            if (!interestTags.isEmpty()) {
                                displayInterests(java.util.Arrays.asList(interestTags.split(",")));
                            } else {
                                displayInterests(new ArrayList<>());
                            }
                            // Update profile info
                            String fullName = profile.get("fullName") != null
                                    ? profile.get("fullName").toString() : username;
                            tvUserProfileName.setText(fullName);
                        } else {
                            loadInterestsFromLocal();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.example.weconnect.models.ApiResponse<java.util.Map<String, Object>>> call, Throwable t) {
                        loadInterestsFromLocal();
                    }
                });
            } else {
                loadInterestsFromLocal();
            }
        }
    }

    private void loadInterestsFromLocal() {
        android.content.SharedPreferences prefs =
                getSharedPreferences("weconnect_prefs", MODE_PRIVATE);
        String saved = prefs.getString("user_interests", "");
        if (!saved.isEmpty()) {
            displayInterests(java.util.Arrays.asList(saved.split(",")));
        } else {
            // Final fallback
            List<String> defaultTags = new ArrayList<>();
            defaultTags.add("☕ Cà phê");
            defaultTags.add("💬 Giao lưu");
            displayInterests(defaultTags);
        }
    }

    private void displayInterests(List<String> interestTags) {
        chipGroupUserInterests.removeAllViews();
        for (String tag : interestTags) {
            String trimmed = tag.trim();
            if (trimmed.isEmpty()) continue;
            Chip chip = new Chip(this);
            chip.setText(trimmed);
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
    }

    private void bindActivePosts() {
        long targetUserId = getIntent().getLongExtra("user_id", -1);
        if (targetUserId <= 0) {
            // Own profile - load from shared prefs
            android.content.SharedPreferences prefs = getSharedPreferences("weconnect_prefs", MODE_PRIVATE);
            targetUserId = prefs.getLong("user_id", -1);
        }

        if (targetUserId <= 0) {
            // Fallback to fake data
            showActivePosts(FakePostRepository.getInstance().getActivePostsForUser(username));
            return;
        }

        final long userId = targetUserId;
        // Backend getUserActivePosts đã lọc sẵn: archived=false AND endTime > now
        // Không cần lọc lại ở frontend
        postApiService.getUserPosts(userId).enqueue(new Callback<ApiResponse<List<PostResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PostResponse>>> call,
                                   Response<ApiResponse<List<PostResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<PostResponse> allResponses = response.body().getResult();
                    List<Post> userPosts = new ArrayList<>();
                    if (allResponses != null) {
                        for (PostResponse pr : allResponses) {
                            userPosts.add(pr.toPost());
                        }
                    }
                    showActivePosts(userPosts);
                } else {
                    showActivePosts(FakePostRepository.getInstance().getActivePostsForUser(username));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<PostResponse>>> call, Throwable t) {
                showActivePosts(FakePostRepository.getInstance().getActivePostsForUser(username));
            }
        });
    }

    private void showActivePosts(List<Post> activePosts) {
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

    /**
     * Load bài viết gợi ý từ người dùng khác dựa trên sở thích chung.
     * Gọi API getActivePosts rồi lọc: cùng tag sở thích, khác tác giả.
     */
    private void loadRelatedPosts() {
        // Lấy sở thích của user hiện tại
        android.content.SharedPreferences prefs = getSharedPreferences("weconnect_prefs", MODE_PRIVATE);
        String savedInterests = prefs.getString("user_interests", "");

        if (savedInterests.isEmpty()) {
            // Thử load từ API trước
            RetrofitClient.loadToken(this);
            com.example.weconnect.api.UserApiService userApi =
                    RetrofitClient.getClient().create(com.example.weconnect.api.UserApiService.class);
            userApi.getInterests().enqueue(new retrofit2.Callback<ApiResponse<java.util.List<String>>>() {
                @Override
                public void onResponse(retrofit2.Call<ApiResponse<java.util.List<String>>> call,
                                       retrofit2.Response<ApiResponse<java.util.List<String>>> response) {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().getResult() != null
                            && !response.body().getResult().isEmpty()) {
                        java.util.List<String> interests = response.body().getResult();
                        prefs.edit().putString("user_interests", String.join(",", interests)).apply();
                        fetchAndFilterRelatedPosts(interests);
                    } else {
                        hideRelatedPosts();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ApiResponse<java.util.List<String>>> call, Throwable t) {
                    hideRelatedPosts();
                }
            });
            return;
        }

        java.util.List<String> interests = java.util.Arrays.asList(savedInterests.split(","));
        fetchAndFilterRelatedPosts(interests);
    }

    private void fetchAndFilterRelatedPosts(java.util.List<String> userInterests) {
        // Tạo set sở thích để so sánh nhanh
        java.util.Set<String> interestSet = new java.util.HashSet<>();
        for (String tag : userInterests) {
            String trimmed = tag.trim();
            if (!trimmed.isEmpty()) interestSet.add(trimmed.toLowerCase());
        }

        if (interestSet.isEmpty()) {
            hideRelatedPosts();
            return;
        }

        // Dùng username của profile đang xem để loại bỏ bài của chính họ
        postApiService.getActivePosts().enqueue(new Callback<ApiResponse<java.util.List<PostResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<java.util.List<PostResponse>>> call,
                                   Response<ApiResponse<java.util.List<PostResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    java.util.List<PostResponse> allPosts = response.body().getResult();
                    java.util.List<Post> related = new ArrayList<>();
                    if (allPosts != null) {
                        for (PostResponse pr : allPosts) {
                            // Bỏ qua bài của chính user đang xem
                            if (pr.getAuthorName() != null
                                    && pr.getAuthorName().equalsIgnoreCase(username)) {
                                continue;
                            }
                            // Lọc bài có tag trùng sở thích
                            if (pr.getInterestTag() != null
                                    && interestSet.contains(pr.getInterestTag().trim().toLowerCase())) {
                                related.add(pr.toPost());
                            }
                        }
                    }
                    showRelatedPosts(related);
                } else {
                    hideRelatedPosts();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<java.util.List<PostResponse>>> call, Throwable t) {
                hideRelatedPosts();
            }
        });
    }

    private void showRelatedPosts(java.util.List<Post> relatedPosts) {
        if (relatedPosts.isEmpty()) {
            hideRelatedPosts();
            return;
        }
        tvRelatedPostsTitle.setVisibility(View.VISIBLE);
        tvNoRelatedPosts.setVisibility(View.GONE);
        rvRelatedPosts.setVisibility(View.VISIBLE);
        rvRelatedPosts.setLayoutManager(new LinearLayoutManager(this));
        rvRelatedPosts.setAdapter(new PostAdapter(this, relatedPosts));
    }

    private void hideRelatedPosts() {
        tvRelatedPostsTitle.setVisibility(View.GONE);
        tvNoRelatedPosts.setVisibility(View.GONE);
        rvRelatedPosts.setVisibility(View.GONE);
    }

    private void bindSocialState() {
        boolean viewOther = getIntent().getBooleanExtra("view_other", false);
        FakeSocialRepository.SocialState state = socialRepository.getState(username);

        // If explicitly viewing another user from notifications, force it
        boolean isOwnProfile = state.isSelfProfile() && !viewOther;

        if (isOwnProfile) {
            // Hồ sơ của mình: ẩn close, hiện menu ☰, hiện bạn bè + kho lưu trữ
            ivBackUserProfile.setVisibility(View.GONE);
            ivMenuProfile.setVisibility(View.VISIBLE);
            tvFriendCount.setVisibility(View.VISIBLE);
            tvFriendCount.setText("👥 Bạn bè: " + socialRepository.getFriendCount());
            tvFriendCount.setOnClickListener(v -> showFriendListDialog());
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
                Intent intent = new Intent(this, CreatePostActivity.class);
                createPostLauncher.launch(intent);
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

    private void showFriendListDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog sheet =
                new com.google.android.material.bottomsheet.BottomSheetDialog(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(R.color.card_surface, null));
        root.setPadding(0, 0, 0, 48);

        // Header
        TextView header = new TextView(this);
        header.setText("👥 Danh sách bạn bè");
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

        List<String> friends = socialRepository.getFriendNames();

        if (friends.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Bạn chưa có bạn bè nào");
            tvEmpty.setTextSize(15);
            tvEmpty.setTextColor(getResources().getColor(R.color.text_secondary, null));
            tvEmpty.setGravity(Gravity.CENTER);
            tvEmpty.setPadding(0, 48, 0, 48);
            root.addView(tvEmpty);
        } else {
            for (String friendName : friends) {
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding(64, 32, 64, 32);
                row.setBackgroundResource(android.R.drawable.list_selector_background);
                row.setClickable(true);
                row.setFocusable(true);

                TextView tvIcon = new TextView(this);
                tvIcon.setText("👤");
                tvIcon.setTextSize(22);
                row.addView(tvIcon);

                TextView tvName = new TextView(this);
                tvName.setText(friendName);
                tvName.setTextSize(16);
                tvName.setTextColor(getResources().getColor(R.color.text_primary, null));
                tvName.setTypeface(null, android.graphics.Typeface.BOLD);
                tvName.setPadding(32, 0, 0, 0);
                row.addView(tvName);

                row.setOnClickListener(v -> {
                    sheet.dismiss();
                    Intent intent = new Intent(this, UserProfileActivity.class);
                    intent.putExtra("username", friendName);
                    startActivity(intent);
                });

                root.addView(row);

                // Separator
                View sep = new View(this);
                sep.setBackgroundColor(0xFFE8E4DE);
                sep.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1));
                root.addView(sep);
            }
        }

        sheet.setContentView(root);
        sheet.show();
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
            submitReviewToBackend((int) ratingBar.getRating(), etComment.getText().toString().trim());
        });
        root.addView(btnSubmit);

        sheet.setContentView(root);
        sheet.show();
    }

    private void submitReviewToBackend(int stars, String comment) {
        long reviewedUserId = getIntent().getLongExtra("user_id", -1);
        if (reviewedUserId <= 0) {
            Toast.makeText(this, "Không thể gửi đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        // Map stars to reputation label
        String[] labels = {"Cần cải thiện", "Trung bình", "Tích cực", "Đáng tin cậy", "Xuất sắc"};
        String reputationLabel = labels[Math.min(stars - 1, labels.length - 1)];

        Map<String, Object> body = new HashMap<>();
        body.put("reviewedUserId", reviewedUserId);
        body.put("activityName", "Hoạt động chung");
        body.put("reputationLabel", reputationLabel);
        body.put("comment", comment.isEmpty() ? "Đánh giá " + stars + " sao" : comment);

        reviewApiService.createReview(body).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserProfileActivity.this,
                            "Đã gửi đánh giá " + stars + " sao cho " + username,
                            Toast.LENGTH_SHORT).show();
                    // Refresh reviews
                    loadReviewsFromBackend();
                } else {
                    String errorMsg = "Không thể gửi đánh giá";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Toast.makeText(UserProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReviewsFromBackend() {
        long targetUserId = getIntent().getLongExtra("user_id", -1);
        if (targetUserId <= 0) {
            android.content.SharedPreferences prefs = getSharedPreferences("weconnect_prefs", MODE_PRIVATE);
            targetUserId = prefs.getLong("user_id", -1);
        }
        if (targetUserId <= 0) {
            // Fallback: show empty
            rvUserReviews.setAdapter(new UserReviewAdapter(new ArrayList<>()));
            return;
        }

        reviewApiService.getReviews(targetUserId).enqueue(new Callback<ApiResponse<List<Map<String, Object>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Map<String, Object>>>> call,
                                   Response<ApiResponse<List<Map<String, Object>>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getResult() != null) {
                    List<Map<String, Object>> reviewMaps = response.body().getResult();
                    List<UserReview> reviews = new ArrayList<>();
                    for (Map<String, Object> map : reviewMaps) {
                        String reviewerName = map.get("reviewerName") != null ? map.get("reviewerName").toString() : "Ẩn danh";
                        String activityName = map.get("activityName") != null ? map.get("activityName").toString() : "";
                        String reputationLabel = map.get("reputationLabel") != null ? map.get("reputationLabel").toString() : "";
                        String reviewComment = map.get("comment") != null ? map.get("comment").toString() : "";
                        reviews.add(new UserReview(reviewerName, activityName, reputationLabel, reviewComment));
                    }
                    rvUserReviews.setAdapter(new UserReviewAdapter(reviews));
                } else {
                    rvUserReviews.setAdapter(new UserReviewAdapter(new ArrayList<>()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Map<String, Object>>>> call, Throwable t) {
                rvUserReviews.setAdapter(new UserReviewAdapter(new ArrayList<>()));
            }
        });
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
