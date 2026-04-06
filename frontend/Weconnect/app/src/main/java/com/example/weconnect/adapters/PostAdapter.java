package com.example.weconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.activities.ParticipantsActivity;
import com.example.weconnect.activities.PostDetailActivity;
import com.example.weconnect.activities.UserProfileActivity;
import com.example.weconnect.data.FakePostRepository;
import com.example.weconnect.models.Post;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final Context context;
    private final List<Post> postList;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM", Locale.getDefault());
    private final String currentUsername;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        this.currentUsername = FakePostRepository.getInstance().getCurrentUsername();
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

        // Post image: URI from gallery or resource id
        if (post.getPostImageUri() != null && !post.getPostImageUri().isEmpty()) {
            holder.cvPostImage.setVisibility(View.VISIBLE);
            holder.ivPostImage.setImageURI(Uri.parse(post.getPostImageUri()));
        } else if (post.getImageResId() != 0) {
            holder.cvPostImage.setVisibility(View.VISIBLE);
            holder.ivPostImage.setImageResource(post.getImageResId());
        } else {
            holder.cvPostImage.setVisibility(View.GONE);
        }

        if (post.getInterestTag() != null && !post.getInterestTag().isEmpty()) {
            holder.tvTag.setVisibility(View.VISIBLE);
            holder.tvTag.setText(post.getInterestTag());
        } else {
            holder.tvTag.setVisibility(View.GONE);
        }

        if (post.getLocation() != null && !post.getLocation().isEmpty()) {
            holder.tvLocation.setVisibility(View.VISIBLE);
            holder.tvLocation.setText("Địa điểm: " + post.getLocation());
        } else {
            holder.tvLocation.setVisibility(View.GONE);
        }

        // Time Range
        bindTimeRange(holder, post);

        // Post menu (⋯)
        holder.ivPostMenu.setOnClickListener(v -> showPostMenu(post, position));

        // Expired state
        if (post.isExpired() || post.isArchived()) {
            holder.layoutActiveButtons.setVisibility(View.GONE);
            holder.tvExpiredLabel.setVisibility(View.VISIBLE);
        } else {
            holder.layoutActiveButtons.setVisibility(View.VISIBLE);
            holder.tvExpiredLabel.setVisibility(View.GONE);
            bindActiveButtons(holder, post, position);
        }

        holder.btnViewMembers.setText("Thành viên: " + post.getMemberCount() + "/" + post.getMaxMembers());
        holder.btnViewMembers.setTextColor(0xFF000000);
        holder.btnViewMembers.setOnClickListener(v -> {
            Intent intent = new Intent(context, ParticipantsActivity.class);
            intent.putExtra("post_id", post.getId());
            intent.putExtra("member_count", post.getMemberCount());
            intent.putExtra("max_members", post.getMaxMembers());
            context.startActivity(intent);
        });
    }

    private void showPostMenu(Post post, int position) {
        boolean isOwnPost = currentUsername.equalsIgnoreCase(post.getUsername());

        if (isOwnPost) {
            showOwnPostMenu(post, position);
        } else {
            showOtherPostMenu(post, position);
        }
    }

    private void showOwnPostMenu(Post post, int position) {
        com.google.android.material.bottomsheet.BottomSheetDialog sheet =
                new com.google.android.material.bottomsheet.BottomSheetDialog(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(context.getResources().getColor(R.color.soft_beige, null));
        layout.setPadding(0, 32, 0, 32);

        // Header
        android.widget.TextView header = new android.widget.TextView(context);
        header.setText("Tuỳ chọn bài viết");
        header.setTextSize(18);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setTextColor(context.getResources().getColor(R.color.primary_pink, null));
        header.setGravity(android.view.Gravity.CENTER);
        header.setPadding(0, 16, 0, 24);
        layout.addView(header);

        View div = new View(context);
        div.setBackgroundColor(0xFFE8E4DE);
        div.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        layout.addView(div);

        TextView tvEdit = createMenuItem("✏️  Chỉnh sửa bài viết");
        tvEdit.setOnClickListener(v -> {
            sheet.dismiss();
            showEditPostDialog(post, position);
        });
        layout.addView(tvEdit);

        TextView tvDelete = createMenuItem("🗑️  Xoá bài viết");
        tvDelete.setTextColor(0xFFE53935);
        tvDelete.setOnClickListener(v -> {
            sheet.dismiss();
            showDeleteConfirmation(post, position);
        });
        layout.addView(tvDelete);

        sheet.setContentView(layout);
        sheet.show();
    }

    private void showEditPostDialog(Post post, int position) {
        com.google.android.material.bottomsheet.BottomSheetDialog sheet =
                new com.google.android.material.bottomsheet.BottomSheetDialog(context);
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(context.getResources().getColor(R.color.soft_beige, null));
        root.setPadding(48, 40, 48, 48);

        android.widget.TextView header = new android.widget.TextView(context);
        header.setText("Chỉnh sửa bài viết");
        header.setTextSize(20);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setTextColor(context.getResources().getColor(R.color.primary_pink, null));
        header.setGravity(android.view.Gravity.CENTER);
        header.setPadding(0, 0, 0, 24);
        root.addView(header);

        // Content
        com.google.android.material.textfield.TextInputLayout tilContent =
                new com.google.android.material.textfield.TextInputLayout(context,
                        null, com.google.android.material.R.attr.textInputOutlinedStyle);
        tilContent.setHint("Nội dung");
        tilContent.setBoxCornerRadii(36, 36, 36, 36);
        tilContent.setBoxStrokeColorStateList(android.content.res.ColorStateList.valueOf(
                context.getResources().getColor(R.color.primary_pink, null)));
        com.google.android.material.textfield.TextInputEditText etContent =
                new com.google.android.material.textfield.TextInputEditText(context);
        etContent.setText(post.getContent());
        tilContent.addView(etContent);
        root.addView(tilContent);

        // Location
        com.google.android.material.textfield.TextInputLayout tilLoc =
                new com.google.android.material.textfield.TextInputLayout(context,
                        null, com.google.android.material.R.attr.textInputOutlinedStyle);
        tilLoc.setHint("Địa điểm");
        tilLoc.setBoxCornerRadii(36, 36, 36, 36);
        LinearLayout.LayoutParams tilP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tilP.topMargin = 24;
        tilLoc.setLayoutParams(tilP);
        com.google.android.material.textfield.TextInputEditText etLoc =
                new com.google.android.material.textfield.TextInputEditText(context);
        etLoc.setText(post.getLocation() != null ? post.getLocation() : "");
        tilLoc.addView(etLoc);
        root.addView(tilLoc);

        // Tag
        com.google.android.material.textfield.TextInputLayout tilTag =
                new com.google.android.material.textfield.TextInputLayout(context,
                        null, com.google.android.material.R.attr.textInputOutlinedStyle);
        tilTag.setHint("Sở thích");
        tilTag.setBoxCornerRadii(36, 36, 36, 36);
        LinearLayout.LayoutParams tilP2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tilP2.topMargin = 24;
        tilTag.setLayoutParams(tilP2);
        com.google.android.material.textfield.TextInputEditText etTag =
                new com.google.android.material.textfield.TextInputEditText(context);
        etTag.setText(post.getInterestTag() != null ? post.getInterestTag() : "");
        tilTag.addView(etTag);
        root.addView(tilTag);

        // Save button
        com.google.android.material.button.MaterialButton btnSave =
                new com.google.android.material.button.MaterialButton(context);
        btnSave.setText("Lưu thay đổi");
        btnSave.setAllCaps(false);
        btnSave.setCornerRadius(72);
        btnSave.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                context.getResources().getColor(R.color.primary_pink, null)));
        LinearLayout.LayoutParams btnP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnP.topMargin = 36;
        btnSave.setLayoutParams(btnP);
        btnSave.setOnClickListener(v -> {
            post.setContent(etContent.getText().toString());
            post.setLocation(etLoc.getText().toString());
            post.setInterestTag(etTag.getText().toString());
            notifyItemChanged(position);
            Toast.makeText(context, "Đã cập nhật bài viết", Toast.LENGTH_SHORT).show();
            sheet.dismiss();
        });
        root.addView(btnSave);

        android.widget.ScrollView scrollView2 = new android.widget.ScrollView(context);
        scrollView2.addView(root);
        sheet.setContentView(scrollView2);
        sheet.show();
    }

    private void showDeleteConfirmation(Post post, int position) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(context)
                .setTitle("Xoá bài viết")
                .setMessage("Bạn có chắc chắn muốn xoá bài viết này không?")
                .setPositiveButton("Xác nhận xoá", (dialog, which) -> {
                    FakePostRepository.getInstance().removePost(post.getId());
                    postList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, postList.size());
                    Toast.makeText(context, "Đã xoá bài viết", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void showOtherPostMenu(Post post, int position) {
        com.google.android.material.bottomsheet.BottomSheetDialog sheet =
                new com.google.android.material.bottomsheet.BottomSheetDialog(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(0, 32, 0, 32);

        TextView tvHide = createMenuItem("👁‍🗨  Ẩn bài viết");
        tvHide.setOnClickListener(v -> {
            sheet.dismiss();
            hidePost(position);
        });
        layout.addView(tvHide);

        TextView tvReport = createMenuItem("🚩  Báo cáo bài viết");
        tvReport.setOnClickListener(v -> {
            sheet.dismiss();
            showReportDialog(post, position);
        });
        layout.addView(tvReport);

        sheet.setContentView(layout);
        sheet.show();
    }

    private void hidePost(int position) {
        postList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, postList.size());
        Toast.makeText(context, "Đã ẩn bài viết", Toast.LENGTH_SHORT).show();
    }

    private void showReportDialog(Post post, int position) {
        com.google.android.material.bottomsheet.BottomSheetDialog sheet =
                new com.google.android.material.bottomsheet.BottomSheetDialog(context);

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(context.getResources().getColor(R.color.soft_beige, null));
        root.setPadding(0, 0, 0, 48);

        // Header
        android.widget.TextView header = new android.widget.TextView(context);
        header.setText("🚩 Báo cáo bài viết");
        header.setTextSize(20);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setTextColor(context.getResources().getColor(R.color.primary_pink, null));
        header.setGravity(android.view.Gravity.CENTER);
        header.setPadding(0, 40, 0, 16);
        root.addView(header);

        android.widget.TextView subHeader = new android.widget.TextView(context);
        subHeader.setText("Chọn lý do báo cáo bài viết này");
        subHeader.setTextSize(14);
        subHeader.setTextColor(context.getResources().getColor(R.color.text_secondary, null));
        subHeader.setGravity(android.view.Gravity.CENTER);
        subHeader.setPadding(0, 0, 0, 24);
        root.addView(subHeader);

        View div = new View(context);
        div.setBackgroundColor(0xFFE8E4DE);
        div.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        root.addView(div);

        // Reasons card
        com.google.android.material.card.MaterialCardView reasonCard =
                new com.google.android.material.card.MaterialCardView(context);
        LinearLayout.LayoutParams cardP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardP.setMargins(40, 24, 40, 0);
        reasonCard.setLayoutParams(cardP);
        reasonCard.setCardBackgroundColor(context.getResources().getColor(R.color.card_surface, null));
        reasonCard.setRadius(48f);
        reasonCard.setCardElevation(4f);
        reasonCard.setStrokeWidth(3);
        reasonCard.setStrokeColor(context.getResources().getColor(R.color.primary_pink, null));

        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setPadding(40, 24, 40, 24);
        String[] reasons = {
                "Nội dung thô tục",
                "Vi phạm quy định cộng đồng",
                "Spam / Quảng cáo",
                "Thông tin sai lệch",
                "Quấy rối / Bắt nạt",
                "Khác"
        };
        for (int i = 0; i < reasons.length; i++) {
            RadioButton rb = new RadioButton(context);
            rb.setText(reasons[i]);
            rb.setId(i);
            rb.setPadding(24, 20, 24, 20);
            rb.setTextSize(15);
            rb.setTextColor(context.getResources().getColor(R.color.text_primary, null));
            radioGroup.addView(rb);
        }
        reasonCard.addView(radioGroup);
        root.addView(reasonCard);

        // Custom text card
        com.google.android.material.card.MaterialCardView inputCard =
                new com.google.android.material.card.MaterialCardView(context);
        LinearLayout.LayoutParams inputP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputP.setMargins(40, 16, 40, 0);
        inputCard.setLayoutParams(inputP);
        inputCard.setCardBackgroundColor(context.getResources().getColor(R.color.card_surface, null));
        inputCard.setRadius(48f);
        inputCard.setCardElevation(4f);
        inputCard.setStrokeWidth(3);
        inputCard.setStrokeColor(context.getResources().getColor(R.color.primary_pink, null));

        EditText etCustomReason = new EditText(context);
        etCustomReason.setHint("Mô tả chi tiết (không bắt buộc)");
        etCustomReason.setMinLines(2);
        etCustomReason.setPadding(40, 28, 40, 28);
        etCustomReason.setBackground(null);
        etCustomReason.setTextSize(14);
        etCustomReason.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        etCustomReason.setFocusable(true);
        etCustomReason.setFocusableInTouchMode(true);
        etCustomReason.setClickable(true);
        inputCard.addView(etCustomReason);
        root.addView(inputCard);

        // Send button
        com.google.android.material.button.MaterialButton btnSend =
                new com.google.android.material.button.MaterialButton(context);
        btnSend.setText("Gửi báo cáo");
        btnSend.setAllCaps(false);
        btnSend.setCornerRadius(72);
        btnSend.setTextSize(16);
        btnSend.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                context.getResources().getColor(R.color.primary_pink, null)));
        LinearLayout.LayoutParams btnP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnP.setMargins(48, 32, 48, 0);
        btnSend.setLayoutParams(btnP);
        btnSend.setOnClickListener(v -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(context, "Vui lòng chọn lý do báo cáo", Toast.LENGTH_SHORT).show();
                return;
            }
            sheet.dismiss();
            // Show styled success snackbar
            android.widget.Toast successToast = Toast.makeText(context,
                    "✅ Đã gửi báo cáo vi phạm. Cảm ơn bạn!", Toast.LENGTH_LONG);
            successToast.show();
            hidePost(position);
        });
        root.addView(btnSend);

        android.widget.ScrollView sv = new android.widget.ScrollView(context);
        sv.addView(root);
        sheet.setContentView(sv);
        sheet.show();
    }

    private void bindTimeRange(PostViewHolder holder, Post post) {
        long start = post.getStartTimeMillis();
        long end = post.getEndTimeMillis();

        if (start > 0 && end > 0) {
            holder.layoutTimeRange.setVisibility(View.VISIBLE);

            String startDate = DATE_FORMAT.format(new Date(start));
            String endDate = DATE_FORMAT.format(new Date(end));
            holder.tvDateRange.setText("📅 " + startDate + " - " + endDate);

            long remaining = end - System.currentTimeMillis();
            if (remaining > 0 && remaining <= 24L * 60L * 60L * 1000L) {
                holder.tvCountdown.setVisibility(View.VISIBLE);
                holder.tvCountdown.setText("⏰ " + formatCountdown(remaining));
            } else if (remaining <= 0) {
                holder.tvCountdown.setVisibility(View.VISIBLE);
                holder.tvCountdown.setText("Hết hạn");
            } else {
                holder.tvCountdown.setVisibility(View.GONE);
            }
        } else {
            holder.layoutTimeRange.setVisibility(View.GONE);
        }
    }

    private String formatCountdown(long millis) {
        long hours = millis / (60L * 60L * 1000L);
        long minutes = (millis % (60L * 60L * 1000L)) / (60L * 1000L);

        if (hours > 0) {
            return "Còn " + hours + " giờ";
        } else {
            return "Còn " + minutes + " phút";
        }
    }

    private void bindActiveButtons(PostViewHolder holder, Post post, int position) {
        boolean isOwnPost = currentUsername.equalsIgnoreCase(post.getUsername());

        if (isOwnPost) {
            // Own post: don't show join button, only show members centered
            holder.btnJoinGroup.setVisibility(View.GONE);

            // Remove weight so gravity="center" on parent works
            android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    (int) (48 * holder.itemView.getResources().getDisplayMetrics().density));
            params.weight = 0;
            holder.btnViewMembers.setLayoutParams(params);
            holder.btnViewMembers.setPadding(48, 0, 48, 0);
        } else {
            // Other's post: show join button with weighted layout
            holder.btnJoinGroup.setVisibility(View.VISIBLE);

            // Restore weight-based layout for both buttons
            android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                    0,
                    (int) (48 * holder.itemView.getResources().getDisplayMetrics().density));
            params.weight = 1;
            params.setMarginStart((int) (6 * holder.itemView.getResources().getDisplayMetrics().density));
            holder.btnViewMembers.setLayoutParams(params);
            holder.btnViewMembers.setPadding(0, 0, 0, 0);

            if (post.isJoined()) {
                // Already joined: show "Mở đoạn chat" to open conversation
                holder.btnJoinGroup.setText("💬 Mở đoạn chat");
                holder.btnJoinGroup.setEnabled(true);
                holder.btnJoinGroup.setAlpha(1.0f);
                holder.btnJoinGroup.setOnClickListener(v -> {
                    Intent intent = new Intent(context, com.example.weconnect.activities.ConversationActivity.class);
                    intent.putExtra("chat_name", post.getUsername());
                    context.startActivity(intent);
                });
            } else if (post.isPendingApproval()) {
                // Pending approval: show dimmed "Đang chờ duyệt"
                holder.btnJoinGroup.setText("⏳ Đang chờ duyệt");
                holder.btnJoinGroup.setEnabled(false);
                holder.btnJoinGroup.setAlpha(0.6f);
                holder.btnJoinGroup.setOnClickListener(null);
            } else {
                // Not joined: show active "Tham gia" button
                holder.btnJoinGroup.setText("Tham gia");
                holder.btnJoinGroup.setEnabled(true);
                holder.btnJoinGroup.setAlpha(1.0f);
                holder.btnJoinGroup.setOnClickListener(v -> {
                    post.setPendingApproval(true);
                    Toast.makeText(context, "Đã gửi yêu cầu tham gia " + post.getUsername(), Toast.LENGTH_SHORT).show();
                    holder.btnJoinGroup.setText("⏳ Đang chờ duyệt");
                    holder.btnJoinGroup.setEnabled(false);
                    holder.btnJoinGroup.setAlpha(0.6f);
                });
            }
        }
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

    private TextView createMenuItem(String text) {
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setTextSize(16);
        tv.setTextColor(context.getResources().getColor(R.color.text_primary, null));
        tv.setPadding(64, 40, 64, 40);
        tv.setBackgroundResource(android.R.drawable.list_selector_background);
        tv.setClickable(true);
        tv.setFocusable(true);
        return tv;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, ivPostImage, ivPostMenu;
        View cvPostImage;
        TextView tvUsername, tvTime, tvContent;
        TextView btnJoinGroup, btnViewMembers;
        TextView tvTag, tvLocation;
        LinearLayout layoutTimeRange, layoutActiveButtons;
        TextView tvDateRange, tvCountdown, tvExpiredLabel;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.post_item_avatar);
            tvUsername = itemView.findViewById(R.id.post_item_username);
            tvTime = itemView.findViewById(R.id.post_item_time);
            tvContent = itemView.findViewById(R.id.post_item_content);
            cvPostImage = itemView.findViewById(R.id.cvPostImage);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            btnJoinGroup = itemView.findViewById(R.id.btnJoinGroup);
            btnViewMembers = itemView.findViewById(R.id.btnViewMembers);
            tvTag = itemView.findViewById(R.id.post_item_tag);
            tvLocation = itemView.findViewById(R.id.post_item_location);
            layoutTimeRange = itemView.findViewById(R.id.layoutTimeRange);
            tvDateRange = itemView.findViewById(R.id.post_item_date_range);
            tvCountdown = itemView.findViewById(R.id.post_item_countdown);
            layoutActiveButtons = itemView.findViewById(R.id.layoutActiveButtons);
            tvExpiredLabel = itemView.findViewById(R.id.tvExpiredLabel);
            ivPostMenu = itemView.findViewById(R.id.ivPostMenu);
        }
    }
}
