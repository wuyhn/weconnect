package com.example.weconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.weconnect.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.card.MaterialCardView;

public class CreatePostActivity extends AppCompatActivity {

    private EditText etPostContent;
    private TextView tvUserName;
    private ImageView ivClose, ivAddImage, ivAddLocation, ivTagInterest;
    private MaterialButton btnPost;
    private String selectedTag = "";
    private MaterialCardView cardSelectedTag;
    private TextView tvSelectedTag;
    private ImageView ivParticipants;
    private MaterialCardView cardParticipantLimit;
    private TextView tvParticipantLimit;
    private int participantLimit = 0;
    private MaterialCardView cardSelectedLocation;
    private TextView tvSelectedLocation;
    private String selectedLocation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // 1. Ánh xạ View
        ivClose = findViewById(R.id.ivClose);
        etPostContent = findViewById(R.id.etPostContent);
        tvUserName = findViewById(R.id.tvUserName);
        btnPost = findViewById(R.id.btnPost);
        ivAddImage = findViewById(R.id.ivAddImage);
        ivAddLocation = findViewById(R.id.ivAddLocation);
        ivTagInterest = findViewById(R.id.ivTagInterest);
        cardSelectedTag = findViewById(R.id.cardSelectedTag);
        tvSelectedTag = findViewById(R.id.tvSelectedTag);
        cardSelectedLocation = findViewById(R.id.cardSelectedLocation);
        tvSelectedLocation = findViewById(R.id.tvSelectedLocation);

        // 2. Click cho nút Close - Cực kỳ quan trọng
//        ivClose.setOnClickListener(v -> handleExit());
        ivClose.setOnClickListener(v -> {
            // Log để kiểm tra xem có nhận lệnh không
            android.util.Log.d("QUYNH_DEBUG", "Nút Close đã được bấm!");
            finish();
        });
        // 3. Click nút Đăng
        btnPost.setOnClickListener(v -> {
            String content = etPostContent.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(this, "Hãy nhập gì đó...", Toast.LENGTH_SHORT).show();
            } else {
                Intent result = new Intent();
                result.putExtra("post_content", content);
                result.putExtra("post_username", tvUserName.getText().toString());
                result.putExtra("post_time", "Vừa xong");
                result.putExtra("post_tag", selectedTag);
                result.putExtra("post_max_members", participantLimit);
                result.putExtra("post_location", selectedLocation);
                setResult(RESULT_OK, result);
                finish();
            }
        });

        // 4. Các icon phụ
        ivAddImage.setOnClickListener(v -> Toast.makeText(this, "Thêm ảnh", Toast.LENGTH_SHORT).show());
        ivAddLocation.setOnClickListener(v -> showLocationDialog());
        ivTagInterest.setOnClickListener(v -> showTagDialog());
        ivParticipants = findViewById(R.id.ivParticipants);
        cardParticipantLimit = findViewById(R.id.cardParticipantLimit);
        tvParticipantLimit = findViewById(R.id.tvParticipantLimit);

        ivParticipants.setOnClickListener(v -> showParticipantDialog());

        // 5. Xử lý nút back hệ thống
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleExit();
            }
        });


    }

    private void handleExit() {
        if (!etPostContent.getText().toString().trim().isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Hủy bài viết?")
                    .setMessage("Nội dung sẽ không được lưu.")
                    .setPositiveButton("Hủy bỏ", (d, w) -> finish())
                    .setNegativeButton("Viết tiếp", null)
                    .show();
        } else {
            finish();
        }
    }

    private void showTagDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_tag_interest, null);
        dialog.setContentView(view);

        // 1. Ép hiển thị Full màn hình
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                com.google.android.material.bottomsheet.BottomSheetBehavior behavior =
                        com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheet);
                behavior.setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);
                bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        });

        // 2. Ánh xạ các nhóm Chip
        ChipGroup groupSport = view.findViewById(R.id.groupSport);
        ChipGroup groupStudy = view.findViewById(R.id.groupStudy);
        ChipGroup groupChill = view.findViewById(R.id.groupChill);
        MaterialButton btnOk = view.findViewById(R.id.btnOkInterest);

        ChipGroup[] allGroups = {groupSport, groupStudy, groupChill};

        // 3. Logic: Chỉ được chọn 1 Tag duy nhất trên cả 3 nhóm
        for (ChipGroup currentGroup : allGroups) {
            currentGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (!checkedIds.isEmpty()) {
                    // Nếu nhóm này có cái được chọn, thì bỏ chọn tất cả các nhóm còn lại
                    for (ChipGroup otherGroup : allGroups) {
                        if (otherGroup != group) {
                            otherGroup.clearCheck();
                        }
                    }
                }
            });
        }

        // 4. Bấm OK để xác nhận
        btnOk.setOnClickListener(v -> {
            String tagChosen = "";
            for (ChipGroup group : allGroups) {
                int checkedId = group.getCheckedChipId();
                if (checkedId != View.NO_ID) {
                    Chip chip = group.findViewById(checkedId);
                    tagChosen = chip.getText().toString();
                    break;
                }
            }

//            if (!tagChosen.isEmpty()) {
//                selectedTag = tagChosen; // Lưu vào biến toàn cục của Activity
//                Toast.makeText(this, "Đã chọn: " + selectedTag, Toast.LENGTH_SHORT).show();
//                dialog.dismiss();
//            } else {
//                Toast.makeText(this, "Bạn chưa chọn sở thích nào!", Toast.LENGTH_SHORT).show();
//            }

            if (!tagChosen.isEmpty()) {
                selectedTag = tagChosen;

                tvSelectedTag.setText(selectedTag);
                cardSelectedTag.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Đã chọn: " + selectedTag, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Bạn chưa chọn sở thích nào!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showParticipantDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_participant_limit, null);
        dialog.setContentView(view);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                com.google.android.material.bottomsheet.BottomSheetBehavior behavior =
                        com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheet);
                behavior.setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);
                bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        });

        ImageView ivCloseParticipant = view.findViewById(R.id.ivCloseParticipant);
        com.google.android.material.textfield.TextInputEditText etParticipantLimit =
                view.findViewById(R.id.etParticipantLimit);
        MaterialButton btnOkParticipant = view.findViewById(R.id.btnOkParticipant);

        Chip chipParticipant5 = view.findViewById(R.id.chipParticipant5);
        Chip chipParticipant10 = view.findViewById(R.id.chipParticipant10);
        Chip chipParticipant20 = view.findViewById(R.id.chipParticipant20);
        Chip chipParticipant50 = view.findViewById(R.id.chipParticipant50);

        ivCloseParticipant.setOnClickListener(v -> dialog.dismiss());

        if (participantLimit > 0) {
            etParticipantLimit.setText(String.valueOf(participantLimit));
        }

        chipParticipant5.setOnClickListener(v -> etParticipantLimit.setText("5"));
        chipParticipant10.setOnClickListener(v -> etParticipantLimit.setText("10"));
        chipParticipant20.setOnClickListener(v -> etParticipantLimit.setText("20"));
        chipParticipant50.setOnClickListener(v -> etParticipantLimit.setText("50"));

        btnOkParticipant.setOnClickListener(v -> {
            String input = etParticipantLimit.getText() != null
                    ? etParticipantLimit.getText().toString().trim()
                    : "";

            if (input.length() == 0) {
                Toast.makeText(this, "Bạn chưa nhập giới hạn người tham gia!", Toast.LENGTH_SHORT).show();
                return;
            }

            participantLimit = Integer.parseInt(input);
            tvParticipantLimit.setText("👥 Giới hạn: " + participantLimit + " người");
            cardParticipantLimit.setVisibility(View.VISIBLE);

            dialog.dismiss();
        });

        dialog.show();
    }

    private void showLocationDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_location_picker, null);
        dialog.setContentView(view);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                com.google.android.material.bottomsheet.BottomSheetBehavior behavior =
                        com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheet);
                behavior.setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);
                bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        });

        ImageView ivCloseLocation = view.findViewById(R.id.ivCloseLocation);
        com.google.android.material.textfield.MaterialAutoCompleteTextView actWard = view.findViewById(R.id.actWard);
        com.google.android.material.textfield.MaterialAutoCompleteTextView actDistrict = view.findViewById(R.id.actDistrict);
        com.google.android.material.textfield.MaterialAutoCompleteTextView actCity = view.findViewById(R.id.actCity);
        TextView tvLocationPreview = view.findViewById(R.id.tvLocationPreview);
        MaterialButton btnOkLocation = view.findViewById(R.id.btnOkLocation);

        Chip chipHaDong = view.findViewById(R.id.chipHaDong);
        Chip chipCauGiay = view.findViewById(R.id.chipCauGiay);
        Chip chipThuDuc = view.findViewById(R.id.chipThuDuc);
        Chip chipHaiChau = view.findViewById(R.id.chipHaiChau);

        ivCloseLocation.setOnClickListener(v -> dialog.dismiss());

        String[] wards = {"Phường Phú Lương", "Phường Văn Quán", "Phường Mộ Lao", "Phường Yên Nghĩa"};
        String[] districts = {"Hà Đông", "Cầu Giấy", "Thủ Đức", "Hải Châu"};
        String[] cities = {"Hà Nội", "TP.HCM", "Đà Nẵng"};

        android.widget.ArrayAdapter<String> wardAdapter =
                new android.widget.ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, wards);
        android.widget.ArrayAdapter<String> districtAdapter =
                new android.widget.ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, districts);
        android.widget.ArrayAdapter<String> cityAdapter =
                new android.widget.ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);

        actWard.setAdapter(wardAdapter);
        actDistrict.setAdapter(districtAdapter);
        actCity.setAdapter(cityAdapter);

        Runnable updatePreview = () -> {
            String ward = actWard.getText() != null ? actWard.getText().toString().trim() : "";
            String district = actDistrict.getText() != null ? actDistrict.getText().toString().trim() : "";
            String city = actCity.getText() != null ? actCity.getText().toString().trim() : "";

            StringBuilder builder = new StringBuilder("📍 ");
            boolean hasValue = false;

            if (ward.length() > 0) {
                builder.append(ward);
                hasValue = true;
            }
            if (district.length() > 0) {
                if (hasValue) builder.append(", ");
                builder.append(district);
                hasValue = true;
            }
            if (city.length() > 0) {
                if (hasValue) builder.append(", ");
                builder.append(city);
            }

            if (!hasValue && city.length() == 0) {
                tvLocationPreview.setText("📍 Chưa chọn địa điểm");
            } else {
                tvLocationPreview.setText(builder.toString());
            }
        };

        actWard.setOnItemClickListener((parent, v, position, id) -> updatePreview.run());
        actDistrict.setOnItemClickListener((parent, v, position, id) -> updatePreview.run());
        actCity.setOnItemClickListener((parent, v, position, id) -> updatePreview.run());

        chipHaDong.setOnClickListener(v -> {
            actWard.setText("Phường Phú Lương", false);
            actDistrict.setText("Hà Đông", false);
            actCity.setText("Hà Nội", false);
            updatePreview.run();
        });

        chipCauGiay.setOnClickListener(v -> {
            actWard.setText("Phường Văn Quán", false);
            actDistrict.setText("Cầu Giấy", false);
            actCity.setText("Hà Nội", false);
            updatePreview.run();
        });

        chipThuDuc.setOnClickListener(v -> {
            actWard.setText("Phường Mộ Lao", false);
            actDistrict.setText("Thủ Đức", false);
            actCity.setText("TP.HCM", false);
            updatePreview.run();
        });

        chipHaiChau.setOnClickListener(v -> {
            actWard.setText("Phường Yên Nghĩa", false);
            actDistrict.setText("Hải Châu", false);
            actCity.setText("Đà Nẵng", false);
            updatePreview.run();
        });

        if (selectedLocation != null && selectedLocation.length() > 0) {
            tvLocationPreview.setText("📍 " + selectedLocation);
        }

        btnOkLocation.setOnClickListener(v -> {
            String ward = actWard.getText() != null ? actWard.getText().toString().trim() : "";
            String district = actDistrict.getText() != null ? actDistrict.getText().toString().trim() : "";
            String city = actCity.getText() != null ? actCity.getText().toString().trim() : "";

            if (ward.length() == 0 && district.length() == 0 && city.length() == 0) {
                Toast.makeText(this, "Bạn chưa chọn địa điểm!", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder builder = new StringBuilder();
            boolean hasValue = false;

            if (ward.length() > 0) {
                builder.append(ward);
                hasValue = true;
            }
            if (district.length() > 0) {
                if (hasValue) builder.append(", ");
                builder.append(district);
                hasValue = true;
            }
            if (city.length() > 0) {
                if (hasValue) builder.append(", ");
                builder.append(city);
            }

            selectedLocation = builder.toString();
            tvSelectedLocation.setText("📍 " + selectedLocation);
            cardSelectedLocation.setVisibility(View.VISIBLE);

            dialog.dismiss();
        });

        dialog.show();
    }

}