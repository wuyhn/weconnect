package com.example.weconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
        tabChatType.addTab(tabChatType.newTab().setText("Hoat dong"));
        tabChatType.addTab(tabChatType.newTab().setText("Lien he"));
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
        ivNewChat.setOnClickListener(v ->
                Toast.makeText(this, "Compose flow can be added after friend logic.", Toast.LENGTH_SHORT).show()
        );

        btnHome.setOnClickListener(v -> finish());

        btnNotifications.setOnClickListener(v ->
                Toast.makeText(this, "Notifications screen is next.", Toast.LENGTH_SHORT).show()
        );

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
}
