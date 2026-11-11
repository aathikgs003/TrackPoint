package com.example.trackpoint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AttendanceReport extends AppCompatActivity {

    ImageView btnBack;
    Spinner spMonth;
    LinearLayout studentAttendanceList;

    TextView tvDaily, tvWeekly, tvMonthly;

    String[] students = {
            "Arun Kumar",
            "Divya S",
            "Karthik R",
            "Nithya P",
            "Praveen M"
    };

    String[] percentages = {
            "95%", "90%", "80%", "85%", "92%"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report);

        btnBack = findViewById(R.id.btnBack);
        spMonth = findViewById(R.id.spMonth);

        studentAttendanceList =
                findViewById(R.id.studentAttendanceList);

        tvDaily = findViewById(R.id.tvDaily);
        tvWeekly = findViewById(R.id.tvWeekly);
        tvMonthly = findViewById(R.id.tvMonthly);

        setSpinner(spMonth, new String[]{
                "Jan 2025",
                "Feb 2025",
                "Mar 2025",
                "Apr 2025"
        });

        btnBack.setOnClickListener(v -> finish());

        addStudentRows();

        tvDaily.setOnClickListener(v -> selectPeriod("Daily"));
        tvWeekly.setOnClickListener(v -> selectPeriod("Weekly"));
        tvMonthly.setOnClickListener(v -> selectPeriod("Monthly"));
    }

    private void addStudentRows() {

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < students.length; i++) {

            View row = inflater.inflate(
                    R.layout.item_attendance_report,
                    studentAttendanceList,
                    false
            );

            TextView name =
                    row.findViewById(R.id.tvReportStudentName);

            TextView percentage =
                    row.findViewById(R.id.tvReportPercentage);

            name.setText(students[i]);
            percentage.setText(percentages[i]);

            studentAttendanceList.addView(row);
        }
    }

    private void selectPeriod(String period) {

        tvDaily.setTextColor(0xFF52627A);
        tvWeekly.setTextColor(0xFF52627A);
        tvMonthly.setTextColor(0xFF52627A);

        TextView tvPercentage = findViewById(R.id.tvPercentage);
        TextView tvTotalDays = findViewById(R.id.tvTotalDays);
        TextView tvPresentDays = findViewById(R.id.tvPresentDays);
        TextView tvAbsentDays = findViewById(R.id.tvAbsentDays);

        if (period.equals("Daily")) {
            tvDaily.setTextColor(0xFF4935B8);
            if (tvPercentage != null) tvPercentage.setText("100%");
            if (tvTotalDays != null) tvTotalDays.setText("1");
            if (tvPresentDays != null) tvPresentDays.setText("1");
            if (tvAbsentDays != null) tvAbsentDays.setText("0");
        } else if (period.equals("Weekly")) {
            tvWeekly.setTextColor(0xFF4935B8);
            if (tvPercentage != null) tvPercentage.setText("88%");
            if (tvTotalDays != null) tvTotalDays.setText("5");
            if (tvPresentDays != null) tvPresentDays.setText("4");
            if (tvAbsentDays != null) tvAbsentDays.setText("1");
        } else {
            tvMonthly.setTextColor(0xFF4935B8);
            if (tvPercentage != null) tvPercentage.setText("85%");
            if (tvTotalDays != null) tvTotalDays.setText("22");
            if (tvPresentDays != null) tvPresentDays.setText("19");
            if (tvAbsentDays != null) tvAbsentDays.setText("3");
        }
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
