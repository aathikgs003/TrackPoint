package com.example.trackpoint;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class SuperAdminDepartmentDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_super_admin_department_details);

        ImageView btnBack = findViewById(R.id.btnBack);
        TextView txtDeptHeaderTitle = findViewById(R.id.txtDeptHeaderTitle);
        TextView txtDeptFullName = findViewById(R.id.txtDeptFullName);
        TextView txtDeptStaffCount = findViewById(R.id.txtDeptStaffCount);
        TextView txtDeptStudentAdminCount = findViewById(R.id.txtDeptStudentAdminCount);
        TextView txtDeptStudentCount = findViewById(R.id.txtDeptStudentCount);

        btnBack.setOnClickListener(v -> finish());

        String deptCode = getIntent().getStringExtra("deptCode");
        String deptName = getIntent().getStringExtra("deptName");
        int staffCount = getIntent().getIntExtra("staffCount", 0);
        int studentAdminCount = getIntent().getIntExtra("studentAdminCount", 0);
        int studentCount = getIntent().getIntExtra("studentCount", 0);

        if (deptCode != null) txtDeptHeaderTitle.setText(deptCode + " Department");
        if (deptName != null) txtDeptFullName.setText(deptName);
        txtDeptStaffCount.setText(String.valueOf(staffCount));
        txtDeptStudentAdminCount.setText(String.valueOf(studentAdminCount));
        txtDeptStudentCount.setText(String.valueOf(studentCount));

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
    }
}