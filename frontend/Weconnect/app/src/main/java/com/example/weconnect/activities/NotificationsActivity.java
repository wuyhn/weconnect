package com.example.weconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.adapters.NotificationAdapter;
import com.example.weconnect.data.FakeNotificationRepository;
import com.example.weconnect.data.FakePostRepository;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<Object> groupedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        rvNotifications = findViewById(R.id.rvNotifications);
        TextView tvEmpty = findViewById(R.id.tvNoNotifications);
        ImageView ivMarkAllRead = findViewById(R.id.ivMarkAllRead);

        List<FakeNotificationRepository.NotificationItem> notifications =
                FakeNotificationRepository.getInstance().getNotifications();

        if (notifications.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);

            groupedItems = NotificationAdapter.groupByDate(notifications);
            adapter = new NotificationAdapter(this, groupedItems);
            rvNotifications.setLayoutManager(new LinearLayoutManager(this));
            rvNotifications.setAdapter(adapter);
        }

        // Mark all as read
        ivMarkAllRead.setOnClickListener(v -> {
            for (FakeNotificationRepository.NotificationItem item : notifications) {
                item.setRead(true);
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            Toast.makeText(this, "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show();
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        FrameLayout btnHome = findViewById(R.id.btnHomeNotif);
        FrameLayout btnMessages = findViewById(R.id.btnMessagesNotif);
        FrameLayout btnProfile = findViewById(R.id.btnProfileNotif);

        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }
        if (btnMessages != null) {
            btnMessages.setOnClickListener(v -> {
                startActivity(new Intent(this, ChatListActivity.class));
            });
        }
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("username", FakePostRepository.getInstance().getCurrentUsername());
                startActivity(intent);
            });
        }
    }
}
