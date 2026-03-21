package com.example.weconnect.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.example.weconnect.adapters.SearchResultAdapter;
import com.example.weconnect.data.FakeSearchDataSource;
import com.example.weconnect.data.FakePostRepository;
import com.example.weconnect.data.SearchRepository;
import com.example.weconnect.models.Post;
import com.example.weconnect.models.SearchResultItem;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ImageView ivBackSearch;
    private TextInputEditText etSearch;
    private RecyclerView rvSearchResults;

    private SearchResultAdapter searchResultAdapter;
    private SearchRepository searchRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupRecyclerView();
        setupRepository();
        setupClickListeners();
        setupSearchListener();
    }

    private void initViews() {
        ivBackSearch = findViewById(R.id.ivBackSearch);
        etSearch = findViewById(R.id.etSearch);
        rvSearchResults = findViewById(R.id.rvSearchResults);
    }

    private void setupRecyclerView() {
        searchResultAdapter = new SearchResultAdapter(this);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(searchResultAdapter);
    }

    private void setupRepository() {
        ArrayList<Post> posts = new ArrayList<>(FakePostRepository.getInstance().getActivePosts());
        searchRepository = new SearchRepository(new FakeSearchDataSource(posts));
    }

    private void setupClickListeners() {
        ivBackSearch.setOnClickListener(v -> finish());
    }

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void performSearch(String keyword) {
        List<SearchResultItem> results = new ArrayList<>();

        List<String> users = searchRepository.searchUsers(keyword);
        if (!users.isEmpty()) {
            results.add(new SearchResultItem(
                    SearchResultItem.TYPE_SECTION,
                    "Người dùng",
                    "",
                    0
            ));

            for (String user : users) {
                results.add(new SearchResultItem(
                        SearchResultItem.TYPE_USER,
                        user,
                        "",
                        R.drawable.ic_user_placeholder
                ));
            }
        }

        List<Post> posts = searchRepository.searchPosts(keyword);
        if (!posts.isEmpty()) {
            results.add(new SearchResultItem(
                    SearchResultItem.TYPE_SECTION,
                    "Bài viết",
                    "",
                    0
            ));

            for (Post post : posts) {
                String subtitle;

                if (post.getLocation() != null && post.getLocation().length() > 0) {
                    subtitle = "📍 " + post.getLocation();
                } else if (post.getUsername() != null && post.getUsername().length() > 0) {
                    subtitle = post.getUsername();
                } else {
                    subtitle = "";
                }

                results.add(new SearchResultItem(
                        SearchResultItem.TYPE_POST,
                        post.getContent(),
                        subtitle,
                        0,
                        post.getUsername(),
                        post.getContent(),
                        post.getInterestTag(),
                        post.getLocation(),
                        post.getMemberCount(),
                        post.getMaxMembers(),
                        post
                ));
            }
        }

        searchResultAdapter.submitList(results);
    }
}
