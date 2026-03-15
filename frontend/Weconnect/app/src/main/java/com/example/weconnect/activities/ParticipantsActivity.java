package com.example.weconnect.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.weconnect.R;

public class ParticipantsActivity extends AppCompatActivity {

    private ImageView ivCloseParticipants;
    private TextView tvParticipantsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_participant_limit);

        ivCloseParticipants = findViewById(R.id.ivCloseParticipant);
        tvParticipantsCount = findViewById(R.id.tvParticipantsCount);

        int memberCount = getIntent().getIntExtra("member_count", 0);
        int maxMembers = getIntent().getIntExtra("max_members", 0);

        tvParticipantsCount.setText("👥 " + memberCount + "/" + maxMembers);

        ivCloseParticipants.setOnClickListener(v -> finish());
    }
}