package com.example.weconnect.presentation.chat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.data.model.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {

    public interface OnChatRoomClickListener {
        void onChatRoomClick(ChatRoom room);
    }

    private final List<ChatRoom> rooms = new ArrayList<>();
    private final OnChatRoomClickListener listener;

    public ChatRoomAdapter(OnChatRoomClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ChatRoom> newRooms) {
        rooms.clear();
        rooms.addAll(newRooms);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        ChatRoom room = rooms.get(position);
        holder.ivUserAvatar.setImageResource(room.getAvatarResId());
        holder.tvUserName.setText(room.getTitle());
        holder.tvLastMessage.setText(room.getLastMessagePreview());
        holder.tvTime.setText(room.getLastMessageTime());
        holder.tvTypeBadge.setText("");

        if ((ChatRoom.TYPE_GROUP.equals(room.getType()) || ChatRoom.TYPE_ACTIVITY.equals(room.getType()))
                && !room.isActive()) {
            holder.tvTypeBadge.setBackgroundResource(R.drawable.bg_chat_type_badge_inactive);
            holder.viewAccentDot.setBackgroundResource(R.drawable.bg_chat_accent_dot_inactive);
            holder.viewOnlineIndicator.setVisibility(View.GONE);
        } else {
            holder.tvTypeBadge.setBackgroundResource(R.drawable.bg_chat_type_badge);
            holder.viewAccentDot.setBackgroundResource(R.drawable.bg_chat_accent_dot);
            holder.viewOnlineIndicator.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> listener.onChatRoomClick(room));
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    static class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserAvatar;
        TextView tvUserName;
        TextView tvTypeBadge;
        TextView tvLastMessage;
        TextView tvTime;
        View viewAccentDot;
        View viewOnlineIndicator;

        ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTypeBadge = itemView.findViewById(R.id.tvChatTypeBadge);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvChatTime);
            viewAccentDot = itemView.findViewById(R.id.viewAccentDot);
            viewOnlineIndicator = itemView.findViewById(R.id.viewOnlineIndicator);
        }
    }
}
