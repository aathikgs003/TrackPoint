package com.example.trackpoint;

import android.app.DatePickerDialog;
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

    static class AttendanceStudent {
        String name;
        String year;
        String section;

        AttendanceStudent(String name, String year, String section) {
            this.name = name;
            this.year = year;
            this.section = section;
        }
    }

    ArrayList<AttendanceStudent> allStudents = new ArrayList<>();
    ArrayList<Spinner> statusSpinners = new ArrayList<>();

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

        btnAllPresent = findViewById(R.id.btnAllPresent);
        btnAllAbsent = findViewById(R.id.btnAllAbsent);
        btnSubmitAttendance = findViewById(R.id.btnSubmitAttendance);

        attendanceList = findViewById(R.id.attendanceList);

        setSpinner(spYear, new String[]{
                "Select Year", "I Year", "II Year",
                "III Year", "IV Year"
        });

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

        btnSubmitAttendance.setOnClickListener(v -> {
            Toast.makeText(this,
                    "Attendance submitted successfully!",
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadSampleStudents() {
        allStudents.clear();
        allStudents.add(new AttendanceStudent("Arun Kumar", "III Year", "A"));
        allStudents.add(new AttendanceStudent("Divya S", "III Year", "A"));
        allStudents.add(new AttendanceStudent("Karthik R", "III Year", "B"));
        allStudents.add(new AttendanceStudent("Nithya P", "II Year", "A"));
        allStudents.add(new AttendanceStudent("Praveen M", "II Year", "B"));
        allStudents.add(new AttendanceStudent("Kaviya R", "II Year", "A"));
        allStudents.add(new AttendanceStudent("Suresh K", "I Year", "A"));
        allStudents.add(new AttendanceStudent("Meena T", "IV Year", "A"));
    }

    private void filterStudents() {
        if (spYear == null || spYear.getSelectedItem() == null) {
            return;
        }

        String selectedYear = spYear.getSelectedItem().toString();

        ArrayList<AttendanceStudent> filtered = new ArrayList<>();

        for (AttendanceStudent s : allStudents) {
            boolean matchesYear = selectedYear.equals("Select Year") ||
                    selectedYear.equals("All Years") ||
                    s.year.equalsIgnoreCase(selectedYear);

            if (matchesYear) {
                filtered.add(s);
            }
        }

        displayStudents(filtered);
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
                R.layout.spinner_item,
                items
        );

        adapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }
}
