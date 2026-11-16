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

public class SuperAdminStudentAdmins extends AppCompatActivity {

    ImageView btnBack;
    TextView txtHeaderTitle;
    EditText searchStudentAdmin;
    LinearLayout filterRow, studentAdminListContainer;
    Spinner spFilterDept, spFilterStaff;
    Button btnAssignStudentAdmin;

    StudentRepository repository;
    int purple = Color.parseColor("#4935B8");
    String lockedDept = null;
    String lockedStaff = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(purple);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_super_admin_student_admins);

        repository = new StudentRepository(this);

        btnBack = findViewById(R.id.btnBack);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        searchStudentAdmin = findViewById(R.id.searchStudentAdmin);
        filterRow = findViewById(R.id.filterRow);
        spFilterDept = findViewById(R.id.spFilterDept);
        spFilterStaff = findViewById(R.id.spFilterStaff);
        studentAdminListContainer = findViewById(R.id.studentAdminListContainer);
        btnAssignStudentAdmin = findViewById(R.id.btnAssignStudentAdmin);

        btnBack.setOnClickListener(v -> finish());

        lockedDept = getIntent().getStringExtra("filterDepartment");
        lockedStaff = getIntent().getStringExtra("filterStaffAdmin");

        if (lockedStaff != null && !lockedStaff.isEmpty()) {
            txtHeaderTitle.setText("Admins under " + lockedStaff);
            filterRow.setVisibility(View.GONE);
        } else if (lockedDept != null && !lockedDept.isEmpty()) {
            txtHeaderTitle.setText(lockedDept + " Student Admins");
            spFilterDept.setVisibility(View.GONE);
        }

        setupSpinner(spFilterDept, new String[]{"All Depts", "CSE", "AI & DS", "ECE", "IT", "MECH"});
        setupSpinner(spFilterStaff, new String[]{"All Staff Admins", "Prof. Ramesh K", "Dr. Raja", "Prof. Meena R", "Prof. Vijay S", "Prof. Anitha P"});

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renderList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spFilterDept.setOnItemSelectedListener(listener);
        spFilterStaff.setOnItemSelectedListener(listener);

        searchStudentAdmin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                renderList();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAssignStudentAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(this, SuperAddStudentAdmin.class);
            if (lockedDept != null && !lockedDept.isEmpty()) {
                intent.putExtra("preselectedDept", lockedDept);
            }
            if (lockedStaff != null && !lockedStaff.isEmpty()) {
                intent.putExtra("preselectedStaff", lockedStaff);
            }
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderList();
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void renderList() {
        if (studentAdminListContainer == null) return;
        studentAdminListContainer.removeAllViews();

        String deptFilter = (lockedDept != null) ? lockedDept : spFilterDept.getSelectedItem().toString();
        String query = searchStudentAdmin.getText().toString().toLowerCase().trim();

        ArrayList<StudentAdminModel> dbList = repository.getAllStudentAdmins();

        if (dbList.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No Student Admins found in database");
            emptyView.setPadding(16, 32, 16, 32);
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setTextColor(Color.parseColor("#68738A"));
            studentAdminListContainer.addView(emptyView);
            return;
        }

        for (StudentAdminModel admin : dbList) {
            if (!deptFilter.equals("All Depts") && !admin.getDepartment().equalsIgnoreCase(deptFilter)) continue;
            if (!query.isEmpty() && !admin.getName().toLowerCase().contains(query) && !admin.getAdminId().toLowerCase().contains(query)) continue;

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
            tvName.setText(admin.getName() + " (" + admin.getAdminId() + ")");
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextSize(15);
            tvName.setTextColor(Color.parseColor("#18233F"));

            TextView tvDept = new TextView(this);
            tvDept.setText(admin.getDepartment() + " • " + admin.getYear() + "  |  Students: " + admin.getStudentsAssignedCount());
            tvDept.setTextSize(12);
            tvDept.setTextColor(Color.parseColor("#68738A"));

            TextView tvStaff = new TextView(this);
            tvStaff.setText("Student Administrator");
            tvStaff.setTextSize(12);
            tvStaff.setTextColor(purple);

            info.addView(tvName);
            info.addView(tvDept);
            info.addView(tvStaff);

            TextView arrow = new TextView(this);
            arrow.setText("›");
            arrow.setTextSize(26);
            arrow.setTextColor(Color.parseColor("#68738A"));

            card.addView(avatar);
            card.addView(info);
            card.addView(arrow);

            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, StudentAdminDetails.class);
                intent.putExtra("adminName", admin.getName());
                intent.putExtra("adminId", admin.getAdminId());
                intent.putExtra("department", admin.getDepartment());
                intent.putExtra("year", admin.getYear());
                intent.putExtra("phone", admin.getPhone());
                intent.putExtra("email", admin.getEmail());
                intent.putExtra("studentsAssigned", String.valueOf(admin.getStudentsAssignedCount()));
                startActivity(intent);
            });

            studentAdminListContainer.addView(card);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
