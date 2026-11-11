package com.example.trackpoint;

import android.content.Intent;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MyStudents extends AppCompatActivity {

    ImageView btnBack;
    TextView btnAddStudent;
    EditText etSearchStudent;

    Spinner spDepartment, spYear, spSection;
    LinearLayout studentList;

    ArrayList<Student> students = new ArrayList<>();

    // Student model
    static class Student {

        String name;
        String registerNumber;
        String department;
        String year;
        String section;

        Student(String name, String registerNumber,
                String department, String year, String section) {

            this.name = name;
            this.registerNumber = registerNumber;
            this.department = department;
            this.year = year;
            this.section = section;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystudents);

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        btnAddStudent = findViewById(R.id.btnAddStudent);

        etSearchStudent = findViewById(R.id.etSearchStudent);

        spDepartment = findViewById(R.id.spDepartment);
        spYear = findViewById(R.id.spYear);
        spSection = findViewById(R.id.spSection);

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
                "CSE", "IT", "ECE", "EEE"
        });

        // Year dropdown
        setSpinner(spYear, new String[]{
                "All Years",
                "I Year", "II Year", "III Year", "IV Year"
        });

        // Section dropdown
        setSpinner(spSection, new String[]{
                "All Sections", "A", "B", "C"
        });

        // Load sample students
        loadSampleStudents();

        // Display initial student list
        displayStudents(students);

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

        // Section filter
        spSection.setOnItemSelectedListener(filterListener());
    }

    // Sample student data
    private void loadSampleStudents() {

        students.clear();

        students.add(new Student(
                "Arun Kumar", "CSE22CS001",
                "CSE", "III Year", "A"
        ));

        students.add(new Student(
                "Divya S", "CSE22CS002",
                "CSE", "III Year", "A"
        ));

        students.add(new Student(
                "Karthik R", "CSE22CS003",
                "CSE", "III Year", "B"
        ));

        students.add(new Student(
                "Nithya P", "CSE22CS004",
                "CSE", "II Year", "A"
        ));

        students.add(new Student(
                "Praveen M", "CSE22CS005",
                "CSE", "II Year", "B"
        ));
    }

    // Display student rows
    private void displayStudents(ArrayList<Student> list) {

        studentList.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (Student student : list) {

            View row = inflater.inflate(
                    R.layout.item_student,
                    studentList,
                    false
            );

            TextView name = row.findViewById(R.id.tvStudentName);

            TextView rollNumber = row.findViewById(R.id.tvRollNumber);

            View btnView = row.findViewById(R.id.btnViewStudent);

            name.setText(student.name);
            rollNumber.setText(student.registerNumber);

            btnView.setOnClickListener(v -> {

                Toast.makeText(
                        MyStudents.this,
                        "Selected: " + student.name,
                        Toast.LENGTH_SHORT
                ).show();

            });

            studentList.addView(row);
        }
    }

    // Filter students
    private void filterStudents() {

        if (spDepartment.getSelectedItem() == null ||
                spYear.getSelectedItem() == null ||
                spSection.getSelectedItem() == null) {
            return;
        }

        String search = etSearchStudent.getText()
                .toString().trim().toLowerCase();

        String department = spDepartment.getSelectedItem().toString();
        String year = spYear.getSelectedItem().toString();
        String section = spSection.getSelectedItem().toString();

        ArrayList<Student> filteredList = new ArrayList<>();

        for (Student student : students) {

            boolean matchesSearch =
                    student.name.toLowerCase().contains(search) ||
                            student.registerNumber.toLowerCase().contains(search);

            boolean matchesDepartment =
                    department.equals("All Departments") ||
                            student.department.equals(department);

            boolean matchesYear =
                    year.equals("All Years") ||
                            student.year.equals(year);

            boolean matchesSection =
                    section.equals("All Sections") ||
                            student.section.equals(section);

            if (matchesSearch &&
                    matchesDepartment &&
                    matchesYear &&
                    matchesSection) {

                filteredList.add(student);
            }
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
                android.R.layout.simple_spinner_item,
                items
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh when returning from Add Student
        if (studentList != null && !students.isEmpty()) {
            filterStudents();
        }
    }
}
