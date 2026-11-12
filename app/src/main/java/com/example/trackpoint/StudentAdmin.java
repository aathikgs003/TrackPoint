package com.example.trackpoint;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class StudentAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_admin_dashboard);

        // My Students Navigation (Action Card & Bottom Nav)
        View.OnClickListener openStudents = v -> startActivity(new Intent(StudentAdmin.this, MyStudents.class));
        findViewById(R.id.btnMyStudents).setOnClickListener(openStudents);

        // Daily Progress Navigation (Action Card & Bottom Nav)
        View.OnClickListener openProgress = v -> startActivity(new Intent(StudentAdmin.this, DailyProgress.class));
        findViewById(R.id.btnDailyProgress).setOnClickListener(openProgress);

        // Attendance Navigation (Action Card & Bottom Nav)
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

        menuButton.setOnClickListener(v -> {

            View sidebarView = LayoutInflater.from(StudentAdmin.this)
                    .inflate(R.layout.layout_student_sidebar, null);

            PopupWindow sidebar = new PopupWindow(
                    sidebarView,
                    (int) (280 * getResources().getDisplayMetrics().density),
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    true
            );

            sidebar.setBackgroundDrawable(
                    new ColorDrawable(Color.WHITE)
            );

            sidebar.setOutsideTouchable(true);
            sidebar.setElevation(20);

            // Allow sidebar to extend to the screen edges
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

            // Dashboard
            sidebarView.findViewById(R.id.menuDashboard)
                    .setOnClickListener(item -> {
                        sidebar.dismiss();
                    });

            // My Students
            sidebarView.findViewById(R.id.menuStudents)
                    .setOnClickListener(item -> {
                        startActivity(new Intent(
                                StudentAdmin.this,
                                MyStudents.class
                        ));
                        sidebar.dismiss();
                    });

            // Daily Progress
            sidebarView.findViewById(R.id.menuProgress)
                    .setOnClickListener(item -> {
                        startActivity(new Intent(
                                StudentAdmin.this,
                                DailyProgress.class
                        ));
                        sidebar.dismiss();
                    });

            // Attendance
            sidebarView.findViewById(R.id.menuAttendance)
                    .setOnClickListener(item -> {
                        startActivity(new Intent(
                                StudentAdmin.this,
                                MarkAttendance.class
                        ));
                        sidebar.dismiss();
                    });

            // Progress History
            if (sidebarView.findViewById(R.id.menuProgressHistory) != null) {
                sidebarView.findViewById(R.id.menuProgressHistory)
                        .setOnClickListener(item -> {
                            startActivity(new Intent(
                                    StudentAdmin.this,
                                    ProgressHistory.class
                            ));
                            sidebar.dismiss();
                        });
            }

            // Reports
            sidebarView.findViewById(R.id.menuReports)
                    .setOnClickListener(item -> {
                        startActivity(new Intent(
                                StudentAdmin.this,
                                Reports.class
                        ));
                        sidebar.dismiss();
                    });

            // My Profile - Screen 11
            sidebarView.findViewById(R.id.menuProfile)
                    .setOnClickListener(item -> {
                        startActivity(new Intent(
                                StudentAdmin.this,
                                MyProfile.class
                        ));
                        sidebar.dismiss();
                    });

            // Logout
            sidebarView.findViewById(R.id.menuLogout)
                    .setOnClickListener(item -> {
                        sidebar.dismiss();
                        showLogoutDialog();
                    });
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
