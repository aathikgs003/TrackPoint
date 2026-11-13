package com.example.trackpoint;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class StudentAdminDetails extends AppCompatActivity {

    ImageView btnBack;
    Button btnViewStudents, btnAttendance, btnProgress;

    TextView txtAdminName, txtAdminId, txtAdminDepartment, txtAdminYear, txtAdminPhone, txtAdminEmail, txtStudentsAssigned;

    StudentRepository repository;
    String adminNameParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(
                        getWindow(),
                        getWindow().getDecorView()
                );

        controller.setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_student_admin_details);

        repository = new StudentRepository(this);

        LinearLayout rootLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (view, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return windowInsets;
        });

        btnBack = findViewById(R.id.btnBack);
        btnViewStudents = findViewById(R.id.btnViewStudents);
        btnAttendance = findViewById(R.id.btnAttendance);
        btnProgress = findViewById(R.id.btnProgress);

        txtAdminName = findViewById(R.id.txtAdminName);
        txtAdminId = findViewById(R.id.txtAdminId);
        txtAdminDepartment = findViewById(R.id.txtAdminDepartment);
        txtAdminYear = findViewById(R.id.txtAdminYear);
        txtAdminPhone = findViewById(R.id.txtAdminPhone);
        txtAdminEmail = findViewById(R.id.txtAdminEmail);
        txtStudentsAssigned = findViewById(R.id.txtStudentsAssigned);

        Intent intent = getIntent();
        if (intent != null) {
            adminNameParam = intent.getStringExtra("adminName");
        }

        btnBack.setOnClickListener(v -> finish());

        // View Students Button -> Opens StaffAdminStudents filtered by Admin Name
        btnViewStudents.setOnClickListener(v -> {
            Intent viewStudentsIntent = new Intent(
                    StudentAdminDetails.this, StaffAdminStudents.class
            );
            viewStudentsIntent.putExtra("adminName", adminNameParam);
            startActivity(viewStudentsIntent);
        });

        // Attendance Report Button
        btnAttendance.setOnClickListener(v -> {
            Intent attIntent = new Intent(StudentAdminDetails.this, StaffAttendanceReport.class);
            attIntent.putExtra("adminName", adminNameParam);
            startActivity(attIntent);
        });

        // Daily Progress Button
        btnProgress.setOnClickListener(v -> {
            Intent progressIntent = new Intent(StudentAdminDetails.this, StaffProgressHistory.class);
            progressIntent.putExtra("adminName", adminNameParam);
            progressIntent.putExtra("dailyAdminMode", true);
            startActivity(progressIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAdminDetails();
    }

    private void loadAdminDetails() {
        if (adminNameParam == null || adminNameParam.isEmpty()) {
            adminNameParam = "Aathi";
        }

        StudentAdminModel admin = repository.getStudentAdminById(adminNameParam);

        if (admin != null) {
            if (txtAdminName != null) txtAdminName.setText(admin.getName());
            if (txtAdminId != null) txtAdminId.setText(admin.getAdminId());
            if (txtAdminDepartment != null) txtAdminDepartment.setText(admin.getDepartment());
            if (txtAdminYear != null) txtAdminYear.setText(admin.getYear());
            if (txtAdminPhone != null) txtAdminPhone.setText(admin.getPhone());
            if (txtAdminEmail != null) txtAdminEmail.setText(admin.getEmail());

            int realCount = repository.getAssignedStudentsCountForAdmin(admin.getName());
            if (txtStudentsAssigned != null) txtStudentsAssigned.setText(String.valueOf(realCount));
        } else {
            Intent intent = getIntent();
            String name = intent != null ? intent.getStringExtra("adminName") : "Aathi";
            String id = intent != null ? intent.getStringExtra("adminId") : "SADM001";
            String dept = intent != null ? intent.getStringExtra("department") : "CSE";
            String year = intent != null ? intent.getStringExtra("year") : "III Year";
            String phone = intent != null ? intent.getStringExtra("phone") : "98765 43201";
            String email = intent != null ? intent.getStringExtra("email") : "aathi@gmail.com";

            if (txtAdminName != null) txtAdminName.setText(name);
            if (txtAdminId != null) txtAdminId.setText(id);
            if (txtAdminDepartment != null) txtAdminDepartment.setText(dept);
            if (txtAdminYear != null) txtAdminYear.setText(year);
            if (txtAdminPhone != null) txtAdminPhone.setText(phone);
            if (txtAdminEmail != null) txtAdminEmail.setText(email);

            int realCount = repository.getAssignedStudentsCountForAdmin(name);
            if (txtStudentsAssigned != null) txtStudentsAssigned.setText(String.valueOf(realCount));
        }
    }
}
