package com.example.weconnect.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weconnect.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView ivBackChangePassword;
    private TextInputLayout tilCurrentPassword;
    private TextInputLayout tilNewPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText etCurrentPassword;
    private TextInputEditText etNewPassword;
    private TextInputEditText etConfirmPassword;
    private MaterialButton btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        ivBackChangePassword = findViewById(R.id.ivBackChangePassword);
        tilCurrentPassword = findViewById(R.id.tilCurrentPassword);
        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
    }

    private void setupClickListeners() {
        ivBackChangePassword.setOnClickListener(v -> finish());

        btnChangePassword.setOnClickListener(v -> {
            if (validateForm()) {
                // Fake password change
                Toast.makeText(this, "Doi mat khau thanh cong!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        String currentPassword = etCurrentPassword.getText() != null
                ? etCurrentPassword.getText().toString().trim() : "";
        String newPassword = etNewPassword.getText() != null
                ? etNewPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null
                ? etConfirmPassword.getText().toString().trim() : "";

        // Clear previous errors
        tilCurrentPassword.setError(null);
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);

        if (currentPassword.isEmpty()) {
            tilCurrentPassword.setError("Vui long nhap mat khau hien tai");
            isValid = false;
        }

        if (newPassword.isEmpty()) {
            tilNewPassword.setError("Vui long nhap mat khau moi");
            isValid = false;
        } else if (newPassword.length() < 6) {
            tilNewPassword.setError("Mat khau phai co it nhat 6 ky tu");
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Vui long xac nhan mat khau moi");
            isValid = false;
        } else if (!confirmPassword.equals(newPassword)) {
            tilConfirmPassword.setError("Mat khau xac nhan khong khop");
            isValid = false;
        }

        if (newPassword.equals(currentPassword) && !newPassword.isEmpty()) {
            tilNewPassword.setError("Mat khau moi phai khac mat khau cu");
            isValid = false;
        }

        return isValid;
    }
}
