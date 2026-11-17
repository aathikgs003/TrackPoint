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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.util.ArrayList;

public class SuperAdminStaffAdmins extends AppCompatActivity {

    ImageView btnBack;
    TextView txtHeaderTitle;
    EditText searchStaffAdmin;
    Spinner spFilterStaffDept;
    LinearLayout staffAdminListContainer;
    Button btnAssignStaffAdmin;

    StudentRepository repository;
    int purple = Color.parseColor("#4935B8");
    String lockedDepartment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(purple);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_super_admin_staff_admins);

        repository = new StudentRepository(this);

        btnBack = findViewById(R.id.btnBack);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        searchStaffAdmin = findViewById(R.id.searchStaffAdmin);
        spFilterStaffDept = findViewById(R.id.spFilterStaffDept);
        staffAdminListContainer = findViewById(R.id.staffAdminListContainer);
        btnAssignStaffAdmin = findViewById(R.id.btnAssignStaffAdmin);

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
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderList();
    }

    private void renderList() {
        if (staffAdminListContainer == null) return;
        staffAdminListContainer.removeAllViews();

        String deptFilter = (lockedDepartment != null) ? lockedDepartment : spFilterStaffDept.getSelectedItem().toString();
        String query = searchStaffAdmin.getText().toString().toLowerCase().trim();

        ArrayList<StaffAdminModel> dbList = repository.getAllStaffAdmins();

        if (dbList.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No Staff Admins found in database");
            emptyView.setPadding(16, 32, 16, 32);
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setTextColor(Color.parseColor("#68738A"));
            staffAdminListContainer.addView(emptyView);
            return;
        }

        for (StaffAdminModel staff : dbList) {
            if (!deptFilter.equals("All Departments") && !staff.getDepartment().toLowerCase().contains(deptFilter.toLowerCase())) {
                continue;
            }
            if (!query.isEmpty() && !staff.getName().toLowerCase().contains(query) && !staff.getStaffId().toLowerCase().contains(query)) {
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
            tvName.setText(staff.getName());
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextSize(16);
            tvName.setTextColor(Color.parseColor("#18233F"));

            int studentAdminCount = repository.getStudentAdminsCountForStaff(staff.getName());

            TextView tvMeta = new TextView(this);
            tvMeta.setText(staff.getStaffId() + "  •  Dept: " + staff.getDepartment() + "  •  Student Admins: " + studentAdminCount);
            tvMeta.setTextSize(13);
            tvMeta.setTextColor(Color.parseColor("#68738A"));

            TextView tvSub = new TextView(this);
            tvSub.setText("Academic Staff Admin");
            tvSub.setTextSize(12);
            tvSub.setTextColor(purple);

            info.addView(tvName);
            info.addView(tvMeta);
            info.addView(tvSub);

            TextView arrow = new TextView(this);
            arrow.setText("›");
            arrow.setTextSize(26);
            arrow.setTextColor(Color.parseColor("#68738A"));

            card.addView(avatar);
            card.addView(info);
            card.addView(arrow);

            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, SuperAdminStaffDetails.class);
                intent.putExtra("staffName", staff.getName());
                intent.putExtra("staffId", staff.getStaffId());
                intent.putExtra("department", staff.getDepartment());
                intent.putExtra("email", staff.getEmail());
                intent.putExtra("phone", staff.getPhone());
                intent.putExtra("studentAdminCount", studentAdminCount);
                startActivity(intent);
            });

            staffAdminListContainer.addView(card);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
