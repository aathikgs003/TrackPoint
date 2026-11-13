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
    StudentRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_add_student_admin);

        repository = new StudentRepository(this);

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
                new String[]{"Select Year", "I Year", "II Year", "III Year", "IV Year"}
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

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etAdminEmail.setError("Enter valid email address");
                return;
            }

            if (repository.adminIdExists(id)) {
                Toast.makeText(this, "Admin ID already exists in database!", Toast.LENGTH_SHORT).show();
                return;
            }

            String dept = spAdminDepartment.getSelectedItem().toString();
            String year = spAdminYear.getSelectedItem().toString();

            StudentAdminModel newAdmin = new StudentAdminModel(
                    name,
                    id,
                    dept,
                    year,
                    email,
                    phone.startsWith("+91") ? phone : "+91 " + phone
            );
            newAdmin.setStudentsAssignedCount(0);

            long rowId = repository.addStudentAdmin(newAdmin);
            if (rowId > 0) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("adminName", name);
                resultIntent.putExtra("adminId", id);
                resultIntent.putExtra("department", dept);
                resultIntent.putExtra("year", year);
                resultIntent.putExtra("phone", phone);
                resultIntent.putExtra("email", email);
                resultIntent.putExtra("studentCount", 0);
                setResult(RESULT_OK, resultIntent);

                Toast.makeText(this, "Student Admin " + name + " saved to database!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save Student Admin to database", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
