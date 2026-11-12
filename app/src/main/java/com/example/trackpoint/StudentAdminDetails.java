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

    TextView txtAdminName, txtAdminId, txtStudentCount;

    // Bottom Navigation
    LinearLayout navHome, navAdmins, navStudents, navReports;

    ImageView iconHome, iconAdmins, iconStudents, iconReports;

    TextView textHome, textAdmins, textStudents, textReports;

    int purple = Color.parseColor("#4825B8");
    int grey = Color.parseColor("#888888");

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
        txtStudentCount = findViewById(R.id.txtStudentCount);

        Intent intent = getIntent();

        String adminName = intent.getStringExtra("adminName");
        String adminId = intent.getStringExtra("adminId");
        int studentCount = intent.getIntExtra("studentCount", 0);

        txtAdminName.setText(adminName);
        txtAdminId.setText(adminId);
        txtStudentCount.setText(String.valueOf(studentCount));

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
            startActivity(new Intent(StudentAdminDetails.this, StaffProgressReport.class));
        });

        // Bottom Navigation Layouts
        navHome = findViewById(R.id.navHome);
        navAdmins = findViewById(R.id.navAdmins);
        navStudents = findViewById(R.id.navStudents);
        navReports = findViewById(R.id.navReports);

        // Bottom Navigation Icons
        iconHome = findViewById(R.id.iconHome);
        iconAdmins = findViewById(R.id.iconAdmins);
        iconStudents = findViewById(R.id.iconStudents);
        iconReports = findViewById(R.id.iconReports);

        // Bottom Navigation Labels
        textHome = findViewById(R.id.textHome);
        textAdmins = findViewById(R.id.textAdmins);
        textStudents = findViewById(R.id.textStudents);
        textReports = findViewById(R.id.textReports);

        // Highlight Admins as the active page
        setActiveNavigation();

        // Bottom Navigation Clicks
        navHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(
                    StudentAdminDetails.this, StaffAdmin.class
            );
            startActivity(homeIntent);
        });

        navAdmins.setOnClickListener(v -> finish());

        navStudents.setOnClickListener(v -> {
            Intent studentsIntent = new Intent(
                    StudentAdminDetails.this, StaffAdminStudents.class
            );
            startActivity(studentsIntent);
        });

        navReports.setOnClickListener(v -> {
            startActivity(new Intent(this, StaffReports.class));
        });
    }

    private void setActiveNavigation() {

        iconHome.setColorFilter(grey);
        textHome.setTextColor(grey);

        iconAdmins.setColorFilter(purple);
        textAdmins.setTextColor(purple);

        iconStudents.setColorFilter(grey);
        textStudents.setTextColor(grey);

        iconReports.setColorFilter(grey);
        textReports.setTextColor(grey);
    }
}