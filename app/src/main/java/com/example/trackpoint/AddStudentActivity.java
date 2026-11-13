package com.example.trackpoint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddStudentActivity extends AppCompatActivity {

    TextView tvHeaderTitle;
    EditText etStudentName, etRegisterNumber, etStudentEmail, etStudentPhone;
    Spinner spDepartment, spYear;
    Button btnSaveStudent;
    ImageView btnBack;
    StudentRepository repository;

    boolean isEditMode = false;
    String rollNoToEdit = null;
    StudentModel existingStudent = null;

    String[] deptItems = new String[]{"Select Department", "CSE", "ECE", "IT", "AI & DS"};
    String[] yearItems = new String[]{"Select Year", "I Year", "II Year", "III Year", "IV Year"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        repository = new StudentRepository(this);

        tvHeaderTitle = findViewById(R.id.header) != null ? ((TextView) findViewById(R.id.header).findViewById(R.id.btnBack).getRootView().findViewById(R.id.header).findViewWithTag("title")) : null;

        etStudentName = findViewById(R.id.etStudentName);
        etRegisterNumber = findViewById(R.id.etRegisterNumber);
        etStudentEmail = findViewById(R.id.etStudentEmail);
        etStudentPhone = findViewById(R.id.etStudentPhone);

        spDepartment = findViewById(R.id.spDepartment);
        spYear = findViewById(R.id.spYear);

        btnSaveStudent = findViewById(R.id.btnSaveStudent);
        btnBack = findViewById(R.id.btnBack);

        setSpinner(spDepartment, deptItems);
        setSpinner(spYear, yearItems);

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("is_edit_mode", false)) {
            isEditMode = true;
            rollNoToEdit = intent.getStringExtra("student_roll_no");
            if (rollNoToEdit == null) {
                rollNoToEdit = intent.getStringExtra("student_reg_no");
            }
        }

        if (isEditMode && rollNoToEdit != null) {
            existingStudent = repository.getStudentByRegNo(rollNoToEdit);
            if (existingStudent != null) {
                if (etStudentName != null) etStudentName.setText(existingStudent.getName());
                if (etRegisterNumber != null) {
                    etRegisterNumber.setText(existingStudent.getRegisterNumber());
                    etRegisterNumber.setEnabled(false);
                }
                if (etStudentEmail != null) etStudentEmail.setText(existingStudent.getEmail());
                if (etStudentPhone != null) etStudentPhone.setText(existingStudent.getPhone());

                setSpinnerSelection(spDepartment, deptItems, existingStudent.getDepartment());
                setSpinnerSelection(spYear, yearItems, existingStudent.getYear());
            }

            if (btnSaveStudent != null) {
                btnSaveStudent.setText("Update Student");
            }
        }

        btnBack.setOnClickListener(v -> finish());

        btnSaveStudent.setOnClickListener(v -> {
            String name = etStudentName.getText().toString().trim();
            String registerNumber = etRegisterNumber.getText().toString().trim();
            String email = etStudentEmail != null ? etStudentEmail.getText().toString().trim() : "";
            String phone = etStudentPhone != null ? etStudentPhone.getText().toString().trim() : "";

            String department = spDepartment.getSelectedItem() != null ? spDepartment.getSelectedItem().toString() : "";
            String year = spYear.getSelectedItem() != null ? spYear.getSelectedItem().toString() : "";

            if (name.isEmpty() || registerNumber.isEmpty() || email.isEmpty() || phone.isEmpty()
                    || spDepartment.getSelectedItemPosition() == 0
                    || spYear.getSelectedItemPosition() == 0) {

                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etStudentEmail.setError("Enter valid login email address");
                return;
            }

            SharedPreferences sessionPrefs = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
            String loggedInAdminName = sessionPrefs.getString("logged_in_admin_name", "Aathi");

            if (isEditMode) {
                if (existingStudent == null) {
                    existingStudent = repository.getStudentByRegNo(registerNumber);
                }

                if (existingStudent != null) {
                    existingStudent.setName(name);
                    existingStudent.setEmail(email);
                    existingStudent.setPhone(phone.startsWith("+91") ? phone : "+91 " + phone);
                    existingStudent.setDepartment(department);
                    existingStudent.setYear(year);

                    boolean updated = repository.updateStudent(existingStudent);
                    if (updated) {
                        Toast.makeText(this, "Student " + name + " updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to update student", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    StudentModel updatedStudent = new StudentModel(name, registerNumber, department, year, email, phone.startsWith("+91") ? phone : "+91 " + phone, "123", loggedInAdminName);
                    repository.addStudent(updatedStudent);
                    Toast.makeText(this, "Student " + name + " updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else { // Add Mode
                if (repository.registerNumberExists(registerNumber)) {
                    Toast.makeText(this, "Register Number already exists in database!", Toast.LENGTH_SHORT).show();
                    return;
                }

                StudentModel newStudent = new StudentModel(
                        name,
                        registerNumber,
                        department,
                        year,
                        email,
                        phone.startsWith("+91") ? phone : "+91 " + phone,
                        "123",
                        loggedInAdminName
                );

                long result = repository.addStudent(newStudent);
                if (result > 0) {
                    Toast.makeText(this, "Student " + name + " assigned to " + loggedInAdminName + " successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save student to database", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                items
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setSpinnerSelection(Spinner spinner, String[] items, String value) {
        if (spinner == null || value == null) return;
        for (int i = 0; i < items.length; i++) {
            if (items[i].equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}
