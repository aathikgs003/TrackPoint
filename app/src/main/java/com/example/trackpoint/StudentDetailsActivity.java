package com.example.trackpoint;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StudentDetailsActivity extends AppCompatActivity {

    ImageView btnBack;
    TextView tvStudentName, tvRollNumber, tvDepartment, tvYear, tvSection, tvEmail, tvPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        btnBack = findViewById(R.id.btnBack);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvRollNumber = findViewById(R.id.tvRollNumber);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvYear = findViewById(R.id.tvYear);
        tvSection = findViewById(R.id.tvSection);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);

        btnBack.setOnClickListener(v -> finish());

        // Get intent extras passed from MyStudents
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("student_name");
            String rollNo = intent.getStringExtra("student_roll_no");
            String dept = intent.getStringExtra("student_dept");
            String year = intent.getStringExtra("student_year");
            String section = intent.getStringExtra("student_section");

            if (name != null) tvStudentName.setText(name);
            if (rollNo != null) tvRollNumber.setText(rollNo);
            if (dept != null) tvDepartment.setText(dept);
            if (year != null) tvYear.setText(year);
            if (section != null) tvSection.setText(section);
            if (rollNo != null) tvEmail.setText(rollNo.toLowerCase() + "@college.edu");
        }
    }
}
