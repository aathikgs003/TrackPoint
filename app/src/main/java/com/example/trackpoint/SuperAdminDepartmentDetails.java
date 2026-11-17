package com.example.trackpoint;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class SuperAdminDepartmentDetails extends AppCompatActivity {

    private TextView txtDeptHeaderTitle, txtDeptFullName;
    private TextView txtDeptStaffCount, txtDeptStudentAdminCount, txtDeptStudentCount;
    private StudentRepository repository;
    private String deptCode, deptName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_super_admin_department_details);

        repository = new StudentRepository(this);

        ImageView btnBack = findViewById(R.id.btnBack);
        txtDeptHeaderTitle = findViewById(R.id.txtDeptHeaderTitle);
        txtDeptFullName = findViewById(R.id.txtDeptFullName);
        txtDeptStaffCount = findViewById(R.id.txtDeptStaffCount);
        txtDeptStudentAdminCount = findViewById(R.id.txtDeptStudentAdminCount);
        txtDeptStudentCount = findViewById(R.id.txtDeptStudentCount);

        btnBack.setOnClickListener(v -> finish());

        deptCode = getIntent().getStringExtra("deptCode");
        deptName = getIntent().getStringExtra("deptName");

        if (deptCode != null) txtDeptHeaderTitle.setText(deptCode + " Department");
        if (deptName != null) txtDeptFullName.setText(deptName);

        loadDepartmentMetrics();

        // Open Staff Admins filtered by this Department
        findViewById(R.id.rowViewDeptStaffAdmins).setOnClickListener(v -> {
            Intent intent = new Intent(this, SuperAdminStaffAdmins.class);
            intent.putExtra("filterDepartment", deptCode);
            startActivity(intent);
        });

        // Open Student Admins filtered by this Department
        findViewById(R.id.rowViewDeptStudentAdmins).setOnClickListener(v -> {
            Intent intent = new Intent(this, SuperAdminStudentAdmins.class);
            intent.putExtra("filterDepartment", deptCode);
            startActivity(intent);
        });

        // Open Students filtered by this Department
        findViewById(R.id.rowViewDeptStudents).setOnClickListener(v -> {
            Intent intent = new Intent(this, SuperAdminStudents.class);
            intent.putExtra("filterDepartment", deptCode);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDepartmentMetrics();
    }

    private void loadDepartmentMetrics() {
        if (deptCode != null) {
            int staffCount = repository.getStaffAdminCountForDept(deptCode);
            int studentAdminCount = repository.getStudentAdminCountForDept(deptCode);
            int studentCount = repository.getStudentCountForDept(deptCode);

            if (txtDeptStaffCount != null) txtDeptStaffCount.setText(String.valueOf(staffCount));
            if (txtDeptStudentAdminCount != null) txtDeptStudentAdminCount.setText(String.valueOf(studentAdminCount));
            if (txtDeptStudentCount != null) txtDeptStudentCount.setText(String.valueOf(studentCount));
        }
    }
}
