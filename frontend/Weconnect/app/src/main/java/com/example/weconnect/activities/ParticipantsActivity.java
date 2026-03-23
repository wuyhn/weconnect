package com.example.weconnect.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.adapters.ParticipantAdapter;

import java.util.ArrayList;
import java.util.List;

public class ParticipantsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        ImageView ivClose = findViewById(R.id.ivCloseParticipants);
        TextView tvCount = findViewById(R.id.tvParticipantsCount);
        RecyclerView rvParticipants = findViewById(R.id.rvParticipants);

        int memberCount = getIntent().getIntExtra("member_count", 0);
        int maxMembers = getIntent().getIntExtra("max_members", 0);

        tvCount.setText("👥 " + memberCount + "/" + maxMembers);

        // Generate fake participants
        List<ParticipantAdapter.Participant> participants = generateFakeParticipants(memberCount);

        rvParticipants.setLayoutManager(new LinearLayoutManager(this));
        rvParticipants.setAdapter(new ParticipantAdapter(this, participants));

        ivClose.setOnClickListener(v -> finish());
    }

    private List<ParticipantAdapter.Participant> generateFakeParticipants(int count) {
        String[] fakeNames = {
                "Minh Hoang", "Lan Anh", "Duc Anh", "Thi Tuyet",
                "Hai Dang", "Quynh Nguyen", "Van Khanh", "Thu Huong",
                "Quoc Bao", "Thanh Nhan"
        };

        List<ParticipantAdapter.Participant> list = new ArrayList<>();
        for (int i = 0; i < Math.min(count, fakeNames.length); i++) {
            list.add(new ParticipantAdapter.Participant(
                    fakeNames[i],
                    R.drawable.ic_user_placeholder
            ));
        }
        return list;
    }
}