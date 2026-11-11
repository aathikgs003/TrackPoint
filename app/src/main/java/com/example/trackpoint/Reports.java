package com.example.trackpoint;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Reports extends AppCompatActivity {

    ImageView btnBack;

    LinearLayout btnAttendanceReport;
    LinearLayout btnProgressReport;
    LinearLayout btnStudentReport;
    LinearLayout btnDepartmentReport;
    LinearLayout btnExportExcel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        btnBack = findViewById(R.id.btnBack);

        btnAttendanceReport = findViewById(R.id.btnAttendanceReport);
        btnProgressReport = findViewById(R.id.btnProgressReport);
        btnStudentReport = findViewById(R.id.btnStudentReport);
        btnDepartmentReport = findViewById(R.id.btnDepartmentReport);
        btnExportExcel = findViewById(R.id.btnExportExcel);

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

        // Student Report
        btnStudentReport.setOnClickListener(v ->
                Toast.makeText(this,
                        "Student Report will be added later",
                        Toast.LENGTH_SHORT).show()
        );

        // Department Report
        btnDepartmentReport.setOnClickListener(v ->
                Toast.makeText(this,
                        "Department Report will be added later",
                        Toast.LENGTH_SHORT).show()
        );

        // Excel Export
        btnExportExcel.setOnClickListener(v ->
                Toast.makeText(this,
                        "Excel export will be implemented later",
                        Toast.LENGTH_SHORT).show()
        );
    }
}
