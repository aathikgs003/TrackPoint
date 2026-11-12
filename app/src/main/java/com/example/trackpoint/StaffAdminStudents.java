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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.util.ArrayList;
import java.util.List;

public class StaffAdminStudents extends AppCompatActivity {

    ImageView btnBack;
    TextView txtHeaderTitle;
    Spinner spFilterDepartment, spFilterYear;
    EditText searchStudent;
    LinearLayout studentListContainer;
    Button btnAddStudent;

    // Bottom Navigation
    LinearLayout navHome, navAdmins, navStudents, navReports;
    ImageView iconHome, iconAdmins, iconStudents, iconReports;
    TextView textHome, textAdmins, textStudents, textReports;

    int purple = Color.parseColor("#4825B8");
    int grey = Color.parseColor("#888888");

    String filterByAdminName = null;

    static class StudentModel {
        String name, regId, department, year, section, assignedAdmin, phone, email, attendance, progress, tasks;

        StudentModel(String name, String regId, String department, String year, String section,
                     String assignedAdmin, String phone, String email,
                     String attendance, String progress, String tasks) {
            this.name = name;
            this.regId = regId;
            this.department = department;
            this.year = year;
            this.section = section;
            this.assignedAdmin = assignedAdmin;
            this.phone = phone;
            this.email = email;
            this.attendance = attendance;
            this.progress = progress;
            this.tasks = tasks;
        }
    }

    List<StudentModel> allStudents = new ArrayList<>();

    ActivityResultLauncher<Intent> addStudentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    allStudents.add(new StudentModel(
                            data.getStringExtra("studentName"),
                            data.getStringExtra("studentId"),
                            data.getStringExtra("department"),
                            data.getStringExtra("year"),
                            data.getStringExtra("section"),
                            data.getStringExtra("assignedAdmin"),
                            data.getStringExtra("phone"),
                            data.getStringExtra("email"),
                            "100%", "0%", "0"
                    ));
                    applyFilters();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4825B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_staff_admin_students);

        btnBack = findViewById(R.id.btnBack);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        spFilterDepartment = findViewById(R.id.spFilterDepartment);
        spFilterYear = findViewById(R.id.spFilterYear);
        searchStudent = findViewById(R.id.searchStudent);
        studentListContainer = findViewById(R.id.studentListContainer);
        btnAddStudent = findViewById(R.id.btnAddStudent);

        filterByAdminName = getIntent().getStringExtra("adminName");
        if (filterByAdminName != null && !filterByAdminName.isEmpty()) {
            txtHeaderTitle.setText("Students (" + filterByAdminName + ")");
        } else {
            txtHeaderTitle.setText("Students");
        }

        btnBack.setOnClickListener(v -> finish());

        // Setup Filter Spinners
        setupSpinner(spFilterDepartment, new String[]{"All Departments", "CSE", "AI & DS", "ECE", "IT", "MECH"});
        setupSpinner(spFilterYear, new String[]{"All Years", "II Year", "III Year", "IV Year"});

        // Sample Students across Departments, Years & Student Admins
        allStudents.add(new StudentModel("Aishu M", "CSE22CS001", "CSE", "II Year", "A", "Arun Kumar", "98765 43210", "aishu@college.edu", "92%", "88%", "5"));
        allStudents.add(new StudentModel("Bala K", "CSE22CS002", "CSE", "II Year", "A", "Arun Kumar", "98765 43211", "bala@college.edu", "89%", "84%", "4"));
        allStudents.add(new StudentModel("Dharani R", "AIDS21AD014", "AI & DS", "III Year", "A", "Divya S", "98765 43212", "dharani@college.edu", "94%", "91%", "6"));
        allStudents.add(new StudentModel("Gokul S", "ECE20EC029", "ECE", "IV Year", "B", "Karthik R", "98765 43213", "gokul@college.edu", "86%", "85%", "5"));
        allStudents.add(new StudentModel("Harini V", "IT22IT018", "IT", "II Year", "B", "Nithya P", "98765 43214", "harini@college.edu", "90%", "87%", "5"));

        applyFilters();

        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spFilterDepartment.setOnItemSelectedListener(filterListener);
        spFilterYear.setOnItemSelectedListener(filterListener);

        searchStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAddStudent.setOnClickListener(v -> {
            Intent intent = new Intent(StaffAdminStudents.this, StaffAddStudentActivity.class);
            addStudentLauncher.launch(intent);
        });

        // Bottom Navigation
        navHome = findViewById(R.id.navHome);
        navAdmins = findViewById(R.id.navAdmins);
        navStudents = findViewById(R.id.navStudents);
        navReports = findViewById(R.id.navReports);

        iconHome = findViewById(R.id.iconHome);
        iconAdmins = findViewById(R.id.iconAdmins);
        iconStudents = findViewById(R.id.iconStudents);
        iconReports = findViewById(R.id.iconReports);

        textHome = findViewById(R.id.textHome);
        textAdmins = findViewById(R.id.textAdmins);
        textStudents = findViewById(R.id.textStudents);
        textReports = findViewById(R.id.textReports);

        setActiveNavigation();

        navHome.setOnClickListener(v -> startActivity(new Intent(this, StaffAdmin.class)));
        navAdmins.setOnClickListener(v -> startActivity(new Intent(this, StudentAdmins.class)));
        navStudents.setOnClickListener(v -> {});
        navReports.setOnClickListener(v -> {
            startActivity(new Intent(this, StaffReports.class));
        });
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void applyFilters() {
        studentListContainer.removeAllViews();

        String selectedDept = spFilterDepartment.getSelectedItem().toString();
        String selectedYear = spFilterYear.getSelectedItem().toString();
        String query = searchStudent.getText().toString().toLowerCase().trim();

        for (StudentModel student : allStudents) {
            if (filterByAdminName != null && !filterByAdminName.isEmpty()
                    && !student.assignedAdmin.equalsIgnoreCase(filterByAdminName)) {
                continue;
            }

            if (!selectedDept.equals("All Departments")
                    && !student.department.equalsIgnoreCase(selectedDept)) {
                continue;
            }

            if (!selectedYear.equals("All Years")
                    && !student.year.equalsIgnoreCase(selectedYear)) {
                continue;
            }

            if (!query.isEmpty()
                    && !student.name.toLowerCase().contains(query)
                    && !student.regId.toLowerCase().contains(query)) {
                continue;
            }

            addStudentCard(student);
        }
    }

    private void addStudentCard(StudentModel student) {
        LinearLayout card = new LinearLayout(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dpToPx(10));
        card.setLayoutParams(cardParams);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));
        card.setBackgroundColor(Color.parseColor("#F3EEFF"));

        ImageView avatar = new ImageView(this);
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(dpToPx(44), dpToPx(44));
        avatar.setLayoutParams(avatarParams);
        avatar.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        avatar.setImageResource(R.drawable.profile_symbol);
        avatar.setColorFilter(purple);
        avatar.setBackgroundColor(Color.parseColor("#E2D5FF"));

        LinearLayout infoCol = new LinearLayout(this);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        infoCol.setLayoutParams(infoParams);
        infoCol.setOrientation(LinearLayout.VERTICAL);
        infoCol.setPadding(dpToPx(12), 0, 0, 0);

        TextView tvName = new TextView(this);
        tvName.setText(student.name);
        tvName.setTypeface(null, Typeface.BOLD);
        tvName.setTextSize(16);
        tvName.setTextColor(Color.parseColor("#17112D"));

        TextView tvId = new TextView(this);
        tvId.setText(student.regId + "  •  " + student.department + " (" + student.year + ")");
        tvId.setTextSize(12);
        tvId.setTextColor(Color.parseColor("#555555"));

        TextView tvAdmin = new TextView(this);
        tvAdmin.setText("Admin: " + student.assignedAdmin);
        tvAdmin.setTextSize(12);
        tvAdmin.setTextColor(purple);

        infoCol.addView(tvName);
        infoCol.addView(tvId);
        infoCol.addView(tvAdmin);

        TextView arrow = new TextView(this);
        arrow.setText("›");
        arrow.setTextSize(27);
        arrow.setTextColor(purple);

        card.addView(avatar);
        card.addView(infoCol);
        card.addView(arrow);

        // Open Screen 6 (Student Details)
        card.setOnClickListener(v -> {
            Intent intent = new Intent(StaffAdminStudents.this, StaffStudentDetails.class);
            intent.putExtra("studentName", student.name);
            intent.putExtra("studentId", student.regId);
            intent.putExtra("department", student.department);
            intent.putExtra("year", student.year);
            intent.putExtra("section", student.section);
            intent.putExtra("phone", student.phone);
            intent.putExtra("email", student.email);
            intent.putExtra("attendance", student.attendance);
            intent.putExtra("progress", student.progress);
            intent.putExtra("tasks", student.tasks);
            startActivity(intent);
        });

        studentListContainer.addView(card);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void setActiveNavigation() {
        iconHome.setColorFilter(grey);
        textHome.setTextColor(grey);

        iconAdmins.setColorFilter(grey);
        textAdmins.setTextColor(grey);

        iconStudents.setColorFilter(purple);
        textStudents.setTextColor(purple);

        iconReports.setColorFilter(grey);
        textReports.setTextColor(grey);
    }
}