package com.example.trackpoint;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MarkAttendance extends AppCompatActivity {

    ImageView btnBack;
    TextView tvAttendanceDate;
    Spinner spYear, spSection;
    Button btnAllPresent, btnAllAbsent, btnSubmitAttendance;

    LinearLayout attendanceList;

    ArrayList<Spinner> statusSpinners = new ArrayList<>();

    String[] students = {
            "Arun Kumar",
            "Divya S",
            "Karthik R",
            "Nithya P",
            "Praveen M"
    };

    String[] attendanceOptions = {
            "Present", "Absent"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        btnBack = findViewById(R.id.btnBack);
        tvAttendanceDate = findViewById(R.id.tvAttendanceDate);
        spYear = findViewById(R.id.spYear);
        spSection = findViewById(R.id.spSection);

        btnAllPresent = findViewById(R.id.btnAllPresent);
        btnAllAbsent = findViewById(R.id.btnAllAbsent);
        btnSubmitAttendance = findViewById(R.id.btnSubmitAttendance);

        attendanceList = findViewById(R.id.attendanceList);

        setSpinner(spYear, new String[]{
                "Select Year", "I Year", "II Year",
                "III Year", "IV Year"
        });

        setSpinner(spSection, new String[]{
                "Select Section", "A", "B", "C"
        });

        // Set current date
        Calendar calendar = Calendar.getInstance();
        updateDate(calendar);

        tvAttendanceDate.setOnClickListener(v -> {
            Calendar current = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {
                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, day);
                        updateDate(selected);
                    },
                    current.get(Calendar.YEAR),
                    current.get(Calendar.MONTH),
                    current.get(Calendar.DAY_OF_MONTH)
            );

            dialog.show();
        });

        // Create student rows
        addStudentRows();

        // Mark all present
        btnAllPresent.setOnClickListener(v -> {
            for (Spinner spinner : statusSpinners) {
                spinner.setSelection(0);
            }
        });

        // Mark all absent
        btnAllAbsent.setOnClickListener(v -> {
            for (Spinner spinner : statusSpinners) {
                spinner.setSelection(1);
            }
        });

        btnBack.setOnClickListener(v -> finish());

        // Submit attendance
        btnSubmitAttendance.setOnClickListener(v -> {

            if (spYear.getSelectedItemPosition() == 0 ||
                    spSection.getSelectedItemPosition() == 0) {

                Toast.makeText(this,
                        "Please select Year and Section",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder result = new StringBuilder();

            for (int i = 0; i < students.length; i++) {
                result.append(students[i])
                        .append(": ")
                        .append(statusSpinners.get(i).getSelectedItem())
                        .append("\n");
            }

            Toast.makeText(this,
                    "Attendance validated for " + students.length + " students",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void addStudentRows() {

        LayoutInflater inflater = LayoutInflater.from(this);

        for (String student : students) {

            View row = inflater.inflate(
                    R.layout.item_attendance,
                    attendanceList,
                    false
            );

            TextView name = row.findViewById(R.id.tvStudentName);
            Spinner status = row.findViewById(R.id.spAttendanceStatus);

            name.setText(student);

            setSpinner(status, attendanceOptions);

            statusSpinners.add(status);

            attendanceList.addView(row);
        }
    }

    private void updateDate(Calendar calendar) {

        SimpleDateFormat format =
                new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        tvAttendanceDate.setText("📅   " + format.format(calendar.getTime()));
    }

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
}
