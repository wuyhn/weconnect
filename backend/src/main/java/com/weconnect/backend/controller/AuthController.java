package com.weconnect.backend.controller;

import com.weconnect.backend.dto.AuthRequest;
import com.weconnect.backend.entity.User;
import com.weconnect.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        String result = authService.register(request);
        if (result.contains("thành công")) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            User user = authService.login(request.getEmail(), request.getPassword());
            // Trả về object chứa thông tin cần thiết để Android chuyển trang
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            // Trả về nội dung lỗi (401 Unauthorized) để Android hiển thị lên TextView lỗi
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
