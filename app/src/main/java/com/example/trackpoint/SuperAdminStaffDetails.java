package com.example.trackpoint;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class SuperAdminStaffDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_super_admin_staff_details);

        ImageView btnBack = findViewById(R.id.btnBack);
        TextView txtStaffName = findViewById(R.id.txtStaffName);
        TextView txtStaffId = findViewById(R.id.txtStaffId);
        TextView txtStaffDepartment = findViewById(R.id.txtStaffDepartment);
        TextView txtStaffEmail = findViewById(R.id.txtStaffEmail);
        TextView txtStaffPhone = findViewById(R.id.txtStaffPhone);
        TextView txtStaffStudentAdminCount = findViewById(R.id.txtStaffStudentAdminCount);
        Button btnViewAssignedStudentAdmins = findViewById(R.id.btnViewAssignedStudentAdmins);

        btnBack.setOnClickListener(v -> finish());

        String name = getIntent().getStringExtra("staffName");
        String staffId = getIntent().getStringExtra("staffId");
        String department = getIntent().getStringExtra("department");
        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phone");
        int count = getIntent().getIntExtra("studentAdminCount", 0);

        if (name != null) txtStaffName.setText(name);
        if (staffId != null) txtStaffId.setText(staffId);
        if (department != null) txtStaffDepartment.setText(department);
        if (email != null) txtStaffEmail.setText(email);
        if (phone != null) txtStaffPhone.setText(phone);
        txtStaffStudentAdminCount.setText(count + " Student Admins");

        btnViewAssignedStudentAdmins.setOnClickListener(v -> {
            Intent intent = new Intent(this, SuperAdminStudentAdmins.class);
            intent.putExtra("filterDepartment", department);
            intent.putExtra("filterStaffAdmin", name);
            startActivity(intent);
        });
    }
}