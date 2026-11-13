package com.example.trackpoint;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class StudentDetailsActivity extends AppCompatActivity {

    ImageView btnBack;
    TextView tvStudentName, tvRollNumber, tvDepartment, tvYear, tvEmail, tvPhone;
    Button btnEditStudent, btnDeleteStudent;

    StudentRepository repository;
    StudentModel currentStudent;
    String rollNoParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        repository = new StudentRepository(this);

        btnBack = findViewById(R.id.btnBack);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvRollNumber = findViewById(R.id.tvRollNumber);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvYear = findViewById(R.id.tvYear);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);

        btnEditStudent = findViewById(R.id.btnEditStudent);
        btnDeleteStudent = findViewById(R.id.btnDeleteStudent);

        btnBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("student_roll_no")) {
                rollNoParam = intent.getStringExtra("student_roll_no");
            } else if (intent.hasExtra("student_reg_no")) {
                rollNoParam = intent.getStringExtra("student_reg_no");
            }
        }

        if (btnEditStudent != null) {
            btnEditStudent.setOnClickListener(v -> openEditStudentScreen());
        }

        if (btnDeleteStudent != null) {
            btnDeleteStudent.setOnClickListener(v -> showDeleteConfirmationDialog());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudentDetails();
    }

    private void loadStudentDetails() {
        if (rollNoParam != null && !rollNoParam.isEmpty()) {
            currentStudent = repository.getStudentByRegNo(rollNoParam);
        }

        Intent intent = getIntent();
        if (currentStudent == null && intent != null) {
            String name = intent.getStringExtra("student_name");
            String rollNo = intent.getStringExtra("student_roll_no");
            if (rollNo == null) rollNo = intent.getStringExtra("student_reg_no");
            String dept = intent.getStringExtra("student_dept");
            String year = intent.getStringExtra("student_year");
            String email = intent.getStringExtra("student_email");
            String phone = intent.getStringExtra("student_phone");

            if (name != null) {
                currentStudent = new StudentModel(
                        name,
                        rollNo != null ? rollNo : "CSE22CS001",
                        dept != null ? dept : "CSE",
                        year != null ? year : "III Year",
                        email != null ? email : "arun@gmail.com",
                        phone != null ? phone : "98765 43210"
                );
            }
        }

        if (currentStudent != null) {
            if (tvStudentName != null) tvStudentName.setText(currentStudent.getName());
            if (tvRollNumber != null) tvRollNumber.setText(currentStudent.getRegisterNumber());
            if (tvDepartment != null) tvDepartment.setText(currentStudent.getDepartment());
            if (tvYear != null) tvYear.setText(currentStudent.getYear());
            if (tvEmail != null) tvEmail.setText(currentStudent.getEmail());
            if (tvPhone != null) tvPhone.setText(currentStudent.getPhone());
        }
    }

    private void openEditStudentScreen() {
        if (currentStudent == null) {
            Toast.makeText(this, "No student record loaded to edit", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent editIntent = new Intent(StudentDetailsActivity.this, AddStudentActivity.class);
        editIntent.putExtra("is_edit_mode", true);
        editIntent.putExtra("student_roll_no", currentStudent.getRegisterNumber());
        startActivity(editIntent);
    }

    private void showDeleteConfirmationDialog() {
        if (currentStudent == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete " + currentStudent.getName() + " (" + currentStudent.getRegisterNumber() + ") from the database?")
                .setPositiveButton("Yes, Delete", (dialog, which) -> {
                    boolean deleted = repository.deleteStudentByRegNo(currentStudent.getRegisterNumber());
                    if (deleted) {
                        Toast.makeText(this, "Student " + currentStudent.getName() + " deleted successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete student", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
