package com.example.weconnect.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.adapters.NotificationAdapter;
import com.example.weconnect.data.FakeNotificationRepository;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ImageView ivClose = findViewById(R.id.ivCloseNotifications);
        RecyclerView rvNotifications = findViewById(R.id.rvNotifications);
        TextView tvEmpty = findViewById(R.id.tvNoNotifications);

        ivClose.setOnClickListener(v -> finish());

        List<FakeNotificationRepository.NotificationItem> notifications =
                FakeNotificationRepository.getInstance().getNotifications();

        if (notifications.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);

            List<Object> grouped = NotificationAdapter.groupByDate(notifications);
            rvNotifications.setLayoutManager(new LinearLayoutManager(this));
            rvNotifications.setAdapter(new NotificationAdapter(this, grouped));
        }
    }
}
