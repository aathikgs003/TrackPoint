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

    private ImageView btnBack;
    private TextView txtHeaderTitle;
    private EditText searchStudentAdmin;
    private LinearLayout filterRow, studentAdminListContainer;
    private Spinner spFilterDept, spFilterStaff;
    private Button btnAssignStudentAdmin;

    private StudentRepository repository;
    private int purple = Color.parseColor("#4935B8");
    private String lockedDept = null;
    private String lockedStaff = null;
    private boolean isUpdatingStaffSpinner = false;

    private static final String[] DEPARTMENTS = {"All Depts", "CSE", "AI & DS", "ECE", "IT", "MECH"};

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

        setupSpinner(spFilterDept, DEPARTMENTS);
        updateStaffFilterSpinner("All Depts", lockedStaff);

        spFilterDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdatingStaffSpinner) return;
                String selectedDept = (lockedDept != null && !lockedDept.isEmpty()) ? lockedDept : DEPARTMENTS[position];
                updateStaffFilterSpinner(selectedDept, null);
                renderList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spFilterStaff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdatingStaffSpinner) return;
                renderList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

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
            } else if (spFilterDept.getSelectedItemPosition() > 0) {
                intent.putExtra("preselectedDept", spFilterDept.getSelectedItem().toString());
            }
            if (lockedStaff != null && !lockedStaff.isEmpty()) {
                intent.putExtra("preselectedStaff", lockedStaff);
            } else if (spFilterStaff.getSelectedItemPosition() > 0) {
                intent.putExtra("preselectedStaff", spFilterStaff.getSelectedItem().toString());
            }
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentDept = (lockedDept != null && !lockedDept.isEmpty()) ? lockedDept :
                (spFilterDept.getSelectedItem() != null ? spFilterDept.getSelectedItem().toString() : "All Depts");
        String currentStaff = (lockedStaff != null && !lockedStaff.isEmpty()) ? lockedStaff :
                (spFilterStaff.getSelectedItem() != null ? spFilterStaff.getSelectedItem().toString() : null);

        updateStaffFilterSpinner(currentDept, currentStaff);
        renderList();
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void updateStaffFilterSpinner(String departmentFilter, String preselectedStaff) {
        isUpdatingStaffSpinner = true;
        ArrayList<StaffAdminModel> allStaff = repository.getAllStaffAdmins();
        ArrayList<String> staffNames = new ArrayList<>();
        staffNames.add("All Staff Admins");

        for (StaffAdminModel sa : allStaff) {
            if (departmentFilter == null || departmentFilter.equals("All Depts")
                    || sa.getDepartment().equalsIgnoreCase(departmentFilter)
                    || sa.getDepartment().toLowerCase().contains(departmentFilter.toLowerCase())) {
                staffNames.add(sa.getName());
            }
        }

        ArrayAdapter<String> staffAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, staffNames.toArray(new String[0]));
        staffAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spFilterStaff.setAdapter(staffAdapter);

        int selectionIndex = 0;
        if (preselectedStaff != null && !preselectedStaff.isEmpty()) {
            for (int i = 1; i < staffNames.size(); i++) {
                if (staffNames.get(i).equalsIgnoreCase(preselectedStaff)) {
                    selectionIndex = i;
                    break;
                }
            }
        }

        spFilterStaff.setSelection(selectionIndex);
        isUpdatingStaffSpinner = false;
    }

    private void renderList() {
        if (studentAdminListContainer == null) return;
        studentAdminListContainer.removeAllViews();

        String deptFilter = (lockedDept != null && !lockedDept.isEmpty()) ? lockedDept :
                (spFilterDept.getSelectedItem() != null ? spFilterDept.getSelectedItem().toString() : "All Depts");
        String staffFilter = (lockedStaff != null && !lockedStaff.isEmpty()) ? lockedStaff :
                (spFilterStaff.getSelectedItem() != null ? spFilterStaff.getSelectedItem().toString() : "All Staff Admins");
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

        int renderedCount = 0;

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
            renderedCount++;
        }

        if (renderedCount == 0) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No Student Admins found matching filters");
            emptyView.setPadding(16, 32, 16, 32);
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setTextColor(Color.parseColor("#68738A"));
            studentAdminListContainer.addView(emptyView);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
