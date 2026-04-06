package com.weconnect.backend.service;

import com.weconnect.backend.entity.User;
import com.weconnect.backend.entity.UserReview;
import com.weconnect.backend.repository.UserRepository;
import com.weconnect.backend.repository.UserReviewRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewService {

    private final UserReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewService(UserReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
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
}
