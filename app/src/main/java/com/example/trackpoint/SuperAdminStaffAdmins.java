package com.example.trackpoint;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminStaffAdmins extends AppCompatActivity {

    ImageView btnBack;
    TextView txtHeaderTitle;
    EditText searchStaffAdmin;
    Spinner spFilterStaffDept;
    LinearLayout staffAdminListContainer;
    Button btnAssignStaffAdmin;

    int purple = Color.parseColor("#4935B8");
    String lockedDepartment = null;

    static class StaffAdminModel {
        String name, staffId, department, email, phone;
        int studentAdminCount;

        StaffAdminModel(String name, String staffId, String department, String email, String phone, int studentAdminCount) {
            this.name = name;
            this.staffId = staffId;
            this.department = department;
            this.email = email;
            this.phone = phone;
            this.studentAdminCount = studentAdminCount;
        }
    }

    List<StaffAdminModel> staffAdmins = new ArrayList<>();

    private ActivityResultLauncher<Intent> addStaffLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(purple);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_super_admin_staff_admins);

        btnBack = findViewById(R.id.btnBack);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        searchStaffAdmin = findViewById(R.id.searchStaffAdmin);
        spFilterStaffDept = findViewById(R.id.spFilterStaffDept);
        staffAdminListContainer = findViewById(R.id.staffAdminListContainer);
        btnAssignStaffAdmin = findViewById(R.id.btnAssignStaffAdmin);

        addStaffLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String name = data.getStringExtra("staffName");
                        String id = data.getStringExtra("staffId");
                        String dept = data.getStringExtra("department");
                        String email = data.getStringExtra("email");
                        String phone = data.getStringExtra("phone");
                        if (phone == null || phone.isEmpty()) phone = "98765 00000";
                        staffAdmins.add(new StaffAdminModel(name, id, dept, email, phone, 0));
                        renderList();
                    }
                }
        );

        btnBack.setOnClickListener(v -> finish());

        lockedDepartment = getIntent().getStringExtra("filterDepartment");
        if (lockedDepartment != null && !lockedDepartment.isEmpty()) {
            txtHeaderTitle.setText(lockedDepartment + " Staff Admins");
            spFilterStaffDept.setVisibility(View.GONE);
        }

        String[] depts = {"All Departments", "CSE", "AI & DS", "ECE", "IT", "MECH"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, depts);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spFilterStaffDept.setAdapter(adapter);

        staffAdmins.add(new StaffAdminModel("Prof. Ramesh K", "STF001", "CSE", "ramesh@college.edu", "98765 11101", 2));
        staffAdmins.add(new StaffAdminModel("Dr. Raja", "STF002", "CSE", "suruthika@gmail.com", "98765 11102", 2));
        staffAdmins.add(new StaffAdminModel("Prof. Meena R", "STF003", "AI & DS", "meena@college.edu", "98765 11103", 3));
        staffAdmins.add(new StaffAdminModel("Prof. Vijay S", "STF004", "ECE", "vijay@college.edu", "98765 11104", 4));
        staffAdmins.add(new StaffAdminModel("Prof. Anitha P", "STF005", "IT", "anitha@college.edu", "98765 11105", 3));
        staffAdmins.add(new StaffAdminModel("Prof. Karthikeyan M", "STF006", "MECH", "karthik.m@college.edu", "98765 11106", 2));

        renderList();

        spFilterStaffDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renderList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        searchStaffAdmin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                renderList();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAssignStaffAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(this, SuperAddStaffAdmin.class);
            if (lockedDepartment != null && !lockedDepartment.isEmpty()) {
                intent.putExtra("preselectedDept", lockedDepartment);
            }
            addStaffLauncher.launch(intent);
        });
    }

    private void renderList() {
        staffAdminListContainer.removeAllViews();
        String deptFilter = (lockedDepartment != null) ? lockedDepartment : spFilterStaffDept.getSelectedItem().toString();
        String query = searchStaffAdmin.getText().toString().toLowerCase().trim();

        for (StaffAdminModel staff : staffAdmins) {
            if (!deptFilter.equals("All Departments") && !staff.department.equalsIgnoreCase(deptFilter)) {
                continue;
            }
            if (!query.isEmpty() && !staff.name.toLowerCase().contains(query) && !staff.staffId.toLowerCase().contains(query)) {
                continue;
            }

            LinearLayout card = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, dpToPx(10));
            card.setLayoutParams(params);
            card.setOrientation(LinearLayout.HORIZONTAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));
            card.setBackgroundResource(R.drawable.action_card);
            card.setElevation(dpToPx(2));

            ImageView avatar = new ImageView(this);
            avatar.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(44), dpToPx(44)));
            avatar.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
            avatar.setImageResource(R.drawable.profile_symbol);
            avatar.setColorFilter(purple);
            avatar.setBackgroundResource(R.drawable.card_purple);

            LinearLayout info = new LinearLayout(this);
            info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            info.setOrientation(LinearLayout.VERTICAL);
            info.setPadding(dpToPx(12), 0, 0, 0);

            TextView tvName = new TextView(this);
            tvName.setText(staff.name);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextSize(16);
            tvName.setTextColor(Color.parseColor("#18233F"));

            TextView tvMeta = new TextView(this);
            tvMeta.setText(staff.staffId + "  •  Dept: " + staff.department);
            tvMeta.setTextSize(13);
            tvMeta.setTextColor(Color.parseColor("#68738A"));
            tvMeta.setTextSize(13);
            tvMeta.setTextColor(Color.parseColor("#555555"));

            TextView tvSub = new TextView(this);
            tvSub.setText("Student Admins under them: " + staff.studentAdminCount);
            tvSub.setTextSize(12);
            tvSub.setTextColor(purple);

            info.addView(tvName);
            info.addView(tvMeta);
            info.addView(tvSub);

            TextView arrow = new TextView(this);
            arrow.setText("›");
            arrow.setTextSize(27);
            arrow.setTextColor(purple);

            card.addView(avatar);
            card.addView(info);
            card.addView(arrow);

            // Tap Staff Admin -> View Student Admins under this Staff Admin
            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, SuperAdminStaffDetails.class);
                intent.putExtra("staffName", staff.name);
                intent.putExtra("staffId", staff.staffId);
                intent.putExtra("department", staff.department);
                intent.putExtra("email", staff.email);
                intent.putExtra("phone", staff.phone);
                intent.putExtra("studentAdminCount", staff.studentAdminCount);
                startActivity(intent);
            });

            staffAdminListContainer.addView(card);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}