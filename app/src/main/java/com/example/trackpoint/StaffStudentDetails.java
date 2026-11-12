package com.example.trackpoint;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class StaffStudentDetails extends AppCompatActivity {

    ImageView btnBack;
    TextView txtStudentName, txtStudentId, txtDepartment, txtYear, txtSection, txtPhone, txtEmail;
    TextView txtAttendance, txtProgress, txtTasks;

    // Bottom Navigation
    LinearLayout navHome, navAdmins, navStudents, navReports;
    ImageView iconHome, iconAdmins, iconStudents, iconReports;
    TextView textHome, textAdmins, textStudents, textReports;

    int purple = Color.parseColor("#4825B8");
    int grey = Color.parseColor("#888888");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4825B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_staff_student_details);

        btnBack = findViewById(R.id.btnBack);
        txtStudentName = findViewById(R.id.txtStudentName);
        txtStudentId = findViewById(R.id.txtStudentId);
        txtDepartment = findViewById(R.id.txtDepartment);
        txtYear = findViewById(R.id.txtYear);
        txtSection = findViewById(R.id.txtSection);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);
        txtAttendance = findViewById(R.id.txtAttendance);
        txtProgress = findViewById(R.id.txtProgress);
        txtTasks = findViewById(R.id.txtTasks);

        Intent intent = getIntent();
        if (intent != null) {
            txtStudentName.setText(intent.getStringExtra("studentName"));
            txtStudentId.setText(intent.getStringExtra("studentId"));
            txtDepartment.setText(intent.getStringExtra("department"));
            txtYear.setText(intent.getStringExtra("year"));
            txtSection.setText(intent.getStringExtra("section"));
            txtPhone.setText(intent.getStringExtra("phone"));
            txtEmail.setText(intent.getStringExtra("email"));
            txtAttendance.setText(intent.getStringExtra("attendance"));
            txtProgress.setText(intent.getStringExtra("progress"));
            txtTasks.setText(intent.getStringExtra("tasks"));
        }

        btnBack.setOnClickListener(v -> finish());

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

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(StaffStudentDetails.this, StaffAdmin.class));
        });

        navAdmins.setOnClickListener(v -> {
            startActivity(new Intent(StaffStudentDetails.this, StudentAdmins.class));
        });

        navStudents.setOnClickListener(v -> {
            finish();
        });

        navReports.setOnClickListener(v -> {
            startActivity(new Intent(this, StaffReports.class));
        });
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