package com.weconnect.backend.service;

import com.weconnect.backend.dto.ChangePasswordRequest;
import com.weconnect.backend.dto.UpdateProfileRequest;
import com.weconnect.backend.dto.UserProfileResponse;
import com.weconnect.backend.entity.User;
import com.weconnect.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        return toProfileResponse(user);
    }

    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getBirthday() != null) user.setBirthday(request.getBirthday());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getInterestTags() != null) user.setInterestTags(request.getInterestTags());

        userRepository.save(user);
        return toProfileResponse(user);
    }

    public String changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Đổi mật khẩu thành công!";
    }

    public UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .birthday(user.getBirthday())
                .gender(user.getGender())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .interestTags(user.getInterestTags())
                .averageRating(user.getAverageRating())
                .reputationScore(user.getReputationScore())
                .build();
    }
}
