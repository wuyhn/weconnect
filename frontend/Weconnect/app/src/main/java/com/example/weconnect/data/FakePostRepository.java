package com.example.weconnect.data;

import com.example.weconnect.R;
import com.example.weconnect.models.Post;

import java.util.ArrayList;
import java.util.List;

public class FakePostRepository {

    private static final long ONE_HOUR = 60L * 60L * 1000L;
    private static final long ONE_DAY = 24L * ONE_HOUR;
    private static final String CURRENT_USERNAME = "Quynh Nguyen";

    private static FakePostRepository instance;

    private final List<Post> allPosts = new ArrayList<>();

    private FakePostRepository() {
        seedPosts();
    }

    public static synchronized FakePostRepository getInstance() {
        if (instance == null) {
            instance = new FakePostRepository();
        }
        return instance;
    }

    public List<Post> getActivePosts() {
        List<Post> activePosts = new ArrayList<>();
        for (Post post : allPosts) {
            if (post.isActive()) {
                activePosts.add(post);
            }
        }
        return activePosts;
    }

    public List<Post> getAllPosts() {
        return new ArrayList<>(allPosts);
    }

    public List<Post> getArchivedPostsForUser(String username) {
        List<Post> archivedPosts = new ArrayList<>();
        for (Post post : allPosts) {
            boolean sameUser = post.getUsername() != null
                    && post.getUsername().equalsIgnoreCase(username);
            if (sameUser && (post.isArchived() || post.isExpired())) {
                archivedPosts.add(post);
            }
        }
        return archivedPosts;
    }

    public void addPost(Post post) {
        allPosts.add(0, post);
    }

    public String getCurrentUsername() {
        return CURRENT_USERNAME;
    }

    private void seedPosts() {
        long now = System.currentTimeMillis();

        allPosts.add(new Post(
                "1",
                CURRENT_USERNAME,
                "15 minutes ago",
                "Anyone up for coffee and a quick planning session this evening?",
                "Coffee meetup",
                "Ha Dong, Ha Noi",
                R.drawable.ic_user_placeholder,
                0,
                2,
                120,
                15,
                20,
                false,
                now - ONE_HOUR,
                now + ONE_DAY,
                false
        ));

        allPosts.add(new Post(
                "2",
                "Minh Hoang",
                "1 hour ago",
                "Looking for teammates to join a design and code co-working activity.",
                "Design and code",
                "Cau Giay, Ha Noi",
                R.drawable.ic_user_placeholder,
                R.drawable.ic_launcher_background,
                4,
                450,
                89,
                8,
                true,
                now - 2L * ONE_HOUR,
                now + 2L * ONE_DAY,
                false
        ));

        allPosts.add(new Post(
                "3",
                "Lan Anh",
                "3 hours ago",
                "Need a food buddy to explore a new noodle place this weekend.",
                "Food trip",
                "Thu Duc, Ho Chi Minh City",
                R.drawable.ic_user_placeholder,
                0,
                3,
                56,
                42,
                10,
                false,
                now - 3L * ONE_HOUR,
                now + 10L * ONE_HOUR,
                false
        ));

        allPosts.add(new Post(
                "4",
                CURRENT_USERNAME,
                "2 days ago",
                "Morning badminton session completed. Thanks everyone for joining.",
                "Badminton",
                "Thanh Xuan, Ha Noi",
                R.drawable.ic_user_placeholder,
                0,
                6,
                210,
                34,
                12,
                false,
                now - 3L * ONE_DAY,
                now - ONE_DAY,
                true
        ));
    }
}
