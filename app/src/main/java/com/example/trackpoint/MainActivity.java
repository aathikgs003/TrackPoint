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
            if(email.isEmpty()) {
                edtEmail.setError("Email is required");
            }
            else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Enter valid email");
            }
            else if(password.isEmpty()) {
                edtPassword.setError("Password is required");
            }
            else if(email.equals("aathi00602@gmail.com") && password.equals("123")) {
                Intent intent = new Intent(
                        MainActivity.this, StudentAdmin.class
                );
                startActivity(intent);
                edtEmail.setText("");
                edtPassword.setText("");
            }
            else if (email.equals("suruthika@gmail.com")
                    && password.equals("123")) {

                Intent intent = new Intent(
                        MainActivity.this, StaffAdmin.class
                );
                startActivity(intent);

                edtEmail.setText("");
                edtPassword.setText("");
            }
            else {
                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}