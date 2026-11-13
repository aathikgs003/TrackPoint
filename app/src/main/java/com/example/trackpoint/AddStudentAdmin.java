package com.example.trackpoint;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class AddStudentAdmin extends AppCompatActivity {

    EditText etAdminName, etAdminId, etAdminEmail, etAdminPhone;
    Spinner spAdminDepartment, spAdminYear;
    Button btnSaveAdmin;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4825B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_add_student_admin);

        etAdminName = findViewById(R.id.etAdminName);
        etAdminId = findViewById(R.id.etAdminId);
        etAdminEmail = findViewById(R.id.etAdminEmail);
        etAdminPhone = findViewById(R.id.etAdminPhone);
        spAdminDepartment = findViewById(R.id.spAdminDepartment);
        spAdminYear = findViewById(R.id.spAdminYear);
        btnSaveAdmin = findViewById(R.id.btnSaveAdmin);
        btnBack = findViewById(R.id.btnBack);

        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                new String[]{"Select Department", "CSE", "AI & DS", "ECE", "IT", "MECH"}
        );
        deptAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spAdminDepartment.setAdapter(deptAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                new String[]{"Select Year", "II Year", "III Year", "IV Year"}
        );
        yearAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spAdminYear.setAdapter(yearAdapter);

        btnBack.setOnClickListener(v -> finish());

        btnSaveAdmin.setOnClickListener(v -> {
            String name = etAdminName.getText().toString().trim();
            String id = etAdminId.getText().toString().trim();
            String email = etAdminEmail.getText().toString().trim();
            String phone = etAdminPhone.getText().toString().trim();

            if (name.isEmpty() || id.isEmpty() || email.isEmpty() || phone.isEmpty()
                    || spAdminDepartment.getSelectedItemPosition() == 0
                    || spAdminYear.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("adminName", name);
            resultIntent.putExtra("adminId", id);
            resultIntent.putExtra("department", spAdminDepartment.getSelectedItem().toString());
            resultIntent.putExtra("year", spAdminYear.getSelectedItem().toString());
            resultIntent.putExtra("phone", phone);
            resultIntent.putExtra("email", email);
            resultIntent.putExtra("studentCount", 0);
            setResult(RESULT_OK, resultIntent);

            Toast.makeText(this, "Student Admin " + name + " added!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}