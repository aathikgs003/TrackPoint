package com.example.trackpoint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StudentAdmin extends AppCompatActivity {

    TextView tvStudentCount, tvAttendance, tvProgressCount, tvPending;
    StudentRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_admin_dashboard);

        repository = new StudentRepository(this);

        tvStudentCount = findViewById(R.id.tvStudentCount);
        tvAttendance = findViewById(R.id.tvAttendance);
        tvProgressCount = findViewById(R.id.tvProgressCount);
        tvPending = findViewById(R.id.tvPending);

        // My Students Navigation
        View.OnClickListener openStudents = v -> startActivity(new Intent(StudentAdmin.this, MyStudents.class));
        findViewById(R.id.btnMyStudents).setOnClickListener(openStudents);

        // Daily Progress Navigation
        View.OnClickListener openProgress = v -> startActivity(new Intent(StudentAdmin.this, DailyProgress.class));
        findViewById(R.id.btnDailyProgress).setOnClickListener(openProgress);

        // Attendance Navigation
        View.OnClickListener openAttendance = v -> startActivity(new Intent(StudentAdmin.this, MarkAttendance.class));
        findViewById(R.id.btnAttendance).setOnClickListener(openAttendance);

        // Progress History Navigation
        findViewById(R.id.btnProgressHistory).setOnClickListener(v -> {
            Intent intent = new Intent(
                    StudentAdmin.this,
                    ProgressHistory.class
            );
            startActivity(intent);
        });

        // Reports Navigation
        findViewById(R.id.btnReports).setOnClickListener(v -> {
            Intent intent = new Intent(
                    StudentAdmin.this,
                    Reports.class
            );
            startActivity(intent);
        });

        // Header Logout Button
        if (findViewById(R.id.notificationButton) != null) {
            findViewById(R.id.notificationButton).setOnClickListener(v -> showLogoutDialog());
        }

        // Sidebar Navigation
        View menuButton = findViewById(R.id.moreButton);
        if (menuButton != null) {
            menuButton.setOnClickListener(v -> showSidebar());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDynamicDashboardData();
    }

    private void loadDynamicDashboardData() {
        ArrayList<StudentModel> students = repository.getAllStudents();
        int totalStudentCount = students.size();

        if (tvStudentCount != null) {
            tvStudentCount.setText(String.valueOf(totalStudentCount));
        }

        SimpleDateFormat dateKeyFmt = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());
        String todayKeyStr = dateKeyFmt.format(new Date());

        SharedPreferences todayPrefs = getSharedPreferences("TodayActivityPrefs", MODE_PRIVATE);
        SharedPreferences attendancePrefs = getSharedPreferences("AttendanceStore", MODE_PRIVATE);

        int submittedTodayCount = 0;
        int presentCount = 0;

        for (StudentModel s : students) {
            String regNo = s.getRegisterNumber();
            boolean isSubmitted = todayPrefs.getBoolean("submitted_" + todayKeyStr, false);
            if (isSubmitted) {
                submittedTodayCount++;
            }

            String status = attendancePrefs.getString("status_" + regNo + "_" + todayKeyStr, "Present");
            if ("Present".equalsIgnoreCase(status)) {
                presentCount++;
            }
        }

        int pendingCount = totalStudentCount > 0 ? (totalStudentCount - submittedTodayCount) : 0;
        if (pendingCount < 0) pendingCount = 0;

        int attendancePercentage = totalStudentCount > 0 ? (int) Math.round(((double) presentCount / totalStudentCount) * 100) : 100;

        if (tvAttendance != null) {
            tvAttendance.setText(attendancePercentage + "%");
        }

        if (tvProgressCount != null) {
            tvProgressCount.setText(String.valueOf(submittedTodayCount));
        }

        if (tvPending != null) {
            tvPending.setText(String.valueOf(pendingCount));
        }
    }

    private void showSidebar() {
        View sidebarView = LayoutInflater.from(StudentAdmin.this)
                .inflate(R.layout.layout_student_sidebar, null);

        PopupWindow sidebar = new PopupWindow(
                sidebarView,
                (int) (280 * getResources().getDisplayMetrics().density),
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );

        sidebar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        sidebar.setOutsideTouchable(true);
        sidebar.setElevation(20);
        sidebar.setClippingEnabled(false);

        if (android.os.Build.VERSION.SDK_INT >= 29) {
            sidebar.setIsClippedToScreen(false);
            sidebar.setIsLaidOutInScreen(true);
        }

        sidebar.showAtLocation(
                getWindow().getDecorView(),
                Gravity.START | Gravity.TOP,
                0,
                0
        );

        sidebarView.findViewById(R.id.menuDashboard).setOnClickListener(item -> sidebar.dismiss());
        sidebarView.findViewById(R.id.menuStudents).setOnClickListener(item -> {
            startActivity(new Intent(StudentAdmin.this, MyStudents.class));
            sidebar.dismiss();
        });

        sidebarView.findViewById(R.id.menuProgress).setOnClickListener(item -> {
            startActivity(new Intent(StudentAdmin.this, DailyProgress.class));
            sidebar.dismiss();
        });

        sidebarView.findViewById(R.id.menuAttendance).setOnClickListener(item -> {
            startActivity(new Intent(StudentAdmin.this, MarkAttendance.class));
            sidebar.dismiss();
        });

        if (sidebarView.findViewById(R.id.menuProgressHistory) != null) {
            sidebarView.findViewById(R.id.menuProgressHistory).setOnClickListener(item -> {
                startActivity(new Intent(StudentAdmin.this, ProgressHistory.class));
                sidebar.dismiss();
            });
        }

        sidebarView.findViewById(R.id.menuReports).setOnClickListener(item -> {
            startActivity(new Intent(StudentAdmin.this, Reports.class));
            sidebar.dismiss();
        });

        sidebarView.findViewById(R.id.menuProfile).setOnClickListener(item -> {
            startActivity(new Intent(StudentAdmin.this, MyProfile.class));
            sidebar.dismiss();
        });

        sidebarView.findViewById(R.id.menuLogout).setOnClickListener(item -> {
            sidebar.dismiss();
            showLogoutDialog();
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(StudentAdmin.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
