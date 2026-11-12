package com.example.trackpoint;

import android.content.Intent;
import android.content.res.ColorStateList;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.util.ArrayList;
import java.util.List;

public class StaffProgressReport extends AppCompatActivity {

    ImageView btnBack;
    Spinner spProgDepartment, spProgYear;
    TextView txtAverageProgress, txtTotalSubmissions;
    LinearLayout progressListContainer;
    Button btnDownloadProgressPdf;

    // Bottom Navigation
    LinearLayout navHome, navAdmins, navStudents, navReports;
    ImageView iconHome, iconAdmins, iconStudents, iconReports;
    TextView textHome, textAdmins, textStudents, textReports;

    int purple = Color.parseColor("#4825B8");
    int grey = Color.parseColor("#888888");

    static class ProgRecord {
        String name, regId, dept, year;
        int progressPercent, completedTasks;

        ProgRecord(String name, String regId, String dept, String year, int progressPercent, int completedTasks) {
            this.name = name;
            this.regId = regId;
            this.dept = dept;
            this.year = year;
            this.progressPercent = progressPercent;
            this.completedTasks = completedTasks;
        }
    }

    List<ProgRecord> records = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4825B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_staff_progress_report);

        btnBack = findViewById(R.id.btnBack);
        spProgDepartment = findViewById(R.id.spProgDepartment);
        spProgYear = findViewById(R.id.spProgYear);
        txtAverageProgress = findViewById(R.id.txtAverageProgress);
        txtTotalSubmissions = findViewById(R.id.txtTotalSubmissions);
        progressListContainer = findViewById(R.id.progressListContainer);
        btnDownloadProgressPdf = findViewById(R.id.btnDownloadProgressPdf);

        btnBack.setOnClickListener(v -> finish());

        setupSpinner(spProgDepartment, new String[]{"All Departments", "CSE", "AI & DS", "ECE", "IT"});
        setupSpinner(spProgYear, new String[]{"All Years", "II Year", "III Year", "IV Year"});

        records.add(new ProgRecord("Aishu M", "CSE22CS001", "CSE", "II Year", 88, 5));
        records.add(new ProgRecord("Bala K", "CSE22CS002", "CSE", "II Year", 84, 4));
        records.add(new ProgRecord("Dharani R", "AIDS21AD014", "AI & DS", "III Year", 91, 6));
        records.add(new ProgRecord("Gokul S", "ECE20EC029", "ECE", "IV Year", 85, 5));
        records.add(new ProgRecord("Harini V", "IT22IT018", "IT", "II Year", 87, 5));

        renderProgress();

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renderProgress();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spProgDepartment.setOnItemSelectedListener(listener);
        spProgYear.setOnItemSelectedListener(listener);

        btnDownloadProgressPdf.setOnClickListener(v -> {
            Toast.makeText(this, "Student_Progress_2026.pdf downloaded!", Toast.LENGTH_SHORT).show();
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

    private void renderProgress() {
        progressListContainer.removeAllViews();

        String dept = spProgDepartment.getSelectedItem().toString();
        String year = spProgYear.getSelectedItem().toString();

        int sum = 0;
        int count = 0;
        int tasksSum = 0;

        for (ProgRecord r : records) {
            if (!dept.equals("All Departments") && !r.dept.equalsIgnoreCase(dept)) continue;
            if (!year.equals("All Years") && !r.year.equalsIgnoreCase(year)) continue;

            sum += r.progressPercent;
            tasksSum += r.completedTasks;
            count++;

            LinearLayout card = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, dpToPx(10));
            card.setLayoutParams(params);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));
            card.setBackgroundResource(R.drawable.action_card);
            card.setElevation(2f);

            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvName = new TextView(this);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            tvName.setText(r.name + " (" + r.dept + ")");
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextSize(15);
            tvName.setTextColor(Color.parseColor("#17112D"));

            TextView tvPct = new TextView(this);
            tvPct.setText(r.progressPercent + "%");
            tvPct.setTypeface(null, Typeface.BOLD);
            tvPct.setTextSize(14);
            tvPct.setTextColor(purple);

            topRow.addView(tvName);
            topRow.addView(tvPct);

            TextView tvMeta = new TextView(this);
            tvMeta.setText(r.regId + "  •  " + r.year + "  •  " + r.completedTasks + " Tasks");
            tvMeta.setTextSize(12);
            tvMeta.setTextColor(Color.parseColor("#666666"));
            tvMeta.setPadding(0, dpToPx(2), 0, dpToPx(8));

            ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            bar.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(8)
            ));
            bar.setMax(100);
            bar.setProgress(r.progressPercent);
            bar.setProgressTintList(ColorStateList.valueOf(purple));
            bar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E2D5FF")));

            card.addView(topRow);
            card.addView(tvMeta);
            card.addView(bar);

            progressListContainer.addView(card);
        }

        int avg = count > 0 ? (sum / count) : 0;
        txtAverageProgress.setText(avg + "%");
        txtTotalSubmissions.setText(tasksSum + " Tasks Completed");
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