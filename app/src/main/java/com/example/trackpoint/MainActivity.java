package com.example.trackpoint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;
    private StudentRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new StudentRepository(this);

        edtEmail = findViewById(R.id.username);
        edtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString();

            if (email.isEmpty()) {
                edtEmail.setError("Email or ID is required");
                return;
            }

            if (password.isEmpty()) {
                edtPassword.setError("Password is required");
                return;
            }

            // 1. Staff Admin Role (SQLite DB check)
            StaffAdminModel staffAdmin = repository.authenticateStaffAdmin(email, password);
            if (staffAdmin != null || (email.equalsIgnoreCase("suruthika@gmail.com") && password.equals("123"))) {
                String staffName = staffAdmin != null ? staffAdmin.getName() : "Suruthika J";
                String staffId = staffAdmin != null ? staffAdmin.getStaffId() : "STF001";
                String staffDept = staffAdmin != null ? staffAdmin.getDepartment() : "CSE";
                String staffEmail = staffAdmin != null ? staffAdmin.getEmail() : email;

                SharedPreferences sessionPrefs = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
                sessionPrefs.edit().putString("logged_in_role", "STAFF_ADMIN")
                        .putString("logged_in_staff_id", staffId)
                        .putString("logged_in_staff_name", staffName)
                        .putString("logged_in_staff_dept", staffDept)
                        .putString("logged_in_staff_email", staffEmail).apply();

                Intent intent = new Intent(MainActivity.this, StaffAdmin.class);
                startActivity(intent);
                edtEmail.setText("");
                edtPassword.setText("");
                return;
            }

            // 2. Student Admin Role (SQLite DB check)
            StudentAdminModel studentAdmin = repository.authenticateStudentAdmin(email, password);
            if (studentAdmin != null || (email.equalsIgnoreCase("aathi@gmail.com") && password.equals("123"))) {
                String adminId = studentAdmin != null ? studentAdmin.getAdminId() : "SADM001";
                String adminName = studentAdmin != null ? studentAdmin.getName() : "Aathi";

                SharedPreferences sessionPrefs = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
                sessionPrefs.edit().putString("logged_in_role", "STUDENT_ADMIN")
                        .putString("logged_in_admin_id", adminId)
                        .putString("logged_in_admin_name", adminName).apply();

                Intent intent = new Intent(MainActivity.this, StudentAdmin.class);
                startActivity(intent);
                edtEmail.setText("");
                edtPassword.setText("");
                return;
            }

            // 3. Student Role (SQLite DB check)
            StudentModel student = repository.authenticateStudent(email, password);
            if (student != null || ((email.equalsIgnoreCase("arun@gmail.com") || email.equalsIgnoreCase("CSE22CS001")) && password.equals("123"))) {
                String regNo = student != null ? student.getRegisterNumber() : "CSE22CS001";

                SharedPreferences sessionPrefs = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
                sessionPrefs.edit().putString("logged_in_role", "STUDENT")
                        .putString("logged_in_reg_no", regNo).apply();

                Intent intent = new Intent(MainActivity.this, StudentDashboardActivity.class);
                startActivity(intent);
                edtEmail.setText("");
                edtPassword.setText("");
                return;
            }

            // 4. Super Admin Role
            if ((email.equalsIgnoreCase("admin@gmail.com") || email.equalsIgnoreCase("superadmin@gmail.com")) && password.equals("123")) {
                SharedPreferences sessionPrefs = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
                sessionPrefs.edit().putString("logged_in_role", "SUPER_ADMIN").apply();

                Intent intent = new Intent(MainActivity.this, SuperAdminDashboard.class);
                startActivity(intent);
                edtEmail.setText("");
                edtPassword.setText("");
                return;
            }

            Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        });
    }
}
