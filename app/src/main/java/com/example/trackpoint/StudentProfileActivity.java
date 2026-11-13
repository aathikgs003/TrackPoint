package com.example.trackpoint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class StudentProfileActivity extends AppCompatActivity {

    ImageView btnBack;
    Button btnLogout;
    TextView tvStudentName, tvStudentEmail, tvStudentPhone, tvStudentDepartment, tvStudentYear, tvStudentRegNo;
    StudentRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        repository = new StudentRepository(this);

        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);

        tvStudentName = findViewById(R.id.tvStudentName);
        tvStudentRegNo = findViewById(R.id.tvStudentRegNo);
        tvStudentDepartment = findViewById(R.id.tvStudentDepartment);
        tvStudentYear = findViewById(R.id.tvStudentYear);
        tvStudentEmail = findViewById(R.id.tvStudentEmail);
        tvStudentPhone = findViewById(R.id.tvStudentPhone);

        btnBack.setOnClickListener(v -> finish());

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> showLogoutDialog());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
    }

    private void loadProfileData() {
        SharedPreferences sessionPrefs = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
        String loggedInRegNo = sessionPrefs.getString("logged_in_reg_no", "CSE22CS001");

        StudentModel student = repository.getStudentByRegNo(loggedInRegNo);

        if (student != null) {
            if (tvStudentName != null) tvStudentName.setText(student.getName());
            if (tvStudentRegNo != null) tvStudentRegNo.setText(student.getRegisterNumber());
            if (tvStudentDepartment != null) tvStudentDepartment.setText(student.getDepartment());
            if (tvStudentYear != null) tvStudentYear.setText(student.getYear());
            if (tvStudentEmail != null) tvStudentEmail.setText(student.getEmail());
            if (tvStudentPhone != null) tvStudentPhone.setText(student.getPhone());
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(StudentProfileActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
