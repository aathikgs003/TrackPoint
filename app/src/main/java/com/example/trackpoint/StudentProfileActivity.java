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
    TextView tvStudentName, tvStudentEmail, tvStudentPhone, tvStudentDepartment, tvStudentYear, tvStudentSection, tvStudentRegNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);

        tvStudentName = findViewById(R.id.tvStudentName);
        tvStudentRegNo = findViewById(R.id.tvStudentRegNo);
        tvStudentDepartment = findViewById(R.id.tvStudentDepartment);
        tvStudentYear = findViewById(R.id.tvStudentYear);
        tvStudentSection = findViewById(R.id.tvStudentSection);
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
        SharedPreferences prefs = getSharedPreferences("UserProfilePrefs", MODE_PRIVATE);
        String name = prefs.getString("student_name", "Arun Kumar");
        String email = prefs.getString("student_email", "arun@college.edu");
        String phone = prefs.getString("student_phone", "98765 43210");
        String regNo = prefs.getString("student_reg_no", "CSE22CS001");
        String dept = prefs.getString("student_dept", "CSE");
        String year = prefs.getString("student_year", "II Year");
        String section = prefs.getString("student_section", "A");

        if (tvStudentName != null) tvStudentName.setText(name);
        if (tvStudentEmail != null) tvStudentEmail.setText(email);
        if (tvStudentPhone != null) tvStudentPhone.setText(phone);
        if (tvStudentRegNo != null) tvStudentRegNo.setText(regNo);
        if (tvStudentDepartment != null) tvStudentDepartment.setText(dept);
        if (tvStudentYear != null) tvStudentYear.setText(year);
        if (tvStudentSection != null) tvStudentSection.setText(section);
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
