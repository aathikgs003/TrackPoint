package com.example.trackpoint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MyProfile extends AppCompatActivity {

    ImageView btnBack;
    Button btnLogout;
    TextView tvAdminName, tvProfileName, tvProfileEmail, tvProfileDepartment, tvProfilePhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);

        tvAdminName = findViewById(R.id.tvAdminName);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileDepartment = findViewById(R.id.tvProfileDepartment);
        tvProfilePhone = findViewById(R.id.tvProfilePhone);

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
        String name = prefs.getString("admin_name", "Student Admin");
        String email = prefs.getString("admin_email", "admin@example.com");
        String phone = prefs.getString("admin_phone", "+91 9876543210");
        String dept = prefs.getString("admin_dept", "Computer Science");

        if (tvAdminName != null) tvAdminName.setText(name);
        if (tvProfileName != null) tvProfileName.setText(name);
        if (tvProfileEmail != null) tvProfileEmail.setText(email);
        if (tvProfilePhone != null) tvProfilePhone.setText(phone);
        if (tvProfileDepartment != null) tvProfileDepartment.setText(dept);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(MyProfile.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
