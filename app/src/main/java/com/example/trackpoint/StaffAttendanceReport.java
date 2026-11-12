package com.example.trackpoint;

import android.content.Intent;
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
import java.util.List;

public class StaffAttendanceReport extends AppCompatActivity {

    ImageView btnBack;
    Spinner spAttDepartment, spAttYear;
    TextView txtOverallAttendance, txtLowAttendanceCount;
    LinearLayout attendanceListContainer;
    Button btnDownloadPdf;

    // Bottom Navigation
    LinearLayout navHome, navAdmins, navStudents, navReports;
    ImageView iconHome, iconAdmins, iconStudents, iconReports;
    TextView textHome, textAdmins, textStudents, textReports;

    int purple = Color.parseColor("#4825B8");
    int grey = Color.parseColor("#888888");

    static class AttRecord {
        String name, regId, dept, year;
        int percentage;

        AttRecord(String name, String regId, String dept, String year, int percentage) {
            this.name = name;
            this.regId = regId;
            this.dept = dept;
            this.year = year;
            this.percentage = percentage;
        }
    }

    List<AttRecord> records = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4825B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_staff_attendance_report);

        btnBack = findViewById(R.id.btnBack);
        spAttDepartment = findViewById(R.id.spAttDepartment);
        spAttYear = findViewById(R.id.spAttYear);
        txtOverallAttendance = findViewById(R.id.txtOverallAttendance);
        txtLowAttendanceCount = findViewById(R.id.txtLowAttendanceCount);
        attendanceListContainer = findViewById(R.id.attendanceListContainer);
        btnDownloadPdf = findViewById(R.id.btnDownloadPdf);

        btnBack.setOnClickListener(v -> finish());

        setupSpinner(spAttDepartment, new String[]{"All Departments", "CSE", "AI & DS", "ECE", "IT"});
        setupSpinner(spAttYear, new String[]{"All Years", "II Year", "III Year", "IV Year"});

        records.add(new AttRecord("Aishu M", "CSE22CS001", "CSE", "II Year", 92));
        records.add(new AttRecord("Bala K", "CSE22CS002", "CSE", "II Year", 72));
        records.add(new AttRecord("Dharani R", "AIDS21AD014", "AI & DS", "III Year", 94));
        records.add(new AttRecord("Gokul S", "ECE20EC029", "ECE", "IV Year", 86));
        records.add(new AttRecord("Harini V", "IT22IT018", "IT", "II Year", 90));

        renderAttendance();

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

        // Bottom Navigation
        navHome = findViewById(R.id.navHome);
        navAdmins = findViewById(R.id.navAdmins);
        navStudents = findViewById(R.id.navStudents);
        navReports = findViewById(R.id.navReports);

        iconHome = findViewById(R.id.iconHome);
        iconAdmins = findViewById(R.id.iconAdmins);
        iconStudents = findViewById(R.id.iconStudents);
        iconReports = findViewById(R.id.iconReports);

        textHome = findViewById(R.id.textHome);
        textAdmins = findViewById(R.id.textAdmins);
        textStudents = findViewById(R.id.textStudents);
        textReports = findViewById(R.id.textReports);

        setActiveNavigation();

        navHome.setOnClickListener(v -> startActivity(new Intent(this, StaffAdmin.class)));
        navAdmins.setOnClickListener(v -> startActivity(new Intent(this, StudentAdmins.class)));
        navStudents.setOnClickListener(v -> startActivity(new Intent(this, StaffAdminStudents.class)));
        navReports.setOnClickListener(v -> finish());
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void renderAttendance() {
        attendanceListContainer.removeAllViews();

        String dept = spAttDepartment.getSelectedItem().toString();
        String year = spAttYear.getSelectedItem().toString();

        int sum = 0;
        int count = 0;
        int lowCount = 0;

        for (AttRecord r : records) {
            if (!dept.equals("All Departments") && !r.dept.equalsIgnoreCase(dept)) continue;
            if (!year.equals("All Years") && !r.year.equalsIgnoreCase(year)) continue;

            sum += r.percentage;
            count++;
            if (r.percentage < 75) lowCount++;

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
            card.setElevation(2f);

            LinearLayout col = new LinearLayout(this);
            col.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            col.setOrientation(LinearLayout.VERTICAL);

            TextView tvName = new TextView(this);
            tvName.setText(r.name);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextSize(15);
            tvName.setTextColor(Color.parseColor("#17112D"));

            TextView tvSub = new TextView(this);
            tvSub.setText(r.regId + "  •  " + r.dept + " (" + r.year + ")");
            tvSub.setTextSize(12);
            tvSub.setTextColor(Color.parseColor("#666666"));

            col.addView(tvName);
            col.addView(tvSub);

            TextView badge = new TextView(this);
            badge.setText(r.percentage + "%");
            badge.setTypeface(null, Typeface.BOLD);
            badge.setTextSize(14);
            badge.setPadding(dpToPx(12), dpToPx(6), dpToPx(12), dpToPx(6));

            if (r.percentage < 75) {
                badge.setTextColor(Color.parseColor("#D94343"));
                badge.setBackgroundColor(Color.parseColor("#FDECEC"));
            } else {
                badge.setTextColor(Color.parseColor("#168B43"));
                badge.setBackgroundColor(Color.parseColor("#E9F9EF"));
            }

            card.addView(col);
            card.addView(badge);
            attendanceListContainer.addView(card);
        }

        int avg = count > 0 ? (sum / count) : 0;
        txtOverallAttendance.setText(avg + "%");
        txtLowAttendanceCount.setText("Below 75%: " + lowCount + (lowCount == 1 ? " Student" : " Students"));
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void setActiveNavigation() {
        iconHome.setColorFilter(grey);
        textHome.setTextColor(grey);

        iconAdmins.setColorFilter(grey);
        textAdmins.setTextColor(grey);

        iconStudents.setColorFilter(grey);
        textStudents.setTextColor(grey);

        iconReports.setColorFilter(purple);
        textReports.setTextColor(purple);
    }
}