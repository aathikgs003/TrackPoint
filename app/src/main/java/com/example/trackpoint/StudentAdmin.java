package com.example.trackpoint;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StudentAdmin extends AppCompatActivity {
    TextView students, progress, attendance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_admin_dashboard);
        students = findViewById(R.id.navstudents);
        progress = findViewById(R.id.navprogress);
        attendance = findViewById(R.id.navattendance);
        students.setOnClickListener(v -> {
            Intent intent = new Intent(
                   StudentAdmin.this, MyStudents.class
            );
            startActivity(intent);
        });
        progress.setOnClickListener(v -> {
            Intent intent = new Intent(
                    StudentAdmin.this, StudentProgress.class
            );
            startActivity(intent);
        });
        attendance.setOnClickListener(v -> {
            Intent intent = new Intent(
                    StudentAdmin.this, StudentAttendance.class
            );
        });
    }
}
