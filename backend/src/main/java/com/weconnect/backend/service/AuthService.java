package com.weconnect.backend.service;

import com.weconnect.backend.dto.AuthRequest;
import com.weconnect.backend.entity.User;
import com.weconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
//    @Autowired private PasswordEncoder passwordEncoder;

    // LOGIC ĐĂNG KÝ
    public String register(AuthRequest request) {
        // Kiểm tra xem email đã tồn tại chưa
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Email này đã được sử dụng!";
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setFullName(request.getFullName());
        // Mã hóa mật khẩu trước khi lưu vào MySQL
        newUser.setPassword(request.getPassword());
//        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setBirthday(request.getBirthday()); // Lấy từ request bỏ vào Entity
        newUser.setGender(request.getGender());
        newUser.setBlocked(false);

        userRepository.save(newUser);
        return "Đăng ký tài khoản thành công!";
    }

    // LOGIC ĐĂNG NHẬP
    public User login(String email, String password) {
        // 1. Kiểm tra Email có tồn tại không
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            throw new RuntimeException("Tài khoản không tồn tại. Vui lòng kiểm tra lại hoặc đăng ký tài khoản mới.");
        }

        // 2. Kiểm tra tài khoản có bị khóa không (theo bảng thiết kế)
        if (user.isBlocked()) {
            throw new RuntimeException("Tài khoản của bạn hiện đang bị khóa.");
        }

        // 3. Kiểm tra mật khẩu
        if (!password.equals(user.getPassword())) {
//        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mật khẩu sai, vui lòng kiểm tra lại.");
        }

        return user; // Đăng nhập thành công trả về thông tin User
    }
}
