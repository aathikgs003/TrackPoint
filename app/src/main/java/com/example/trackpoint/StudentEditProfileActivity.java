package com.example.trackpoint;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StudentEditProfileActivity extends AppCompatActivity {

    ImageView btnBack;
    EditText etStudentName, etStudentEmail, etStudentPhone;
    Button btnUpdateProfile;
    String userType = "student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_edit_profile);

        btnBack = findViewById(R.id.btnBack);
        etStudentName = findViewById(R.id.etStudentName);
        etStudentEmail = findViewById(R.id.etStudentEmail);
        etStudentPhone = findViewById(R.id.etStudentPhone);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        if (getIntent() != null && getIntent().hasExtra("user_type")) {
            userType = getIntent().getStringExtra("user_type");
        }

        SharedPreferences prefs = getSharedPreferences("UserProfilePrefs", MODE_PRIVATE);

        String initialName = getIntent() != null ? getIntent().getStringExtra("name") : null;
        String initialEmail = getIntent() != null ? getIntent().getStringExtra("email") : null;
        String initialPhone = getIntent() != null ? getIntent().getStringExtra("phone") : null;

        if ("admin".equalsIgnoreCase(userType)) {
            etStudentName.setText(prefs.getString("admin_name", initialName != null ? initialName : "Student Admin"));
            etStudentEmail.setText(prefs.getString("admin_email", initialEmail != null ? initialEmail : "admin@example.com"));
            etStudentPhone.setText(prefs.getString("admin_phone", initialPhone != null ? initialPhone : "+91 9876543210"));
        } else {
            etStudentName.setText(prefs.getString("student_name", initialName != null ? initialName : "Arun Kumar"));
            etStudentEmail.setText(prefs.getString("student_email", initialEmail != null ? initialEmail : "arun@college.edu"));
            etStudentPhone.setText(prefs.getString("student_phone", initialPhone != null ? initialPhone : "98765 43210"));
        }

        btnBack.setOnClickListener(v -> finish());

        btnUpdateProfile.setOnClickListener(v -> {
            String name = etStudentName.getText().toString().trim();
            String email = etStudentEmail.getText().toString().trim();
            String phone = etStudentPhone.getText().toString().trim();

            if (name.isEmpty()) {
                etStudentName.setError("Name cannot be empty");
                return;
            }
            if (email.isEmpty()) {
                etStudentEmail.setError("Email cannot be empty");
                return;
            }
            if (phone.isEmpty()) {
                etStudentPhone.setError("Phone cannot be empty");
                return;
            }

            SharedPreferences.Editor editor = prefs.edit();
            if ("admin".equalsIgnoreCase(userType)) {
                editor.putString("admin_name", name);
                editor.putString("admin_email", email);
                editor.putString("admin_phone", phone);
            } else {
                editor.putString("student_name", name);
                editor.putString("student_email", email);
                editor.putString("student_phone", phone);
            }
            editor.apply();

            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
