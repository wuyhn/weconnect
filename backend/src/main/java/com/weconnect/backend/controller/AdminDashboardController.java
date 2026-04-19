package com.weconnect.backend.controller;

import com.weconnect.backend.dto.request.ApiResponse;
import com.weconnect.backend.repository.PostRepository;
import com.weconnect.backend.repository.UserRepository;
import com.weconnect.backend.repository.UserReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserReviewRepository userReviewRepository;

    public AdminDashboardController(UserRepository userRepository,
                                     PostRepository postRepository,
                                     UserReviewRepository userReviewRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.userReviewRepository = userReviewRepository;
    }

    // Thống kê cho dashboard
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        long totalUsers = userRepository.count();
        long totalPosts = postRepository.count();
        long totalReviews = userReviewRepository.count();

        // Đếm users bị block
        long blockedUsers = userRepository.findAll().stream()
                .filter(u -> u.isBlocked()).count();

        // Đếm posts đã archived
        long archivedPosts = postRepository.findAll().stream()
                .filter(p -> p.isArchived()).count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalPosts", totalPosts);
        stats.put("totalReviews", totalReviews);
        stats.put("blockedUsers", blockedUsers);
        stats.put("archivedPosts", archivedPosts);

        return ResponseEntity.ok(ApiResponse.builder()
                .code(1000).message("Thành công")
                .result(stats).build());
    }
}
