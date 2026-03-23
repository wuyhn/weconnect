package com.example.weconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.activities.UserProfileActivity;

import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {

    public static class Participant {
        public final String name;
        public final int avatarResId;

        public Participant(String name, int avatarResId) {
            this.name = name;
            this.avatarResId = avatarResId;
        }
    }

    private final Context context;
    private final List<Participant> participants;

    public ParticipantAdapter(Context context, List<Participant> participants) {
        this.context = context;
        this.participants = participants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_participant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Participant p = participants.get(position);
        holder.tvName.setText(p.name);
        holder.ivAvatar.setImageResource(p.avatarResId);

        View.OnClickListener openProfile = v -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("username", p.name);
            context.startActivity(intent);
        };

        holder.itemView.setOnClickListener(openProfile);
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivParticipantAvatar);
            tvName = itemView.findViewById(R.id.tvParticipantName);
        }
    }
}
