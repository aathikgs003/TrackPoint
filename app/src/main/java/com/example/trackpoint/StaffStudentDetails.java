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
    TextView txtStudentName, txtStudentId, txtDepartment, txtYear, txtPhone, txtEmail;
    TextView txtAttendance, txtProgress, txtTasks;

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
            txtPhone.setText(intent.getStringExtra("phone"));
            txtEmail.setText(intent.getStringExtra("email"));
            txtAttendance.setText(intent.getStringExtra("attendance"));
            txtProgress.setText(intent.getStringExtra("progress"));
            txtTasks.setText(intent.getStringExtra("tasks"));
        }

        btnBack.setOnClickListener(v -> finish());

        findViewById(R.id.btnViewStudentWeeklySubmissions).setOnClickListener(v -> {
            Intent historyIntent = new Intent(StaffStudentDetails.this, StaffProgressHistory.class);
            historyIntent.putExtra("studentId", txtStudentId.getText().toString());
            historyIntent.putExtra("studentName", txtStudentName.getText().toString());
            historyIntent.putExtra("weeklyOnly", true);
            startActivity(historyIntent);
        });

    }
}