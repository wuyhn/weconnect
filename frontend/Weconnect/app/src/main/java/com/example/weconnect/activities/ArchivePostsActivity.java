package com.example.weconnect.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.adapters.PostAdapter;
import com.example.weconnect.data.FakePostRepository;
import com.example.weconnect.models.Post;

import java.util.List;

public class ArchivePostsActivity extends AppCompatActivity {

    private ImageView ivBackArchive;
    private TextView tvArchiveTitle;
    private TextView tvArchiveEmpty;
    private RecyclerView rvArchivedPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_posts);

        initViews();
        setupClickListeners();
        bindArchivePosts();
    }

    private void initViews() {
        ivBackArchive = findViewById(R.id.ivBackArchive);
        tvArchiveTitle = findViewById(R.id.tvArchiveTitle);
        tvArchiveEmpty = findViewById(R.id.tvArchiveEmpty);
        rvArchivedPosts = findViewById(R.id.rvArchivedPosts);
    }

    private void setupClickListeners() {
        ivBackArchive.setOnClickListener(v -> finish());
    }

    private void bindArchivePosts() {
        String username = getIntent().getStringExtra("username");
        if (username == null || username.trim().isEmpty()) {
            username = FakePostRepository.getInstance().getCurrentUsername();
        }

        tvArchiveTitle.setText(username + " archive");

        List<Post> archivedPosts = FakePostRepository.getInstance().getArchivedPostsForUser(username);
        rvArchivedPosts.setLayoutManager(new LinearLayoutManager(this));
        rvArchivedPosts.setAdapter(new PostAdapter(this, archivedPosts));
        tvArchiveEmpty.setVisibility(archivedPosts.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
