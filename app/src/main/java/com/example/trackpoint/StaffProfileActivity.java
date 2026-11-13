package com.example.trackpoint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class StaffProfileActivity extends AppCompatActivity {

    ImageView btnBack;
    Button btnLogout;
    TextView tvStaffName, tvStaffRole, tvStaffFullName, tvStaffEmail, tvStaffDepartment, tvStaffPhone;
    StudentRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_staff_profile);

        repository = new StudentRepository(this);

        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);

        tvStaffName = findViewById(R.id.tvStaffName);
        tvStaffRole = findViewById(R.id.tvStaffRole);
        tvStaffFullName = findViewById(R.id.tvStaffFullName);
        tvStaffEmail = findViewById(R.id.tvStaffEmail);
        tvStaffDepartment = findViewById(R.id.tvStaffDepartment);
        tvStaffPhone = findViewById(R.id.tvStaffPhone);

        btnBack.setOnClickListener(v -> finish());

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> showLogoutDialog());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStaffProfile();
    }

    private void loadStaffProfile() {
        SharedPreferences sessionPrefs = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
        String loggedInEmail = sessionPrefs.getString("logged_in_email", "suruthika@gmail.com");

        StaffAdminModel staff = repository.getStaffAdminByEmail(loggedInEmail);

        if (staff != null) {
            if (tvStaffName != null) tvStaffName.setText(staff.getName());
            if (tvStaffFullName != null) tvStaffFullName.setText(staff.getName());
            if (tvStaffEmail != null) tvStaffEmail.setText(staff.getEmail());
            if (tvStaffDepartment != null) tvStaffDepartment.setText(staff.getDepartment());
            if (tvStaffPhone != null) tvStaffPhone.setText(staff.getPhone());
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(StaffProfileActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
