package com.example.weconnect.adapters;

import android.content.Context;
import android.content.Intent;

import com.example.weconnect.activities.UserProfileActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.models.SearchResultItem;
import com.example.weconnect.activities.PostDetailActivity;
import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final Context context;
        private final List<SearchResultItem> items = new ArrayList<>();

        public SearchResultAdapter(Context context) {
            this.context = context;
        }

        public void submitList(List<SearchResultItem> newItems) {
            items.clear();
            items.addAll(newItems);
            notifyDataSetChanged();
        }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == SearchResultItem.TYPE_SECTION) {
            View view = layoutInflater.inflate(R.layout.item_search_section, parent, false);
            return new SectionViewHolder(view);
        } else if (viewType == SearchResultItem.TYPE_USER) {
            View view = layoutInflater.inflate(R.layout.item_search_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = layoutInflater.inflate(R.layout.item_search_post, parent, false);
            return new PostViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchResultItem item = items.get(position);

        if (holder instanceof SectionViewHolder) {
            ((SectionViewHolder) holder).tvSectionTitle.setText(item.getTitle());
        } else if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).tvUserName.setText(item.getTitle());
            ((UserViewHolder) holder).ivUserAvatar.setImageResource(item.getAvatarResId());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("username", item.getTitle());
                context.startActivity(intent);
            });
        } else if (holder instanceof PostViewHolder) {
            ((PostViewHolder) holder).tvPostTitle.setText(item.getTitle());

            if (item.getSubtitle() != null && item.getSubtitle().length() > 0) {
                ((PostViewHolder) holder).tvPostSubtitle.setVisibility(View.VISIBLE);
                ((PostViewHolder) holder).tvPostSubtitle.setText(item.getSubtitle());
            } else {
                ((PostViewHolder) holder).tvPostSubtitle.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("post_username", item.getUsername());
                intent.putExtra("post_content", item.getContent());
                intent.putExtra("post_tag", item.getTag());
                intent.putExtra("post_location", item.getLocation());
                intent.putExtra("post_member_count", item.getMemberCount());
                intent.putExtra("post_max_members", item.getMaxMembers());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView tvSectionTitle;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSectionTitle = itemView.findViewById(R.id.tvSectionTitle);
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserAvatar;
        TextView tvUserName;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivSearchUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvSearchUserName);
        }
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvPostTitle;
        TextView tvPostSubtitle;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPostTitle = itemView.findViewById(R.id.tvSearchPostTitle);
            tvPostSubtitle = itemView.findViewById(R.id.tvSearchPostSubtitle);
        }
    }
}