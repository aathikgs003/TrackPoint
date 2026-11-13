package com.example.trackpoint;

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

public class StaffProgressReport extends AppCompatActivity {

    ImageView btnBack;
    Spinner spProgDepartment, spProgYear;
    TextView txtAverageProgress, txtTotalSubmissions;
    LinearLayout progressListContainer;
    Button btnDownloadProgressPdf;

    StudentRepository repository;
    int purple = Color.parseColor("#4935B8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_staff_progress_report);

        repository = new StudentRepository(this);

        btnBack = findViewById(R.id.btnBack);
        spProgDepartment = findViewById(R.id.spProgDepartment);
        spProgYear = findViewById(R.id.spProgYear);
        txtAverageProgress = findViewById(R.id.txtAverageProgress);
        txtTotalSubmissions = findViewById(R.id.txtTotalSubmissions);
        progressListContainer = findViewById(R.id.progressListContainer);
        btnDownloadProgressPdf = findViewById(R.id.btnDownloadProgressPdf);

        btnBack.setOnClickListener(v -> finish());

        setupSpinner(spProgDepartment, new String[]{"All Departments", "CSE", "AI & DS", "ECE", "IT"});
        setupSpinner(spProgYear, new String[]{"All Years", "I Year", "II Year", "III Year", "IV Year"});

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderProgress();
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void renderProgress() {
        if (progressListContainer == null) return;
        progressListContainer.removeAllViews();

        String dept = spProgDepartment.getSelectedItem() != null ? spProgDepartment.getSelectedItem().toString() : "All Departments";
        String year = spProgYear.getSelectedItem() != null ? spProgYear.getSelectedItem().toString() : "All Years";

        ArrayList<StudentModel> dbList = repository.searchAndFilterStudents("", dept, year);

        if (dbList.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No student progress entries found in database");
            emptyView.setPadding(16, 32, 16, 32);
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setTextColor(Color.parseColor("#68738A"));
            progressListContainer.addView(emptyView);

            if (txtAverageProgress != null) txtAverageProgress.setText("0%");
            if (txtTotalSubmissions != null) txtTotalSubmissions.setText("0 Tasks Completed");
            return;
        }

        int sum = 0;
        int count = 0;
        int tasksSum = 0;

        for (StudentModel s : dbList) {
            int progressPercent = 90;
            int completedTasks = 1;

            sum += progressPercent;
            tasksSum += completedTasks;
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
            card.setElevation(dpToPx(2));

            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvName = new TextView(this);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            tvName.setText(s.getName() + " (" + s.getDepartment() + ")");
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextSize(15);
            tvName.setTextColor(Color.parseColor("#18233F"));

            TextView tvPct = new TextView(this);
            tvPct.setText(progressPercent + "%");
            tvPct.setTypeface(null, Typeface.BOLD);
            tvPct.setTextSize(14);
            tvPct.setTextColor(purple);

            topRow.addView(tvName);
            topRow.addView(tvPct);

            TextView tvMeta = new TextView(this);
            tvMeta.setText(s.getRegisterNumber() + "  •  " + s.getYear() + "  •  " + completedTasks + " Tasks");
            tvMeta.setTextSize(12);
            tvMeta.setTextColor(Color.parseColor("#68738A"));
            tvMeta.setPadding(0, dpToPx(2), 0, dpToPx(8));

            ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            bar.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(8)
            ));
            bar.setMax(100);
            bar.setProgress(progressPercent);
            bar.setProgressTintList(ColorStateList.valueOf(purple));
            bar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EAE5FF")));

            card.addView(topRow);
            card.addView(tvMeta);
            card.addView(bar);

            progressListContainer.addView(card);
        }

        int avg = count > 0 ? (sum / count) : 0;
        if (txtAverageProgress != null) txtAverageProgress.setText(avg + "%");
        if (txtTotalSubmissions != null) txtTotalSubmissions.setText(tasksSum + " Tasks Completed");
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
