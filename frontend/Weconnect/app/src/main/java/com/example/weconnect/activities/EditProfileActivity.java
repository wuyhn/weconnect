package com.example.weconnect.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weconnect.R;
import com.example.weconnect.data.FakePostRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView ivBackEditProfile;
    private TextInputEditText etDisplayName;
    private TextInputEditText etBirthDate;
    private TextInputEditText etBio;
    private ChipGroup chipGroupGender;
    private ChipGroup chipGroupInterests;
    private MaterialButton btnSaveProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();
        loadCurrentProfile();
        setupClickListeners();
    }

    private void initViews() {
        ivBackEditProfile = findViewById(R.id.ivBackEditProfile);
        etDisplayName = findViewById(R.id.etDisplayName);
        etBirthDate = findViewById(R.id.etBirthDate);
        etBio = findViewById(R.id.etBio);
        chipGroupGender = findViewById(R.id.chipGroupGender);
        chipGroupInterests = findViewById(R.id.chipGroupInterests);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
    }

    private void loadCurrentProfile() {
        String currentUser = FakePostRepository.getInstance().getCurrentUsername();
        etDisplayName.setText(currentUser);
        etBirthDate.setText("01/01/2000");
        etBio.setText("Xin chao! Toi la " + currentUser);

        // Pre-select gender
        chipGroupGender.check(R.id.chipMale);

        // Pre-select some interests based on current user
        List<String> currentInterests = new ArrayList<>();
        if ("Quynh Nguyen".equalsIgnoreCase(currentUser)) {
            currentInterests.add("Coffee meetup");
            currentInterests.add("Gaming");
            currentInterests.add("Movies");
        } else {
            currentInterests.add("Coffee meetup");
            currentInterests.add("Design and code");
        }

        for (int i = 0; i < chipGroupInterests.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupInterests.getChildAt(i);
            if (currentInterests.contains(chip.getText().toString())) {
                chip.setChecked(true);
            }
        }
    }

    private void setupClickListeners() {
        ivBackEditProfile.setOnClickListener(v -> finish());

        etBirthDate.setOnClickListener(v -> showDatePicker());

        btnSaveProfile.setOnClickListener(v -> {
            String name = etDisplayName.getText() != null ? etDisplayName.getText().toString().trim() : "";
            String birthDate = etBirthDate.getText() != null ? etBirthDate.getText().toString().trim() : "";

            if (name.isEmpty()) {
                etDisplayName.setError("Vui long nhap ten");
                return;
            }

            if (birthDate.isEmpty()) {
                etBirthDate.setError("Vui long chon ngay sinh");
                return;
            }

            // Collect selected interests
            List<String> selectedInterests = new ArrayList<>();
            for (int i = 0; i < chipGroupInterests.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupInterests.getChildAt(i);
                if (chip.isChecked()) {
                    selectedInterests.add(chip.getText().toString());
                }
            }

            // Fake save
            Toast.makeText(this, "Da luu thay doi thanh cong!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 20;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    etBirthDate.setText(date);
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
}
