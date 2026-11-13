package com.example.trackpoint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MyStudents extends AppCompatActivity {

    ImageView btnBack;
    TextView btnAddStudent;
    EditText etSearchStudent;

    Spinner spDepartment, spYear;
    LinearLayout studentList;

    StudentRepository repository;
    String loggedInAdminName = "Aathi";
    String userRole = "STUDENT_ADMIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystudents);

        repository = new StudentRepository(this);

        SharedPreferences sessionPrefs = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
        userRole = sessionPrefs.getString("logged_in_role", "STUDENT_ADMIN");
        loggedInAdminName = sessionPrefs.getString("logged_in_admin_name", "Aathi");

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        btnAddStudent = findViewById(R.id.btnAddStudent);

        etSearchStudent = findViewById(R.id.etSearchStudent);

        spDepartment = findViewById(R.id.spDepartment);
        spYear = findViewById(R.id.spYear);

        studentList = findViewById(R.id.studentList);

        // Back navigation
        btnBack.setOnClickListener(v -> finish());

        // Add Student navigation
        btnAddStudent.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MyStudents.this,
                    AddStudentActivity.class
            );
            startActivity(intent);
        });

        // Department dropdown
        setSpinner(spDepartment, new String[]{
                "All Departments",
                "CSE", "IT", "ECE", "EEE", "AI & DS"
        });

        // Year dropdown
        setSpinner(spYear, new String[]{
                "All Years",
                "I Year", "II Year", "III Year", "IV Year"
        });

        // Search functionality
        etSearchStudent.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                filterStudents();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Department filter
        spDepartment.setOnItemSelectedListener(filterListener());

        // Year filter
        spYear.setOnItemSelectedListener(filterListener());

        filterStudents();
    }

    // Display student rows
    private void displayStudents(ArrayList<StudentModel> list) {
        if (studentList == null) return;
        studentList.removeAllViews();

        if (list == null || list.isEmpty()) {
            TextView emptyView = new TextView(this);
            if ("STUDENT_ADMIN".equalsIgnoreCase(userRole)) {
                emptyView.setText("No students assigned to " + loggedInAdminName + " in database");
            } else {
                emptyView.setText("No students found in database");
            }
            emptyView.setPadding(16, 32, 16, 32);
            emptyView.setGravity(android.view.Gravity.CENTER);
            emptyView.setTextColor(android.graphics.Color.parseColor("#68738A"));
            studentList.addView(emptyView);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);

        for (StudentModel student : list) {
            View row = inflater.inflate(
                    R.layout.item_student,
                    studentList,
                    false
            );

            TextView name = row.findViewById(R.id.tvStudentName);
            TextView rollNumber = row.findViewById(R.id.tvRollNumber);
            View btnView = row.findViewById(R.id.btnViewStudent);

            name.setText(student.getName());
            rollNumber.setText(student.getRegisterNumber());

            btnView.setOnClickListener(v -> {
                Intent intent = new Intent(MyStudents.this, StudentDetailsActivity.class);
                intent.putExtra("student_name", student.getName());
                intent.putExtra("student_roll_no", student.getRegisterNumber());
                intent.putExtra("student_dept", student.getDepartment());
                intent.putExtra("student_year", student.getYear());
                intent.putExtra("student_email", student.getEmail());
                intent.putExtra("student_phone", student.getPhone());
                startActivity(intent);
            });

            studentList.addView(row);
        }
    }

    // Filter students via SQLite database
    private void filterStudents() {
        if (spDepartment == null || spDepartment.getSelectedItem() == null ||
                spYear == null || spYear.getSelectedItem() == null) {
            return;
        }

        String search = etSearchStudent.getText().toString().trim();
        String department = spDepartment.getSelectedItem().toString();
        String year = spYear.getSelectedItem().toString();

        ArrayList<StudentModel> filteredList;
        if ("STUDENT_ADMIN".equalsIgnoreCase(userRole)) {
            filteredList = repository.searchAndFilterStudentsForAdmin(search, department, year, loggedInAdminName);
        } else {
            filteredList = repository.searchAndFilterStudents(search, department, year);
        }

        displayStudents(filteredList);
    }

    // Shared spinner listener
    private AdapterView.OnItemSelectedListener filterListener() {
        return new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(
                    AdapterView<?> parent,
                    View view,
                    int position,
                    long id) {

                filterStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    // Set dropdown adapter
    private void setSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                items
        );

        adapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        filterStudents();
    }
}
