package com.weconnect.backend.service;

import com.weconnect.backend.entity.Post;
import com.weconnect.backend.entity.PostMember;
import com.weconnect.backend.entity.User;
import com.weconnect.backend.entity.UserReview;
import com.weconnect.backend.repository.PostMemberRepository;
import com.weconnect.backend.repository.PostRepository;
import com.weconnect.backend.repository.UserRepository;
import com.weconnect.backend.repository.UserReviewRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ReviewService {

    private final UserReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PostMemberRepository postMemberRepository;
    private final PostRepository postRepository;

    public ReviewService(UserReviewRepository reviewRepository, UserRepository userRepository,
                         PostMemberRepository postMemberRepository, PostRepository postRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.postMemberRepository = postMemberRepository;
        this.postRepository = postRepository;
    }

    // Lấy danh sách review
    public List<Map<String, Object>> getReviews(Long userId) {
        List<UserReview> reviews = reviewRepository.findByReviewedUserIdOrderByCreatedAtDesc(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (UserReview r : reviews) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("reviewerId", r.getReviewerId());

            User reviewer = userRepository.findById(r.getReviewerId()).orElse(null);
            map.put("reviewerName", reviewer != null ? reviewer.getFullName() : "Unknown");

            map.put("activityName", r.getActivityName());
            map.put("reputationLabel", r.getReputationLabel());
            map.put("comment", r.getComment());
            map.put("createdAt", r.getCreatedAt());
            result.add(map);
        }
        return result;
    }

    // Viết review
    public String createReview(Long reviewerId, Long reviewedUserId,
                               String activityName, String reputationLabel, String comment) {
        if (reviewerId.equals(reviewedUserId)) {
            throw new RuntimeException("Không thể đánh giá chính mình.");
        }

        // Kiểm tra 2 user phải cùng nhóm (cùng là thành viên/tác giả của ít nhất 1 bài đăng)
        if (!hasCommonGroup(reviewerId, reviewedUserId)) {
            throw new RuntimeException("Hai người phải có cùng nhóm hoạt động mới được đánh giá.");
        }

        UserReview review = UserReview.builder()
                .reviewerId(reviewerId)
                .reviewedUserId(reviewedUserId)
                .activityName(activityName)
                .reputationLabel(reputationLabel)
                .comment(comment)
                .build();

        reviewRepository.save(review);
        return "Đánh giá thành công!";
    }

    /**
     * Kiểm tra 2 user có cùng nhóm hoạt động không.
     * Cùng nhóm = cả 2 đều là thành viên (APPROVED) hoặc tác giả của cùng 1 bài đăng.
     */
    private boolean hasCommonGroup(Long userId1, Long userId2) {
        // Lấy tất cả post IDs mà userId1 tham gia (approved) hoặc là tác giả
        Set<Long> user1PostIds = getParticipatedPostIds(userId1);
        Set<Long> user2PostIds = getParticipatedPostIds(userId2);

        // Kiểm tra có post ID chung không
        for (Long postId : user1PostIds) {
            if (user2PostIds.contains(postId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Lấy danh sách post IDs mà user tham gia (approved member) hoặc là tác giả.
     */
    private Set<Long> getParticipatedPostIds(Long userId) {
        Set<Long> postIds = new HashSet<>();

        // Bài đăng mà user là tác giả
        List<Post> authoredPosts = postRepository.findByAuthorIdOrderByCreatedAtDesc(userId);
        for (Post p : authoredPosts) {
            postIds.add(p.getId());
        }

        // Bài đăng mà user là thành viên đã được duyệt
        List<PostMember> memberships = postMemberRepository.findByUserIdAndStatus(userId, PostMember.Status.APPROVED);
        for (PostMember pm : memberships) {
            postIds.add(pm.getPostId());
        }

        return postIds;
    }
}

