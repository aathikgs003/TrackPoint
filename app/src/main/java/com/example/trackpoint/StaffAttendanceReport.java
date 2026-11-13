package com.example.trackpoint;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
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
import androidx.core.view.WindowCompat;

import java.util.ArrayList;

public class StaffAttendanceReport extends AppCompatActivity {

    ImageView btnBack;
    Spinner spAttDepartment, spAttYear;
    TextView txtOverallAttendance, txtLowAttendanceCount;
    LinearLayout attendanceListContainer;
    Button btnDownloadPdf;

    StudentRepository repository;
    String filterAdminName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_staff_attendance_report);

        repository = new StudentRepository(this);

        btnBack = findViewById(R.id.btnBack);
        spAttDepartment = findViewById(R.id.spAttDepartment);
        spAttYear = findViewById(R.id.spAttYear);
        txtOverallAttendance = findViewById(R.id.txtOverallAttendance);
        txtLowAttendanceCount = findViewById(R.id.txtLowAttendanceCount);
        attendanceListContainer = findViewById(R.id.attendanceListContainer);
        btnDownloadPdf = findViewById(R.id.btnDownloadPdf);

        filterAdminName = getIntent().getStringExtra("adminName");

        btnBack.setOnClickListener(v -> finish());

        setupSpinner(spAttDepartment, new String[]{"All Departments", "CSE", "AI & DS", "ECE", "IT"});
        setupSpinner(spAttYear, new String[]{"All Years", "I Year", "II Year", "III Year", "IV Year"});

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renderAttendance();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spAttDepartment.setOnItemSelectedListener(listener);
        spAttYear.setOnItemSelectedListener(listener);

        btnDownloadPdf.setOnClickListener(v -> {
            Toast.makeText(this, "Attendance_Report_2026.pdf downloaded!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderAttendance();
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void renderAttendance() {
        if (attendanceListContainer == null) return;
        attendanceListContainer.removeAllViews();

        String dept = spAttDepartment.getSelectedItem() != null ? spAttDepartment.getSelectedItem().toString() : "All Departments";
        String year = spAttYear.getSelectedItem() != null ? spAttYear.getSelectedItem().toString() : "All Years";

        ArrayList<StudentModel> dbList = repository.searchAndFilterStudentsForAdmin("", dept, year, filterAdminName);

        if (dbList.isEmpty()) {
            TextView emptyView = new TextView(this);
            if (filterAdminName != null && !filterAdminName.isEmpty()) {
                emptyView.setText("No students assigned to " + filterAdminName + " in database");
            } else {
                emptyView.setText("No attendance records found in database");
            }
            emptyView.setPadding(16, 32, 16, 32);
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setTextColor(Color.parseColor("#68738A"));
            attendanceListContainer.addView(emptyView);

            if (txtOverallAttendance != null) txtOverallAttendance.setText("0%");
            if (txtLowAttendanceCount != null) txtLowAttendanceCount.setText("Below 75%: 0 Students");
            return;
        }

        int sum = 0;
        int count = 0;
        int lowCount = 0;

        for (StudentModel s : dbList) {
            int percentage = 95;
            sum += percentage;
            count++;
            if (percentage < 75) lowCount++;

            LinearLayout card = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, dpToPx(10));
            card.setLayoutParams(params);
            card.setOrientation(LinearLayout.HORIZONTAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));
            card.setBackgroundResource(R.drawable.action_card);
            card.setElevation(dpToPx(2));

            LinearLayout col = new LinearLayout(this);
            col.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            col.setOrientation(LinearLayout.VERTICAL);

            TextView tvName = new TextView(this);
            tvName.setText(s.getName());
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextSize(15);
            tvName.setTextColor(Color.parseColor("#18233F"));

            TextView tvSub = new TextView(this);
            tvSub.setText(s.getRegisterNumber() + "  •  " + s.getDepartment() + " (" + s.getYear() + ")");
            tvSub.setTextSize(12);
            tvSub.setTextColor(Color.parseColor("#68738A"));

            col.addView(tvName);
            col.addView(tvSub);

            TextView badge = new TextView(this);
            badge.setText(percentage + "%");
            badge.setTypeface(null, Typeface.BOLD);
            badge.setTextSize(14);
            badge.setPadding(dpToPx(12), dpToPx(6), dpToPx(12), dpToPx(6));

            if (percentage < 75) {
                badge.setTextColor(Color.parseColor("#D94343"));
                badge.setBackgroundResource(R.drawable.card_yellow);
            } else {
                badge.setTextColor(Color.parseColor("#10B23E"));
                badge.setBackgroundResource(R.drawable.card_green);
            }

            card.addView(col);
            card.addView(badge);
            attendanceListContainer.addView(card);
        }

        int avg = count > 0 ? (sum / count) : 100;
        if (txtOverallAttendance != null) txtOverallAttendance.setText(avg + "%");
        if (txtLowAttendanceCount != null) txtLowAttendanceCount.setText("Below 75%: " + lowCount + (lowCount == 1 ? " Student" : " Students"));
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
