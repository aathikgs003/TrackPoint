package com.example.trackpoint;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle phone status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        getWindow().setStatusBarColor(Color.TRANSPARENT);

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(
                        getWindow(),
                        getWindow().getDecorView()
                );

        controller.setAppearanceLightStatusBars(false);

        // Load XML layout
        setContentView(R.layout.activity_student_admin_details);

        // Handle screen spacing
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

        // Admin Details
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

        String adminName = intent != null ? intent.getStringExtra("adminName") : null;
        String adminId = intent != null ? intent.getStringExtra("adminId") : null;
        String department = intent != null ? intent.getStringExtra("department") : null;
        String year = intent != null ? intent.getStringExtra("year") : null;
        String phone = intent != null ? intent.getStringExtra("phone") : null;
        String email = intent != null ? intent.getStringExtra("email") : null;
        String studentsAssigned = intent != null ? intent.getStringExtra("studentsAssigned") : null;
        int studentCount = intent != null ? intent.getIntExtra("studentCount", 0) : 0;

        txtAdminName.setText(adminName != null ? adminName : "Arun Kumar");
        txtAdminId.setText(adminId != null ? adminId : "SADM001");
        txtAdminDepartment.setText(department != null ? department : "CSE");
        txtAdminYear.setText(year != null ? year : "III Year");
        txtAdminPhone.setText(phone != null ? phone : "98765 43201");
        txtAdminEmail.setText(email != null ? email : "arun.admin@college.edu");
        txtStudentsAssigned.setText(studentsAssigned != null ? studentsAssigned : (studentCount > 0 ? String.valueOf(studentCount) : "24"));

        btnBack.setOnClickListener(v -> finish());

        // View Students Button -> Opens Screen 5 with Admin Name
        btnViewStudents.setOnClickListener(v -> {
            Intent viewStudentsIntent = new Intent(
                    StudentAdminDetails.this, StaffAdminStudents.class
            );
            viewStudentsIntent.putExtra("adminName", adminName);
            startActivity(viewStudentsIntent);
        });

        // Attendance & Progress Report Buttons
        btnAttendance.setOnClickListener(v -> {
            startActivity(new Intent(StudentAdminDetails.this, StaffAttendanceReport.class));
        });

        btnProgress.setOnClickListener(v -> {
            Intent progressIntent = new Intent(StudentAdminDetails.this, StaffProgressHistory.class);
            progressIntent.putExtra("adminName", adminName);
            progressIntent.putExtra("dailyAdminMode", true);
            startActivity(progressIntent);
        });
    }
}