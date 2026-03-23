package com.example.weconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.activities.UserProfileActivity;
import com.example.weconnect.data.FakeNotificationRepository;
import com.example.weconnect.data.FakeNotificationRepository.NotificationItem;
import com.example.weconnect.data.FakeNotificationRepository.NotificationType;
import com.example.weconnect.data.FakeSocialRepository;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final Context context;
    private final List<Object> items; // String (date header) or NotificationItem

    public NotificationAdapter(Context context, List<Object> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            TextView tv = new TextView(context);
            tv.setTextSize(15);
            tv.setTextColor(context.getResources().getColor(R.color.primary_pink, null));
            tv.setPadding(56, 32, 20, 8);
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
            return new HeaderViewHolder(tv);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).tvHeader.setText((String) items.get(position));
        } else if (holder instanceof NotifViewHolder) {
            bindNotification((NotifViewHolder) holder, (NotificationItem) items.get(position));
        }
    }

    private void bindNotification(NotifViewHolder holder, NotificationItem item) {
        holder.tvMessage.setText(item.getMessage());
        holder.tvTime.setText(formatTime(item.getTimestamp()));

        // Show action buttons for actionable items
        boolean isActionable = (item.getType() == NotificationType.FRIEND_REQUEST_RECEIVED
                || item.getType() == NotificationType.JOIN_REQUEST);

        if (isActionable && !item.isActioned()) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.tvActioned.setVisibility(View.GONE);

            holder.btnAccept.setOnClickListener(v -> {
                if (item.getType() == NotificationType.FRIEND_REQUEST_RECEIVED) {
                    FakeSocialRepository.getInstance().acceptFriendRequest(item.getRelatedUsername());
                    Toast.makeText(context, "Đã chấp nhận lời mời kết bạn", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Đã duyệt yêu cầu tham gia", Toast.LENGTH_SHORT).show();
                }
                item.setActioned(true);
                notifyItemChanged(holder.getAdapterPosition());
            });

            holder.btnDecline.setOnClickListener(v -> {
                if (item.getType() == NotificationType.FRIEND_REQUEST_RECEIVED) {
                    FakeSocialRepository.getInstance().declineFriendRequest(item.getRelatedUsername());
                    Toast.makeText(context, "Đã từ chối lời mời kết bạn", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Đã từ chối yêu cầu tham gia", Toast.LENGTH_SHORT).show();
                }
                item.setActioned(true);
                notifyItemChanged(holder.getAdapterPosition());
            });
        } else if (isActionable && item.isActioned()) {
            holder.layoutActions.setVisibility(View.GONE);
            holder.tvActioned.setVisibility(View.VISIBLE);
            holder.tvActioned.setText("Đã xử lý");
        } else {
            holder.layoutActions.setVisibility(View.GONE);
            holder.tvActioned.setVisibility(View.GONE);
        }

        // Click to open profile
        if (item.getRelatedUsername() != null && !item.getRelatedUsername().isEmpty()) {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("username", item.getRelatedUsername());
                context.startActivity(intent);
            });
        }

        item.setRead(true);
    }

    private String formatTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long minutes = diff / (60 * 1000);
        long hours = diff / (60 * 60 * 1000);

        if (minutes < 1) return "Vừa xong";
        if (minutes < 60) return minutes + " phút trước";
        if (hours < 24) return hours + " giờ trước";

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Group notifications by date
    public static List<Object> groupByDate(List<NotificationItem> notifications) {
        List<Object> grouped = new java.util.ArrayList<>();
        String lastDateLabel = "";

        for (NotificationItem item : notifications) {
            String dateLabel = getDateLabel(item.getTimestamp());
            if (!dateLabel.equals(lastDateLabel)) {
                grouped.add(dateLabel);
                lastDateLabel = dateLabel;
            }
            grouped.add(item);
        }
        return grouped;
    }

    private static String getDateLabel(long timestamp) {
        Calendar notifCal = Calendar.getInstance();
        notifCal.setTimeInMillis(timestamp);

        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        if (sameDay(notifCal, today)) return "Hôm nay";
        if (sameDay(notifCal, yesterday)) return "Hôm qua";

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private static boolean sameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        HeaderViewHolder(TextView tv) {
            super(tv);
            tvHeader = tv;
        }
    }

    static class NotifViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvMessage, tvTime, tvActioned;
        LinearLayout layoutActions;
        MaterialButton btnAccept, btnDecline;

        NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivNotifAvatar);
            tvMessage = itemView.findViewById(R.id.tvNotifMessage);
            tvTime = itemView.findViewById(R.id.tvNotifTime);
            tvActioned = itemView.findViewById(R.id.tvNotifActioned);
            layoutActions = itemView.findViewById(R.id.layoutNotifActions);
            btnAccept = itemView.findViewById(R.id.btnNotifAccept);
            btnDecline = itemView.findViewById(R.id.btnNotifDecline);
        }
    }
}
