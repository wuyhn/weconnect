package com.example.weconnect.data.repository;

import com.example.weconnect.data.model.Post;
import java.util.List;

public interface SearchDataSource {
    List<Post> searchPosts(String keyword);
    List<String> searchUsers(String keyword);
}
