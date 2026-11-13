package com.example.trackpoint;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Reports extends AppCompatActivity {

    ImageView btnBack;
    LinearLayout btnAttendanceReport;
    LinearLayout btnProgressReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        btnBack = findViewById(R.id.btnBack);
        btnAttendanceReport = findViewById(R.id.btnAttendanceReport);
        btnProgressReport = findViewById(R.id.btnProgressReport);

        btnBack.setOnClickListener(v -> finish());

        // Open Attendance Report
        btnAttendanceReport.setOnClickListener(v -> {
            Intent intent = new Intent(
                    Reports.this,
                    AttendanceReport.class
            );
            startActivity(intent);
        });

        // Progress Report
        btnProgressReport.setOnClickListener(v -> {
            Intent intent = new Intent(
                    Reports.this,
                    ProgressHistory.class
            );
            startActivity(intent);
        });
    }
}
