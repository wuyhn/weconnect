package com.example.weconnect.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.adapters.MessageAdapter;
import com.example.weconnect.data.FakeChatRepository;
import com.example.weconnect.data.FakePostRepository;
import com.example.weconnect.models.ChatRoom;
import com.google.android.material.button.MaterialButton;

public class ConversationActivity extends AppCompatActivity {

    private ImageView ivBackConversation;
    private ImageView ivConversationAvatar;
    private TextView tvConversationTitle;
    private TextView tvConversationType;
    private TextView tvConversationStatus;
    private RecyclerView rvMessages;
    private EditText etMessageInput;
    private MaterialButton btnSendMessage;
    private MessageAdapter adapter;
    private ChatRoom room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        initViews();
        setupRecyclerView();
        setupClickListeners();
        bindRoom();
    }

    private void initViews() {
        ivBackConversation = findViewById(R.id.ivBackConversation);
        ivConversationAvatar = findViewById(R.id.ivConversationAvatar);
        tvConversationTitle = findViewById(R.id.tvConversationTitle);
        tvConversationType = findViewById(R.id.tvConversationType);
        tvConversationStatus = findViewById(R.id.tvConversationStatus);
        rvMessages = findViewById(R.id.rvMessages);
        etMessageInput = findViewById(R.id.etMessageInput);
        btnSendMessage = findViewById(R.id.btnSendMessage);
    }

    private void setupRecyclerView() {
        adapter = new MessageAdapter();
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);
    }

    private void setupClickListeners() {
        ivBackConversation.setOnClickListener(v -> finish());
        btnSendMessage.setOnClickListener(v -> sendMessage());
    }

    private void bindRoom() {
        String roomId = getIntent().getStringExtra("room_id");
        room = FakeChatRepository.getInstance().getRoomById(roomId);
        if (room == null) {
            Toast.makeText(this, "Chat room not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ivConversationAvatar.setImageResource(room.getAvatarResId());
        tvConversationTitle.setText(room.getTitle());
        tvConversationType.setText(room.getTypeLabel());

        if (ChatRoom.TYPE_GROUP.equals(room.getType()) && !room.isActive()) {
            tvConversationStatus.setText(room.getInactiveStatusLabel());
            tvConversationStatus.setBackgroundResource(R.drawable.bg_chat_type_badge_inactive);
            tvConversationStatus.setTextColor(0xFF7A7A7A);
        } else {
            tvConversationStatus.setText("Dang hoat dong");
            tvConversationStatus.setBackgroundResource(R.drawable.bg_chat_type_badge);
            tvConversationStatus.setTextColor(0xFFFF4D6D);
        }

        refreshMessages();
    }

    private void sendMessage() {
        if (room == null) {
            return;
        }

        String content = etMessageInput.getText() != null
                ? etMessageInput.getText().toString().trim()
                : "";

        if (TextUtils.isEmpty(content)) {
            return;
        }

        FakeChatRepository.getInstance().sendMessage(
                room.getId(),
                FakePostRepository.getInstance().getCurrentUsername(),
                content
        );
        etMessageInput.setText("");
        room = FakeChatRepository.getInstance().getRoomById(room.getId());
        refreshMessages();
    }

    private void refreshMessages() {
        adapter.submitList(room.getMessages());
        if (!room.getMessages().isEmpty()) {
            rvMessages.scrollToPosition(room.getMessages().size() - 1);
        }
    }
}
