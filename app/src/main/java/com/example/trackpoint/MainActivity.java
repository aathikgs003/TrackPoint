package com.example.trackpoint;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText edtEmail;
    EditText edtPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtEmail = findViewById(R.id.username);
        edtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString();

            if (email.isEmpty()) {
                edtEmail.setError("Email is required");
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.equalsIgnoreCase("CSE22CS001")) {
                edtEmail.setError("Enter valid email");
            } else if (password.isEmpty()) {
                edtPassword.setError("Password is required");
            } else if (email.equalsIgnoreCase("aathi@gmail.com") && password.equals("123")) {
                // Student Admin
                Intent intent = new Intent(MainActivity.this, StudentAdmin.class);
                startActivity(intent);
                edtEmail.setText("");
                edtPassword.setText("");
            } else if (email.equalsIgnoreCase("suruthika@gmail.com") && password.equals("123")) {
                // Staff Admin
                Intent intent = new Intent(MainActivity.this, StaffAdmin.class);
                startActivity(intent);
                edtEmail.setText("");
                edtPassword.setText("");
            } else if ((email.equalsIgnoreCase("arun@gmail.com") || email.equalsIgnoreCase("CSE22CS001")) && password.equals("123")) {
                // Student Dashboard
                Intent intent = new Intent(MainActivity.this, StudentDashboardActivity.class);
                startActivity(intent);
                edtEmail.setText("");
                edtPassword.setText("");
            } else {
                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
