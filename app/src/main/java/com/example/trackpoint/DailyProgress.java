package com.example.trackpoint;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class DailyProgress extends AppCompatActivity {

    ImageView btnBack;
    TextView tvSelectedDate;
    Spinner spYear;
    Button btnSaveProgress;
    LinearLayout progressList;

    static class ProgressStudent {
        String name;
        String year;

        ProgressStudent(String name, String year) {
            this.name = name;
            this.year = year;
        }
    }

    ArrayList<ProgressStudent> allStudents = new ArrayList<>();
    ArrayList<EditText> activityInputs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_progress);

        btnBack = findViewById(R.id.btnBack);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        spYear = findViewById(R.id.spYear);
        btnSaveProgress = findViewById(R.id.btnSaveProgress);
        progressList = findViewById(R.id.progressList);

        setSpinner(spYear,
                new String[]{"Select Year", "I Year", "II Year",
                        "III Year", "IV Year"});

        Calendar calendar = Calendar.getInstance();
        updateDate(calendar);

        tvSelectedDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    DailyProgress.this,
                    (view, year, month, day) -> {
                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, day);
                        updateDate(selected);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });

        loadSampleStudents();

        // Initial fetch/filter
        filterStudents();

        // Spinner item listener for dynamic filtering by Year
        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnBack.setOnClickListener(v -> finish());

        btnSaveProgress.setOnClickListener(v -> {
            if (spYear.getSelectedItemPosition() == 0) {
                Toast.makeText(this,
                        "Please select Year",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this,
                    "Daily progress saved successfully!",
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadSampleStudents() {
        allStudents.clear();
        StudentRepository repo = new StudentRepository(this);
        android.content.SharedPreferences sessionPrefs = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
        String role = sessionPrefs.getString("logged_in_role", "STUDENT_ADMIN");
        String loggedInAdminName = sessionPrefs.getString("logged_in_admin_name", "Aathi");

        ArrayList<StudentModel> dbList;
        if ("STUDENT_ADMIN".equalsIgnoreCase(role)) {
            dbList = repo.searchAndFilterStudentsForAdmin("", "All Departments", "All Years", loggedInAdminName);
        } else {
            dbList = repo.getAllStudents();
        }

        for (StudentModel s : dbList) {
            allStudents.add(new ProgressStudent(s.getName(), s.getYear()));
        }
    }

    private void filterStudents() {
        if (spYear == null || spYear.getSelectedItem() == null) {
            return;
        }

        String selectedYear = spYear.getSelectedItem().toString();

        ArrayList<ProgressStudent> filtered = new ArrayList<>();

        for (ProgressStudent s : allStudents) {
            boolean matchesYear = selectedYear.equals("Select Year") ||
                    selectedYear.equals("All Years") ||
                    s.year.equalsIgnoreCase(selectedYear);

            if (matchesYear) {
                filtered.add(s);
            }
        }

        displayStudents(filtered);
    }

    private void displayStudents(ArrayList<ProgressStudent> list) {
        if (progressList == null) return;
        progressList.removeAllViews();
        activityInputs.clear();

        LayoutInflater inflater = LayoutInflater.from(this);

        if (list.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No students found for selected filter");
            emptyView.setPadding(16, 32, 16, 32);
            emptyView.setGravity(android.view.Gravity.CENTER);
            emptyView.setTextColor(android.graphics.Color.parseColor("#68738A"));
            progressList.addView(emptyView);
            return;
        }

        for (ProgressStudent student : list) {
            View row = inflater.inflate(
                    R.layout.item_daily_progress,
                    progressList,
                    false
            );

            TextView name = row.findViewById(R.id.tvStudentName);
            EditText etActivity = row.findViewById(R.id.etActivity);

            name.setText(student.name);
            // etActivity is intentionally left blank so user enters fresh input!

            activityInputs.add(etActivity);
            progressList.addView(row);
        }
    }

    private void updateDate(Calendar calendar) {
        SimpleDateFormat format =
                new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        tvSelectedDate.setText("📅   " + format.format(calendar.getTime()));
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
