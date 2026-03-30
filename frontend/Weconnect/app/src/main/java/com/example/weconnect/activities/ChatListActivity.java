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

        // Header: "Tạo tin nhắn mới"
        TextView header = new TextView(this);
        header.setText("Tạo tin nhắn mới");
        header.setTextSize(20);
        header.setTextColor(getResources().getColor(R.color.primary_pink, null));
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setGravity(android.view.Gravity.CENTER);
        header.setPadding(0, 48, 0, 24);
        root.addView(header);

        // Search bar
        EditText search = new EditText(this);
        search.setHint("🔍 Tìm bạn bè...");
        search.setTextSize(15);
        search.setTextColor(getResources().getColor(R.color.text_primary, null));
        search.setHintTextColor(getResources().getColor(R.color.text_secondary, null));
        search.setBackground(null);
        search.setPadding(64, 32, 64, 32);
        search.setSingleLine(true);
        search.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        search.setFocusable(true);
        search.setFocusableInTouchMode(true);
        root.addView(search);

        // Divider
        View div1 = new View(this);
        div1.setBackgroundColor(0xFFE8E4DE);
        div1.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2));
        root.addView(div1);

        // Selected friends label
        TextView tvSelected = new TextView(this);
        tvSelected.setText("Chọn bạn bè để tạo nhóm chat");
        tvSelected.setTextSize(14);
        tvSelected.setTextColor(getResources().getColor(R.color.text_secondary, null));
        tvSelected.setPadding(64, 28, 64, 12);
        root.addView(tvSelected);

        // Friend list container
        LinearLayout friendListContainer = new LinearLayout(this);
        friendListContainer.setOrientation(LinearLayout.VERTICAL);

        // Get friends
        com.example.weconnect.data.FakeSocialRepository socialRepo =
                com.example.weconnect.data.FakeSocialRepository.getInstance();
        java.util.List<String> allFriends = socialRepo.getFriendNames();
        java.util.Set<String> selectedFriends = new java.util.LinkedHashSet<>();

        // Build friend rows
        Runnable buildFriendRows = () -> {};
        final Runnable[] buildRef = new Runnable[1];
        buildRef[0] = () -> {
            friendListContainer.removeAllViews();
            String query = search.getText().toString().trim().toLowerCase();

            for (String friendName : allFriends) {
                if (!query.isEmpty() && !friendName.toLowerCase().contains(query)) {
                    continue;
                }

                LinearLayout friendRow = new LinearLayout(this);
                friendRow.setOrientation(LinearLayout.HORIZONTAL);
                friendRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
                friendRow.setPadding(64, 24, 64, 24);
                friendRow.setBackgroundResource(android.R.drawable.list_selector_background);
                friendRow.setClickable(true);

                // Checkbox
                android.widget.CheckBox checkBox = new android.widget.CheckBox(this);
                checkBox.setChecked(selectedFriends.contains(friendName));
                checkBox.setButtonTintList(android.content.res.ColorStateList.valueOf(
                        getResources().getColor(R.color.primary_pink, null)));

                // Avatar
                ImageView avatar = new ImageView(this);
                avatar.setImageResource(R.drawable.ic_user_placeholder);
                LinearLayout.LayoutParams avatarLp = new LinearLayout.LayoutParams(88, 88);
                avatarLp.setMargins(24, 0, 0, 0);
                avatar.setLayoutParams(avatarLp);
                avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);

                // Name
                TextView name = new TextView(this);
                name.setText(friendName);
                name.setTextSize(15);
                name.setTextColor(getResources().getColor(R.color.text_primary, null));
                name.setPadding(24, 0, 0, 0);

                friendRow.addView(checkBox);
                friendRow.addView(avatar);
                friendRow.addView(name);

                View.OnClickListener toggleFriend = v -> {
                    if (selectedFriends.contains(friendName)) {
                        selectedFriends.remove(friendName);
                        checkBox.setChecked(false);
                    } else {
                        selectedFriends.add(friendName);
                        checkBox.setChecked(true);
                    }
                    // Update selected count label
                    if (selectedFriends.isEmpty()) {
                        tvSelected.setText("Chọn bạn bè để tạo nhóm chat");
                    } else {
                        tvSelected.setText("Đã chọn: " + selectedFriends.size() + " người");
                    }
                };

                friendRow.setOnClickListener(toggleFriend);
                checkBox.setOnClickListener(toggleFriend);

                friendListContainer.addView(friendRow);
            }

            if (friendListContainer.getChildCount() == 0) {
                TextView noResult = new TextView(this);
                noResult.setText("Không tìm thấy bạn bè");
                noResult.setTextSize(14);
                noResult.setTextColor(getResources().getColor(R.color.text_secondary, null));
                noResult.setGravity(android.view.Gravity.CENTER);
                noResult.setPadding(0, 48, 0, 48);
                friendListContainer.addView(noResult);
            }
        };

        buildRef[0].run();
        root.addView(friendListContainer);

        // Search filter
        search.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buildRef[0].run();
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Divider
        View div2 = new View(this);
        div2.setBackgroundColor(0xFFE8E4DE);
        div2.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2));
        root.addView(div2);

        // Create group button
        com.google.android.material.button.MaterialButton btnCreate =
                new com.google.android.material.button.MaterialButton(this);
        btnCreate.setText("Tạo nhóm chat");
        btnCreate.setAllCaps(false);
        btnCreate.setCornerRadius(72);
        btnCreate.setTextSize(16);
        btnCreate.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                getResources().getColor(R.color.primary_pink, null)));
        LinearLayout.LayoutParams btnP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnP.setMargins(48, 24, 48, 0);
        btnCreate.setLayoutParams(btnP);
        btnCreate.setOnClickListener(v -> {
            if (selectedFriends.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 người bạn", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedFriends.size() == 1) {
                // Direct message
                String friendName = selectedFriends.iterator().next();
                sheet.dismiss();
                com.example.weconnect.models.ChatRoom directRoom =
                        FakeChatRepository.getInstance().getOrCreateDirectRoom(friendName);
                Intent intent = new Intent(this, ConversationActivity.class);
                intent.putExtra("room_id", directRoom.getId());
                startActivity(intent);
            } else {
                // Group chat
                java.util.List<String> memberList = new java.util.ArrayList<>(selectedFriends);
                String groupTitle = String.join(", ", memberList);
                sheet.dismiss();
                com.example.weconnect.models.ChatRoom groupRoom =
                        FakeChatRepository.getInstance().createGroupChat(groupTitle, memberList);
                Intent intent = new Intent(this, ConversationActivity.class);
                intent.putExtra("room_id", groupRoom.getId());
                startActivity(intent);
                loadChats();
            }
        });
        root.addView(btnCreate);

        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        scrollView.addView(root);
        sheet.setContentView(scrollView);
        sheet.show();

        // Auto-show keyboard on search field
        search.requestFocus();
        search.postDelayed(() -> {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(search, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            }
        }, 300);
    }
}
