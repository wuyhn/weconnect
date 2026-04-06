package com.weconnect.backend.controller;

import com.weconnect.backend.dto.request.ApiResponse;
import com.weconnect.backend.entity.User;
import com.weconnect.backend.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Lấy danh sách review của user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReviews(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.builder()
                .code(1000).message("Thành công")
                .result(reviewService.getReviews(userId)).build());
    }

    // Viết review
    @PostMapping
    public ResponseEntity<?> createReview(Authentication authentication,
                                          @RequestBody Map<String, Object> body) {
        User user = (User) authentication.getPrincipal();
        Long reviewedUserId = Long.valueOf(body.get("reviewedUserId").toString());
        String activityName = (String) body.get("activityName");
        String reputationLabel = (String) body.get("reputationLabel");
        String comment = (String) body.get("comment");

        try {
            String result = reviewService.createReview(user.getId(), reviewedUserId,
                    activityName, reputationLabel, comment);
            return ResponseEntity.ok(ApiResponse.builder()
                    .code(1000).message(result).build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .code(1030).message(e.getMessage()).build());
        }
    }
}
