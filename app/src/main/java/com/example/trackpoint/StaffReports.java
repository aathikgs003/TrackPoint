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
    }
}