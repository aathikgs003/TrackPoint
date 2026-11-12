package com.example.trackpoint;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class StaffReports extends AppCompatActivity {

    ImageView btnBack;
    LinearLayout cardAttendanceReport, cardProgressReport;

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

        setContentView(R.layout.activity_staff_reports);

        btnBack = findViewById(R.id.btnBack);
        cardAttendanceReport = findViewById(R.id.cardAttendanceReport);
        cardProgressReport = findViewById(R.id.cardProgressReport);

        btnBack.setOnClickListener(v -> finish());

        cardAttendanceReport.setOnClickListener(v -> {
            startActivity(new Intent(StaffReports.this, StaffAttendanceReport.class));
        });

        cardProgressReport.setOnClickListener(v -> {
            startActivity(new Intent(StaffReports.this, StaffProgressReport.class));
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
        navStudents.setOnClickListener(v -> startActivity(new Intent(this, StaffAdminStudents.class)));
        navReports.setOnClickListener(v -> {});
    }

    private void setActiveNavigation() {
        iconHome.setColorFilter(grey);
        textHome.setTextColor(grey);

        iconAdmins.setColorFilter(grey);
        textAdmins.setTextColor(grey);

        iconStudents.setColorFilter(grey);
        textStudents.setTextColor(grey);

        iconReports.setColorFilter(purple);
        textReports.setTextColor(purple);
    }
}