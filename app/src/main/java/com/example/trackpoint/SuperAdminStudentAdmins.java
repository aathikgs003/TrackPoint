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

public class SuperAdminStudentAdmins extends AppCompatActivity {

    ImageView btnBack;
    TextView txtHeaderTitle;
    EditText searchStudentAdmin;
    LinearLayout filterRow, studentAdminListContainer;
    Spinner spFilterDept, spFilterStaff;
    Button btnAssignStudentAdmin;

    int purple = Color.parseColor("#4935B8");
    String lockedDept = null;
    String lockedStaff = null;

    static class SuperStudentAdminModel {
        String name, id, department, year, assignedStaffAdmin, phone, email, studentsCount;

        SuperStudentAdminModel(String name, String id, String department, String year,
                               String assignedStaffAdmin, String phone, String email, String studentsCount) {
            this.name = name;
            this.id = id;
            this.department = department;
            this.year = year;
            this.assignedStaffAdmin = assignedStaffAdmin;
            this.phone = phone;
            this.email = email;
            this.studentsCount = studentsCount;
        }
    }

    List<SuperStudentAdminModel> list = new ArrayList<>();

    private ActivityResultLauncher<Intent> addStudentAdminLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(purple);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_super_admin_student_admins);

        btnBack = findViewById(R.id.btnBack);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        searchStudentAdmin = findViewById(R.id.searchStudentAdmin);
        filterRow = findViewById(R.id.filterRow);
        spFilterDept = findViewById(R.id.spFilterDept);
        spFilterStaff = findViewById(R.id.spFilterStaff);
        studentAdminListContainer = findViewById(R.id.studentAdminListContainer);
        btnAssignStudentAdmin = findViewById(R.id.btnAssignStudentAdmin);

        addStudentAdminLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String name = data.getStringExtra("adminName");
                        String id = data.getStringExtra("adminId");
                        String dept = data.getStringExtra("department");
                        String year = data.getStringExtra("year");
                        String staff = data.getStringExtra("assignedStaffAdmin");
                        String phone = data.getStringExtra("phone");
                        String email = data.getStringExtra("email");
                        if (phone == null || phone.isEmpty()) phone = "98765 00000";
                        if (email == null || email.isEmpty()) email = "student@college.edu";
                        list.add(new SuperStudentAdminModel(name, id, dept, year, staff, phone, email, "0"));
                        renderList();
                    }
                }
        );

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

        list.add(new SuperStudentAdminModel("Arun Kumar", "SADM001", "CSE", "III Year", "Dr. Raja", "98765 43201", "arun.admin@college.edu", "24"));
        list.add(new SuperStudentAdminModel("Priya M", "SADM005", "CSE", "II Year", "Prof. Ramesh K", "98765 43205", "priya.admin@college.edu", "22"));
        list.add(new SuperStudentAdminModel("Divya S", "SADM002", "AI & DS", "III Year", "Prof. Meena R", "98765 43202", "divya.admin@college.edu", "28"));
        list.add(new SuperStudentAdminModel("Karthik R", "SADM003", "ECE", "IV Year", "Prof. Vijay S", "98765 43203", "karthik.admin@college.edu", "22"));
        list.add(new SuperStudentAdminModel("Nithya P", "SADM004", "IT", "II Year", "Prof. Anitha P", "98765 43204", "nithya.admin@college.edu", "22"));

        renderList();

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
            addStudentAdminLauncher.launch(intent);
        });
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void renderList() {
        studentAdminListContainer.removeAllViews();
        String deptFilter = (lockedDept != null) ? lockedDept : spFilterDept.getSelectedItem().toString();
        String staffFilter = (lockedStaff != null) ? lockedStaff : spFilterStaff.getSelectedItem().toString();
        String query = searchStudentAdmin.getText().toString().toLowerCase().trim();

        for (SuperStudentAdminModel admin : list) {
            if (!deptFilter.equals("All Depts") && !admin.department.equalsIgnoreCase(deptFilter)) continue;
            if (!staffFilter.equals("All Staff Admins") && !admin.assignedStaffAdmin.equalsIgnoreCase(staffFilter)) continue;
            if (!query.isEmpty() && !admin.name.toLowerCase().contains(query) && !admin.id.toLowerCase().contains(query)) continue;

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
            tvName.setText(admin.name + " (" + admin.id + ")");
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextSize(15);
            tvName.setTextColor(Color.parseColor("#18233F"));

            TextView tvDept = new TextView(this);
            tvDept.setText(admin.department + " • " + admin.year + "  |  Students: " + admin.studentsCount);
            tvDept.setTextSize(12);
            tvDept.setTextColor(Color.parseColor("#68738A"));

            TextView tvStaff = new TextView(this);
            tvStaff.setText("Staff Admin: " + admin.assignedStaffAdmin);
            tvStaff.setTextSize(12);
            tvStaff.setTextColor(purple);

            info.addView(tvName);
            info.addView(tvDept);
            info.addView(tvStaff);

            TextView arrow = new TextView(this);
            arrow.setText("›");
            arrow.setTextSize(27);
            arrow.setTextColor(purple);

            card.addView(avatar);
            card.addView(info);
            card.addView(arrow);

            // Reuse existing StudentAdminDetails screen
            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, StudentAdminDetails.class);
                intent.putExtra("adminName", admin.name);
                intent.putExtra("adminId", admin.id);
                intent.putExtra("department", admin.department);
                intent.putExtra("year", admin.year);
                intent.putExtra("phone", admin.phone);
                intent.putExtra("email", admin.email);
                intent.putExtra("studentsAssigned", admin.studentsCount);
                intent.putExtra("joinedDate", "Jan 10, 2025");
                intent.putExtra("status", "Active");
                startActivity(intent);
            });

            studentAdminListContainer.addView(card);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}