package com.example.weconnect.presentation.social.blocked;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weconnect.R;
import com.example.weconnect.data.repository.FakeSocialRepository;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class BlockedUsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        androidx.constraintlayout.widget.ConstraintLayout root = new androidx.constraintlayout.widget.ConstraintLayout(this);
        root.setBackgroundColor(getResources().getColor(R.color.soft_beige, null));
        root.setFitsSystemWindows(true);
        setContentView(root);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setId(android.R.id.content + 100);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(48, 48, 48, 32);

        ImageView ivBack = new ImageView(this);
        ivBack.setImageResource(R.drawable.ic_close);
        ivBack.setPadding(24, 24, 24, 24);
        ivBack.setOnClickListener(v -> finish());
        ivBack.setColorFilter(getResources().getColor(R.color.primary_pink, null));
        LinearLayout.LayoutParams backLp = new LinearLayout.LayoutParams(96, 96);
        ivBack.setLayoutParams(backLp);
        header.addView(ivBack);

        TextView title = new TextView(this);
        title.setText("Danh sách chặn");
        title.setTextSize(20);
        title.setTextColor(getResources().getColor(R.color.primary_pink, null));
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(24, 0, 0, 0);
        header.addView(title);

        root.addView(header);

        // Content
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(48, 0, 48, 48);
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams contentLp =
                new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT,
                        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT);
        contentLp.topToBottom = header.getId();
        contentLp.topMargin = 16;
        content.setLayoutParams(contentLp);
        root.addView(content);

        List<String> blockedUsers = FakeSocialRepository.getInstance().getBlockedUsers();

        if (blockedUsers.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("Bạn chưa chặn ai");
            empty.setTextSize(15);
            empty.setTextColor(getResources().getColor(R.color.text_secondary, null));
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, 120, 0, 0);
            content.addView(empty);
        } else {
            for (String blocked : blockedUsers) {
                com.google.android.material.card.MaterialCardView card = new com.google.android.material.card.MaterialCardView(this);
                LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                cardLp.bottomMargin = 24;
                card.setLayoutParams(cardLp);
                card.setCardBackgroundColor(getResources().getColor(R.color.card_surface, null));
                card.setRadius(48f);
                card.setCardElevation(6f);
                card.setStrokeWidth(0);

                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding(36, 28, 36, 28);

                ImageView avatar = new ImageView(this);
                avatar.setImageResource(R.drawable.ic_user_placeholder);
                LinearLayout.LayoutParams avatarLp = new LinearLayout.LayoutParams(96, 96);
                avatar.setLayoutParams(avatarLp);
                avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
                row.addView(avatar);

                TextView name = new TextView(this);
                name.setText(blocked);
                name.setTextSize(15);
                name.setTextColor(getResources().getColor(R.color.text_primary, null));
                name.setTypeface(null, android.graphics.Typeface.BOLD);
                name.setPadding(32, 0, 0, 0);
                LinearLayout.LayoutParams nameLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                name.setLayoutParams(nameLp);
                row.addView(name);

                MaterialButton btnUnblock = new MaterialButton(this);
                btnUnblock.setText("Bỏ chặn");
                btnUnblock.setAllCaps(false);
                btnUnblock.setTextSize(12);
                btnUnblock.setCornerRadius(60);
                btnUnblock.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF4D6D));
                LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, 84);
                btnUnblock.setLayoutParams(btnLp);
                btnUnblock.setInsetTop(0);
                btnUnblock.setInsetBottom(0);
                btnUnblock.setMinWidth(0);
                btnUnblock.setMinimumWidth(0);
                btnUnblock.setPadding(40, 0, 40, 0);
                btnUnblock.setOnClickListener(v -> {
                    FakeSocialRepository.getInstance().unblockUser(blocked);
                    Toast.makeText(this, "Đã bỏ chặn " + blocked, Toast.LENGTH_SHORT).show();
                    recreate();
                });
                row.addView(btnUnblock);

                card.addView(row);
                content.addView(card);
            }
        }
    }
}
