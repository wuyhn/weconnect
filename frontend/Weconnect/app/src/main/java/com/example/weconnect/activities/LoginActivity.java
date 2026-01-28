package com.example.weconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weconnect.R;
import com.example.weconnect.api.AuthApiService; // Import Interface của bạn
import com.example.weconnect.models.LoginRequest; // Import Model của bạn
import com.example.weconnect.models.User;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private TextView tvErrorEmail, tvErrorPassword, tvRegister;
    private Button btnLogin;

    // --- BƯỚC 1: Khai báo apiService ---
    private AuthApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupSmartValidation();

        // --- BƯỚC 2: Khởi tạo Retrofit ---
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // Địa chỉ kết nối tới Spring Boot
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(AuthApiService.class);

        // Hiệu ứng nảy cho nút Đăng nhập
        btnLogin.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
            }
            return false;
        });

        // Xử lý sự kiện bấm nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            checkFieldsOnSubmit();

            if (validateAllFields()) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // --- BƯỚC 3: Gọi API thật thay vì check giả lập ---
                loginWithBackend(email, password);
            } else {
                Toast.makeText(this, "Vui lòng hoàn thiện đúng thông tin", Toast.LENGTH_SHORT).show();
            }
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    // Hàm gọi tới Spring Boot
    private void loginWithBackend(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);

        apiService.login(loginRequest).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    // Đăng nhập thành công
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Xử lý lỗi: Tài khoản không tồn tại hoặc sai mật khẩu từ Backend trả về
                    try {
                        String messageFromServer = response.errorBody().string();
                        // Hiển thị lỗi lên TextView lỗi mật khẩu hoặc Toast
                        showError(tvErrorPassword, messageFromServer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Lỗi này xảy ra khi Backend chưa chạy hoặc sai IP
                Toast.makeText(LoginActivity.this, "Không thể kết nối Server. Hãy kiểm tra Backend!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupSmartValidation() {
        etEmail.addTextChangedListener(new SimpleTextWatcher(s -> {
            String email = s.toString().trim();
            if (email.isEmpty()) {
                showError(tvErrorEmail, "Email không được để trống");
            } else if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.com$")) {
                showError(tvErrorEmail, "Email phải kết thúc bằng .com");
            } else {
                tvErrorEmail.setVisibility(View.GONE);
            }
        }));

        etPassword.addTextChangedListener(new SimpleTextWatcher(s -> {
            if (s.length() == 0) {
                showError(tvErrorPassword, "Mật khẩu không được để trống");
            } else if (s.length() < 8) {
                showError(tvErrorPassword, "Mật khẩu phải ít nhất 8 ký tự");
            } else {
                tvErrorPassword.setVisibility(View.GONE);
            }
        }));
    }

    private void showError(TextView tv, String message) {
        tv.setText("⚠ " + message);
        tv.setVisibility(View.VISIBLE);
    }

    private void checkFieldsOnSubmit() {
        if (etEmail.getText().toString().isEmpty()) showError(tvErrorEmail, "Email không được để trống");
        if (etPassword.getText().toString().isEmpty()) showError(tvErrorPassword, "Mật khẩu không được để trống");
    }

    private boolean validateAllFields() {
        return tvErrorEmail.getVisibility() == View.GONE &&
                tvErrorPassword.getVisibility() == View.GONE &&
                !etEmail.getText().toString().isEmpty() &&
                !etPassword.getText().toString().isEmpty();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvErrorEmail = findViewById(R.id.tvErrorEmail);
        tvErrorPassword = findViewById(R.id.tvErrorPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    interface TextChangedListener { void onTextChanged(CharSequence s); }
    class SimpleTextWatcher implements TextWatcher {
        private TextChangedListener listener;
        public SimpleTextWatcher(TextChangedListener l) { this.listener = l; }
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { listener.onTextChanged(s); }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}