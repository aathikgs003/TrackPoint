package com.example.trackpoint;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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
    Spinner spYear;
    Button btnAllPresent, btnAllAbsent, btnSubmitAttendance;

    LinearLayout attendanceList;
    Calendar selectedCalendar;
    StudentRepository repository;

    static class AttendanceStudent {
        String name;
        String regNo;
        String year;

        AttendanceStudent(String name, String regNo, String year) {
            this.name = name;
            this.regNo = regNo;
            this.year = year;
        }
    }

    ArrayList<AttendanceStudent> allStudents = new ArrayList<>();
    ArrayList<AttendanceStudent> currentFilteredList = new ArrayList<>();
    ArrayList<Spinner> statusSpinners = new ArrayList<>();

    String[] attendanceOptions = {
            "Present", "Absent"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        repository = new StudentRepository(this);

        btnBack = findViewById(R.id.btnBack);
        tvAttendanceDate = findViewById(R.id.tvAttendanceDate);
        spYear = findViewById(R.id.spYear);

        btnAllPresent = findViewById(R.id.btnAllPresent);
        btnAllAbsent = findViewById(R.id.btnAllAbsent);
        btnSubmitAttendance = findViewById(R.id.btnSubmitAttendance);

        attendanceList = findViewById(R.id.attendanceList);

        setSpinner(spYear, new String[]{
                "Select Year", "I Year", "II Year",
                "III Year", "IV Year"
        });

        selectedCalendar = Calendar.getInstance();
        updateDate(selectedCalendar);

        tvAttendanceDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {
                        selectedCalendar.set(year, month, day);
                        updateDate(selectedCalendar);
                        filterStudents();
                    },
                    selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH)
            );

            dialog.show();
        });

        loadSampleStudents();

        filterStudents();

        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnAllPresent.setOnClickListener(v -> {
            for (Spinner spinner : statusSpinners) {
                spinner.setSelection(0);
            }
        });

        btnAllAbsent.setOnClickListener(v -> {
            for (Spinner spinner : statusSpinners) {
                spinner.setSelection(1);
            }
        });

        btnBack.setOnClickListener(v -> finish());

        btnSubmitAttendance.setOnClickListener(v -> submitAttendanceData());
    }

    private void submitAttendanceData() {
        SimpleDateFormat keyFmt = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());
        String dateKey = keyFmt.format(selectedCalendar.getTime());

        SharedPreferences prefs = getSharedPreferences("AttendanceStore", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        for (int i = 0; i < statusSpinners.size(); i++) {
            if (i < currentFilteredList.size()) {
                AttendanceStudent s = currentFilteredList.get(i);
                String statusVal = statusSpinners.get(i).getSelectedItem() != null ?
                        statusSpinners.get(i).getSelectedItem().toString() : "Present";

                editor.putString("status_" + s.regNo + "_" + dateKey, statusVal);

                // Save into SQLite Database
                repository.saveOrUpdateAttendance(s.regNo, dateKey, statusVal, "Student Admin");
            }
        }

        editor.apply();

        Toast.makeText(this, "Attendance submitted successfully for " + tvAttendanceDate.getText().toString(), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void loadSampleStudents() {
        allStudents.clear();
        ArrayList<StudentModel> dbList = repository.getAllStudents();
        for (StudentModel s : dbList) {
            allStudents.add(new AttendanceStudent(s.getName(), s.getRegisterNumber(), s.getYear()));
        }
    }

    private void filterStudents() {
        if (spYear == null || spYear.getSelectedItem() == null) {
            return;
        }

        String selectedYear = spYear.getSelectedItem().toString();
        currentFilteredList.clear();

        for (AttendanceStudent s : allStudents) {
            boolean matchesYear = selectedYear.equals("Select Year") ||
                    selectedYear.equals("All Years") ||
                    s.year.equalsIgnoreCase(selectedYear);

            if (matchesYear) {
                currentFilteredList.add(s);
            }
        }

        displayStudents(currentFilteredList);
    }

    private void displayStudents(ArrayList<AttendanceStudent> list) {
        if (attendanceList == null) return;
        attendanceList.removeAllViews();
        statusSpinners.clear();

        LayoutInflater inflater = LayoutInflater.from(this);

        if (list.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No students found for selected filter");
            emptyView.setPadding(16, 32, 16, 32);
            emptyView.setGravity(android.view.Gravity.CENTER);
            emptyView.setTextColor(android.graphics.Color.parseColor("#68738A"));
            attendanceList.addView(emptyView);
            return;
        }

        SimpleDateFormat keyFmt = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());
        String dateKey = keyFmt.format(selectedCalendar.getTime());
        SharedPreferences prefs = getSharedPreferences("AttendanceStore", MODE_PRIVATE);

        for (AttendanceStudent student : list) {
            View row = inflater.inflate(
                    R.layout.item_attendance,
                    attendanceList,
                    false
            );

            TextView name = row.findViewById(R.id.tvStudentName);
            Spinner status = row.findViewById(R.id.spAttendanceStatus);

            name.setText(student.name);
            setSpinner(status, attendanceOptions);

            String dbStatus = repository.getAttendanceStatus(student.regNo, dateKey);
            String savedStatus = dbStatus != null ? dbStatus : prefs.getString("status_" + student.regNo + "_" + dateKey, "Present");

            if ("Absent".equalsIgnoreCase(savedStatus)) {
                status.setSelection(1);
            } else {
                status.setSelection(0);
            }

            statusSpinners.add(status);
            attendanceList.addView(row);
        }
    }

    private void updateDate(Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        tvAttendanceDate.setText("📅   " + format.format(calendar.getTime()));
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
