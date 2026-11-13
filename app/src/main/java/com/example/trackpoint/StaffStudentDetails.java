package com.example.trackpoint;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class StaffStudentDetails extends AppCompatActivity {

    ImageView btnBack;
    TextView txtStudentName, txtStudentId, txtDepartment, txtYear, txtPhone, txtEmail;
    TextView txtAttendance, txtProgress, txtTasks;

    StudentRepository repository;
    String currentStudentId = "CSE22CS001";
    String currentStudentName = "Arun Kumar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_staff_student_details);

        repository = new StudentRepository(this);

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
            String sId = intent.getStringExtra("studentId");
            if (sId == null) sId = intent.getStringExtra("student_roll_no");
            if (sId != null && !sId.isEmpty()) currentStudentId = sId;

            String sName = intent.getStringExtra("studentName");
            if (sName == null) sName = intent.getStringExtra("student_name");
            if (sName != null && !sName.isEmpty()) currentStudentName = sName;
        }

        btnBack.setOnClickListener(v -> finish());

        findViewById(R.id.btnViewStudentWeeklySubmissions).setOnClickListener(v -> {
            Intent historyIntent = new Intent(StaffStudentDetails.this, StaffProgressHistory.class);
            historyIntent.putExtra("studentId", currentStudentId);
            historyIntent.putExtra("studentName", currentStudentName);
            historyIntent.putExtra("weeklyOnly", true);
            startActivity(historyIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudentDetails();
    }

    private void loadStudentDetails() {
        StudentModel student = repository.getStudentByRegNo(currentStudentId);

        if (student != null) {
            currentStudentName = student.getName();
            if (txtStudentName != null) txtStudentName.setText(student.getName());
            if (txtStudentId != null) txtStudentId.setText(student.getRegisterNumber());
            if (txtDepartment != null) txtDepartment.setText(student.getDepartment());
            if (txtYear != null) txtYear.setText(student.getYear());
            if (txtPhone != null) txtPhone.setText(student.getPhone());
            if (txtEmail != null) txtEmail.setText(student.getEmail());
            if (txtAttendance != null) txtAttendance.setText("95%");
            if (txtProgress != null) txtProgress.setText("90%");
            if (txtTasks != null) txtTasks.setText("1");
        } else {
            Intent intent = getIntent();
            if (intent != null) {
                if (txtStudentName != null) txtStudentName.setText(intent.getStringExtra("studentName"));
                if (txtStudentId != null) txtStudentId.setText(intent.getStringExtra("studentId"));
                if (txtDepartment != null) txtDepartment.setText(intent.getStringExtra("department"));
                if (txtYear != null) txtYear.setText(intent.getStringExtra("year"));
                if (txtPhone != null) txtPhone.setText(intent.getStringExtra("phone"));
                if (txtEmail != null) txtEmail.setText(intent.getStringExtra("email"));
                if (txtAttendance != null) txtAttendance.setText(intent.getStringExtra("attendance"));
                if (txtProgress != null) txtProgress.setText(intent.getStringExtra("progress"));
                if (txtTasks != null) txtTasks.setText(intent.getStringExtra("tasks"));
            }
        }
    }
}
