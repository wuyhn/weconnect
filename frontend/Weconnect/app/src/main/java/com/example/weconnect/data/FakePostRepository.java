package com.example.weconnect.data;

import com.example.weconnect.R;
import com.example.weconnect.models.Post;

import java.util.ArrayList;
import java.util.List;

public class FakePostRepository {

    private static final long ONE_HOUR = 60L * 60L * 1000L;
    private static final long ONE_DAY = 24L * ONE_HOUR;
    private String currentUsername = "Quỳnh Nguyễn";

    private static FakePostRepository instance;

    private final List<Post> allPosts = new ArrayList<>();
    private List<String> userInterests = new ArrayList<>();

    public void setUserInterests(List<String> interests) {
        this.userInterests = new ArrayList<>(interests);
    }

    public List<String> getUserInterests() {
        return new ArrayList<>(userInterests);
    }

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

    public boolean removePost(String id) {
        java.util.Iterator<Post> iterator = allPosts.iterator();
        while (iterator.hasNext()) {
            Post post = iterator.next();
            if (post.getId() != null && post.getId().equals(id)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    public List<Post> getActivePostsForUser(String username) {
        List<Post> activePosts = new ArrayList<>();
        for (Post post : allPosts) {
            boolean sameUser = post.getUsername() != null
                    && post.getUsername().equalsIgnoreCase(username);
            if (sameUser && post.isActive()) {
                activePosts.add(post);
            }
        }
        return activePosts;
    }

    private void seedPosts() {
        long now = System.currentTimeMillis();

        allPosts.add(new Post(
                "1",
                currentUsername,
                "15 phút trước",
                "Ai muốn đi cà phê và lên kế hoạch buổi tối nay không?",
                "Cà phê",
                "Hà Đông, Hà Nội",
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
                "Minh Hoàng",
                "1 giờ trước",
                "Tìm đồng đội tham gia hoạt động lập trình và thiết kế cùng nhau.",
                "Lập trình",
                "Cầu Giấy, Hà Nội",
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
                "3 giờ trước",
                "Cần bạn ăn uống để khám phá quán phở mới cuối tuần này.",
                "Ẩm thực",
                "Thủ Đức, TP.HCM",
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
                currentUsername,
                "2 ngày trước",
                "Buổi đánh cầu lông sáng nay hoàn thành. Cảm ơn mọi người đã tham gia.",
                "Cầu lông",
                "Thanh Xuân, Hà Nội",
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
