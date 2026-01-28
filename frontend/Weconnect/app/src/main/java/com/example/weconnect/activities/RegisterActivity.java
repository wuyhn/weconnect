package com.example.weconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log; // Thêm để theo dõi lỗi
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton; // Thêm để lấy giá trị giới tính
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.weconnect.R;
import com.example.weconnect.api.AuthApiService; // PHẢI CÓ
import com.example.weconnect.models.User; // Dùng model User của bạn
import java.util.Calendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etBirthday, etEmail, etPassword;
    private TextView tvErrorName, tvErrorBirthday, tvErrorEmail, tvErrorPassword, tvErrorGender, tvBackToLogin;
    private RadioGroup rgGender;
    private Button btnRegister;

    // --- THÊM: Khai báo apiService ---
    private AuthApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupSmartValidation();

        // --- THÊM: Khởi tạo kết nối tới Server ---
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(ScalarsConverterFactory.create()) // Để đọc chuỗi "Đăng ký thành công"
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(AuthApiService.class);

        btnRegister.setOnClickListener(v -> {
            checkFieldsOnSubmit();

            if (validateAllFields()) {
                // --- THÊM: Gọi hàm gửi dữ liệu thật ---
                registerUser();
            } else {
                Toast.makeText(this, "Vui lòng hoàn thiện đúng thông tin", Toast.LENGTH_SHORT).show();
            }
        });

        tvBackToLogin.setOnClickListener(v -> finish());
    }

    // --- HÀM GỬI DỮ LIỆU SANG BACKEND ---
    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String birthday = etBirthday.getText().toString().trim();

        // Lấy giới tính từ RadioGroup
        int selectedId = rgGender.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        String gender = radioButton.getText().toString();

        // Đóng gói vào Object User (Model bạn đã tạo)
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setFullName(fullName);
        newUser.setBirthday(birthday);
        newUser.setGender(gender);

        // Gửi lệnh POST sang Spring Boot
        apiService.register(newUser).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    // KẾT QUẢ THẬT: Đã vào MySQL
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công và đã lưu DB!", Toast.LENGTH_LONG).show();
                    finish(); // Quay lại trang Login
                } else {
                    try {
                        String error = response.errorBody().string();
                        Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối Server!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupSmartValidation() {
        // 1. Kiểm tra Tên
        etFullName.addTextChangedListener(new SimpleTextWatcher(s -> {
            if (s.length() == 0) {
                showError(tvErrorName, "Họ tên không được để trống");
            } else if (s.length() < 8) {
                showError(tvErrorName, "Họ tên phải ít nhất 8 ký tự");
            } else {
                tvErrorName.setVisibility(View.GONE);
            }
        }));

        // 2. Kiểm tra Ngày sinh (Tự thêm / và check tuổi)
        etBirthday.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d]", "");
                    String formatted = clean;
                    if (clean.length() >= 2 && clean.length() < 4) formatted = clean.substring(0, 2) + "/" + clean.substring(2);
                    else if (clean.length() >= 4) formatted = clean.substring(0, 2) + "/" + clean.substring(2, 4) + "/" + clean.substring(4);

                    current = formatted;
                    etBirthday.setText(current);
                    etBirthday.setSelection(current.length());

                    if (current.isEmpty()) {
                        showError(tvErrorBirthday, "Ngày sinh không được để trống");
                    } else if (current.length() < 10) {
                        showError(tvErrorBirthday, "Vui lòng nhập đủ ngày sinh");
                    } else {
                        if (isValidAge(current)) tvErrorBirthday.setVisibility(View.GONE);
                        else showError(tvErrorBirthday, "Bạn phải từ 18 tuổi trở lên");
                    }
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        // 3. Kiểm tra Email
        etEmail.addTextChangedListener(new SimpleTextWatcher(s -> {
            String email = s.toString().trim();
            if (email.isEmpty()) {
                showError(tvErrorEmail, "Email không được để trống");
            } else if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.com$")) {
                showError(tvErrorEmail, "Email phải đúng định dạng");
            } else {
                tvErrorEmail.setVisibility(View.GONE);
            }
        }));

        // 4. Kiểm tra Mật khẩu
        etPassword.addTextChangedListener(new SimpleTextWatcher(s -> {
            if (s.length() == 0) {
                showError(tvErrorPassword, "Mật khẩu không được để trống");
            } else if (s.length() < 8) {
                showError(tvErrorPassword, "Mật khẩu phải ít nhất 8 ký tự");
            } else {
                tvErrorPassword.setVisibility(View.GONE);
            }
        }));

        // 5. Giới tính
        rgGender.setOnCheckedChangeListener((group, checkedId) -> tvErrorGender.setVisibility(View.GONE));
    }

    private void showError(TextView tv, String message) {
        tv.setText("⚠ " + message);
        tv.setVisibility(View.VISIBLE);
    }

    private void checkFieldsOnSubmit() {
        if (etFullName.getText().toString().isEmpty()) showError(tvErrorName, "Họ tên không được để trống");
        if (etBirthday.getText().toString().isEmpty()) showError(tvErrorBirthday, "Ngày sinh không được để trống");
        if (etEmail.getText().toString().isEmpty()) showError(tvErrorEmail, "Email không được để trống");
        if (etPassword.getText().toString().isEmpty()) showError(tvErrorPassword, "Mật khẩu không được để trống");
        if (rgGender.getCheckedRadioButtonId() == -1) showError(tvErrorGender, "Vui lòng chọn giới tính");
    }

    private boolean validateAllFields() {
        // Đảm bảo giới tính đã chọn
        return tvErrorName.getVisibility() == View.GONE &&
                tvErrorBirthday.getVisibility() == View.GONE &&
                tvErrorEmail.getVisibility() == View.GONE &&
                tvErrorPassword.getVisibility() == View.GONE &&
                tvErrorGender.getVisibility() == View.GONE &&
                rgGender.getCheckedRadioButtonId() != -1 &&
                !etEmail.getText().toString().isEmpty();
    }

    private boolean isValidAge(String date) {
        try {
            String[] p = date.split("/");
            Calendar dob = Calendar.getInstance();
            dob.set(Integer.parseInt(p[2]), Integer.parseInt(p[1]) - 1, Integer.parseInt(p[0]));
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) age--;
            return age >= 18 && !dob.after(today);
        } catch (Exception e) { return false; }
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etBirthday = findViewById(R.id.etBirthday);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvErrorName = findViewById(R.id.tvErrorName);
        tvErrorBirthday = findViewById(R.id.tvErrorBirthday);
        tvErrorEmail = findViewById(R.id.tvErrorEmail);
        tvErrorPassword = findViewById(R.id.tvErrorPassword);
        tvErrorGender = findViewById(R.id.tvErrorGender);
        rgGender = findViewById(R.id.rgGender);
        btnRegister = findViewById(R.id.btnRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
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