package com.example.trackpoint;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddStudentActivity extends AppCompatActivity {

    EditText etStudentName, etRegisterNumber;
    Spinner spDepartment, spYear, spSection;
    Button btnSaveStudent;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        etStudentName = findViewById(R.id.etStudentName);
        etRegisterNumber = findViewById(R.id.etRegisterNumber);

        spDepartment = findViewById(R.id.spDepartment);
        spYear = findViewById(R.id.spYear);
        spSection = findViewById(R.id.spSection);

        btnSaveStudent = findViewById(R.id.btnSaveStudent);
        btnBack = findViewById(R.id.btnBack);

        setSpinner(spDepartment,
                new String[]{"Select Department", "CSE", "ECE", "IT", "AI & DS"});

        setSpinner(spYear,
                new String[]{"Select Year", "I Year", "II Year", "III Year", "IV Year"});

        setSpinner(spSection,
                new String[]{"Select Section", "A", "B", "C"});

        btnBack.setOnClickListener(v -> finish());

        btnSaveStudent.setOnClickListener(v -> {
            String name = etStudentName.getText().toString().trim();
            String registerNumber = etRegisterNumber.getText().toString().trim();

            if (name.isEmpty() || registerNumber.isEmpty()
                    || spDepartment.getSelectedItemPosition() == 0
                    || spYear.getSelectedItemPosition() == 0
                    || spSection.getSelectedItemPosition() == 0) {

                Toast.makeText(this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this,
                    "Student " + name + " saved successfully!",
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }

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
}
