package com.example.trackpoint;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
    Spinner spYear, spSection;
    Button btnSaveProgress;
    LinearLayout progressList;

    String[] students = {
            "Arun Kumar",
            "Divya S",
            "Karthik R",
            "Nithya P",
            "Praveen M"
    };

    ArrayList<EditText> activityInputs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_progress);

        btnBack = findViewById(R.id.btnBack);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        spYear = findViewById(R.id.spYear);
        spSection = findViewById(R.id.spSection);
        btnSaveProgress = findViewById(R.id.btnSaveProgress);
        progressList = findViewById(R.id.progressList);

        setSpinner(spYear,
                new String[]{"Select Year", "I Year", "II Year",
                        "III Year", "IV Year"});

        setSpinner(spSection,
                new String[]{"Select Section", "A", "B", "C"});

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

        addStudentRows();

        btnBack.setOnClickListener(v -> finish());

        btnSaveProgress.setOnClickListener(v -> {
            if (spYear.getSelectedItemPosition() == 0 ||
                    spSection.getSelectedItemPosition() == 0) {

                Toast.makeText(this,
                        "Please select Year and Section",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this,
                    "Daily progress saved successfully for " + students.length + " students",
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void addStudentRows() {
        if (progressList == null) return;
        progressList.removeAllViews();
        activityInputs.clear();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (String student : students) {
            View row = inflater.inflate(
                    R.layout.item_daily_progress,
                    progressList,
                    false
            );

            TextView name = row.findViewById(R.id.tvStudentName);
            EditText etActivity = row.findViewById(R.id.etActivity);

            name.setText(student);
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
                android.R.layout.simple_spinner_item,
                items
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }
}
