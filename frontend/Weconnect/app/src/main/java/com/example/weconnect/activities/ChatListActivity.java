package com.example.weconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.adapters.ChatRoomAdapter;
import com.example.weconnect.data.FakeChatRepository;
import com.example.weconnect.data.FakePostRepository;
import com.example.weconnect.models.ChatRoom;
import com.google.android.material.tabs.TabLayout;

public class ChatListActivity extends AppCompatActivity {

    private static final String TAB_GROUP = ChatRoom.TYPE_GROUP;
    private static final String TAB_DIRECT = ChatRoom.TYPE_DIRECT;

    private ImageView ivNewChat;
    private EditText etChatSearch;
    private RecyclerView rvChats;
    private FrameLayout btnHome;
    private FrameLayout btnMessages;
    private FrameLayout btnNotifications;
    private FrameLayout btnProfile;
    private TabLayout tabChatType;
    private ChatRoomAdapter adapter;
    private String currentTab = TAB_GROUP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        setupRecyclerView();
        setupTabs();
        setupClickListeners();
        setupSearch();
        applyIncomingContext();
        loadChats();
    }

    private void initViews() {
        ivNewChat = findViewById(R.id.ivNewChat);
        etChatSearch = findViewById(R.id.etChatSearch);
        rvChats = findViewById(R.id.rvChats);
        btnHome = findViewById(R.id.btnHome);
        btnMessages = findViewById(R.id.btnMessages);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnProfile = findViewById(R.id.btnProfile);
        tabChatType = findViewById(R.id.tabChatType);
    }

    private void setupRecyclerView() {
        adapter = new ChatRoomAdapter(this::openRoom);
        rvChats.setLayoutManager(new LinearLayoutManager(this));
        rvChats.setAdapter(adapter);
        btnMessages.setAlpha(1.0f);
    }

    private void setupTabs() {
        tabChatType.addTab(tabChatType.newTab().setText("Hoạt động"));
        tabChatType.addTab(tabChatType.newTab().setText("Liên hệ"));
        tabChatType.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab != null && tab.getPosition() == 1 ? TAB_DIRECT : TAB_GROUP;
                loadChats();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupClickListeners() {
        ivNewChat.setOnClickListener(v -> showNewChatDialog());

        btnHome.setOnClickListener(v -> finish());

        btnNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra("username", FakePostRepository.getInstance().getCurrentUsername());
            startActivity(intent);
        });
    }

    private void setupSearch() {
        etChatSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadChats();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void applyIncomingContext() {
        String highlightTag = getIntent().getStringExtra("highlight_tag");
        if (highlightTag != null && !highlightTag.trim().isEmpty()) {
            currentTab = TAB_GROUP;
            TabLayout.Tab groupTab = tabChatType.getTabAt(0);
            if (groupTab != null) {
                groupTab.select();
            }
            etChatSearch.setText(highlightTag);
            etChatSearch.setSelection(highlightTag.length());
        }
    }

    private void loadChats() {
        String query = etChatSearch.getText() != null ? etChatSearch.getText().toString() : "";
        adapter.submitList(FakeChatRepository.getInstance().searchChatRoomsByType(currentTab, query));
    }

    private void openRoom(ChatRoom room) {
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("room_id", room.getId());
        startActivity(intent);
    }

    private void showNewChatDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog sheet =
                new com.google.android.material.bottomsheet.BottomSheetDialog(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(R.color.card_surface, null));
        root.setPadding(0, 0, 0, 48);

        // Header: "Tin nhắn mới"
        TextView header = new TextView(this);
        header.setText("Tin nhắn mới");
        header.setTextSize(20);
        header.setTextColor(getResources().getColor(R.color.text_primary, null));
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setGravity(android.view.Gravity.CENTER);
        header.setPadding(0, 48, 0, 24);
        root.addView(header);

        // Search bar: "Đến: Tìm kiếm"
        EditText search = new EditText(this);
        search.setHint("Đến: Tìm kiếm");
        search.setTextSize(15);
        search.setTextColor(getResources().getColor(R.color.text_primary, null));
        search.setHintTextColor(getResources().getColor(R.color.text_secondary, null));
        search.setBackground(null);
        search.setPadding(64, 32, 64, 32);
        search.setSingleLine(true);
        root.addView(search);

        // Divider
        View div1 = new View(this);
        div1.setBackgroundColor(0xFFE8E4DE);
        div1.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2));
        root.addView(div1);

        // "Nhóm chat" row
        LinearLayout groupRow = new LinearLayout(this);
        groupRow.setOrientation(LinearLayout.HORIZONTAL);
        groupRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        groupRow.setPadding(64, 36, 64, 36);
        groupRow.setBackgroundResource(android.R.drawable.list_selector_background);
        groupRow.setClickable(true);

        // Group icon circle
        TextView groupIcon = new TextView(this);
        groupIcon.setText("👥");
        groupIcon.setTextSize(22);
        groupRow.addView(groupIcon);

        TextView groupLabel = new TextView(this);
        groupLabel.setText("Nhóm chat");
        groupLabel.setTextSize(16);
        groupLabel.setTextColor(getResources().getColor(R.color.text_primary, null));
        groupLabel.setPadding(32, 0, 0, 0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        groupLabel.setLayoutParams(lp);
        groupRow.addView(groupLabel);

        TextView arrow = new TextView(this);
        arrow.setText("›");
        arrow.setTextSize(22);
        arrow.setTextColor(getResources().getColor(R.color.text_secondary, null));
        groupRow.addView(arrow);

        groupRow.setOnClickListener(v -> {
            sheet.dismiss();
            Toast.makeText(this, "Tạo nhóm chat", Toast.LENGTH_SHORT).show();
        });
        root.addView(groupRow);

        // Divider
        View div2 = new View(this);
        div2.setBackgroundColor(0xFFE8E4DE);
        div2.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2));
        root.addView(div2);

        // "Gợi ý" label
        TextView suggestLabel = new TextView(this);
        suggestLabel.setText("Gợi ý");
        suggestLabel.setTextSize(15);
        suggestLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        suggestLabel.setTextColor(getResources().getColor(R.color.text_primary, null));
        suggestLabel.setPadding(64, 36, 64, 16);
        root.addView(suggestLabel);

        // Friend list from social repository
        com.example.weconnect.data.FakeSocialRepository socialRepo =
                com.example.weconnect.data.FakeSocialRepository.getInstance();
        java.util.List<String> friends = socialRepo.getFriendNames();

        for (String friendName : friends) {
            LinearLayout friendRow = new LinearLayout(this);
            friendRow.setOrientation(LinearLayout.HORIZONTAL);
            friendRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
            friendRow.setPadding(64, 28, 64, 28);
            friendRow.setBackgroundResource(android.R.drawable.list_selector_background);
            friendRow.setClickable(true);

            // Avatar placeholder
            ImageView avatar = new ImageView(this);
            avatar.setImageResource(R.drawable.ic_user_placeholder);
            LinearLayout.LayoutParams avatarLp = new LinearLayout.LayoutParams(96, 96);
            avatar.setLayoutParams(avatarLp);
            avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            friendRow.addView(avatar);

            // Name
            TextView name = new TextView(this);
            name.setText(friendName);
            name.setTextSize(15);
            name.setTextColor(getResources().getColor(R.color.text_primary, null));
            name.setPadding(32, 0, 0, 0);
            friendRow.addView(name);

            friendRow.setOnClickListener(v -> {
                sheet.dismiss();
                // Open direct chat with this friend
                ChatRoom directRoom = FakeChatRepository.getInstance().findDirectRoom(friendName);
                if (directRoom != null) {
                    Intent intent = new Intent(this, ConversationActivity.class);
                    intent.putExtra("room_id", directRoom.getId());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Bắt đầu trò chuyện với " + friendName, Toast.LENGTH_SHORT).show();
                }
            });

            root.addView(friendRow);
        }

        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        scrollView.addView(root);
        sheet.setContentView(scrollView);
        sheet.show();
    }
}
