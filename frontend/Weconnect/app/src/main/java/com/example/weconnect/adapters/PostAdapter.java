package com.example.weconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.activities.ParticipantsActivity;
import com.example.weconnect.activities.PostDetailActivity;
import com.example.weconnect.activities.UserProfileActivity;
import com.example.weconnect.models.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final Context context;
    private final List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.tvUsername.setText(post.getUsername());
        holder.tvTime.setText(post.getTimeAgo());
        holder.tvContent.setText(post.getContent());
        holder.itemView.setOnClickListener(v -> openPostDetail(post));

        holder.ivAvatar.setImageResource(post.getAvatarResId());
        holder.ivAvatar.setOnClickListener(v -> openUserProfile(post.getUsername()));
        holder.tvUsername.setOnClickListener(v -> openUserProfile(post.getUsername()));

        if (post.getImageResId() != 0) {
            holder.ivPostImage.setVisibility(View.VISIBLE);
            holder.ivPostImage.setImageResource(post.getImageResId());
        } else {
            holder.ivPostImage.setVisibility(View.GONE);
        }

        if (post.getInterestTag() != null && !post.getInterestTag().isEmpty()) {
            holder.tvTag.setVisibility(View.VISIBLE);
            holder.tvTag.setText(post.getInterestTag());
        } else {
            holder.tvTag.setVisibility(View.GONE);
        }

        if (post.getLocation() != null && !post.getLocation().isEmpty()) {
            holder.tvLocation.setVisibility(View.VISIBLE);
            holder.tvLocation.setText("Location: " + post.getLocation());
        } else {
            holder.tvLocation.setVisibility(View.GONE);
        }

        if (post.isJoined()) {
            holder.btnJoinGroup.setText("Da tham gia");
            holder.btnJoinGroup.setEnabled(false);
            holder.btnJoinGroup.setAlpha(0.6f);
        } else {
            holder.btnJoinGroup.setText("Tham gia nhom");
            holder.btnJoinGroup.setEnabled(true);
            holder.btnJoinGroup.setAlpha(1.0f);
            holder.btnJoinGroup.setOnClickListener(v -> {
                Toast.makeText(context, "Da gui yeu cau tham gia " + post.getUsername(), Toast.LENGTH_SHORT).show();
                post.setJoined(true);
                notifyItemChanged(position);
            });
        }

        holder.btnViewMembers.setText("Members: " + post.getMemberCount() + "/" + post.getMaxMembers());
        holder.btnViewMembers.setTextColor(0xFF000000);
        holder.btnViewMembers.setOnClickListener(v -> {
            Intent intent = new Intent(context, ParticipantsActivity.class);
            intent.putExtra("post_id", post.getId());
            intent.putExtra("member_count", post.getMemberCount());
            intent.putExtra("max_members", post.getMaxMembers());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    private void openPostDetail(Post post) {
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra("post", post);
        context.startActivity(intent);
    }

    private void openUserProfile(String username) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra("username", username);
        context.startActivity(intent);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, ivPostImage;
        TextView tvUsername, tvTime, tvContent;
        TextView btnJoinGroup, btnViewMembers;
        TextView tvTag, tvLocation;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.post_item_avatar);
            tvUsername = itemView.findViewById(R.id.post_item_username);
            tvTime = itemView.findViewById(R.id.post_item_time);
            tvContent = itemView.findViewById(R.id.post_item_content);
            ivPostImage = itemView.findViewById(R.id.post_item_image);
            btnJoinGroup = itemView.findViewById(R.id.btnJoinGroup);
            btnViewMembers = itemView.findViewById(R.id.btnViewMembers);
            tvTag = itemView.findViewById(R.id.post_item_tag);
            tvLocation = itemView.findViewById(R.id.post_item_location);
        }
    }
}
