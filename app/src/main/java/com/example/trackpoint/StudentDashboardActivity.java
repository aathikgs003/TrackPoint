package com.example.trackpoint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudentDashboardActivity extends AppCompatActivity {

    TextView tvTodayDate, tvDashboardStatusTitle, tvDashboardSubmitTime, tvDashboardFileName;
    ImageView imgDashboardStatusIcon;
    LinearLayout cardTodayActivityStatus, layoutDashboardStatusBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // Header Logout Button
        findViewById(R.id.notificationButton).setOnClickListener(v -> showLogoutDialog());

        // Summary Cards - No redirect on click per user request
        View cardAttendance = findViewById(R.id.cardAttendance);
        View cardCompleted = findViewById(R.id.cardCompleted);
        View cardInProgress = findViewById(R.id.cardInProgress);
        View cardTotalTasks = findViewById(R.id.cardTotalTasks);

        if (cardAttendance != null) cardAttendance.setOnClickListener(null);
        if (cardCompleted != null) cardCompleted.setOnClickListener(null);
        if (cardInProgress != null) cardInProgress.setOnClickListener(null);
        if (cardTotalTasks != null) cardTotalTasks.setOnClickListener(null);

        // Today's Activity Views
        tvTodayDate = findViewById(R.id.tvTodayDate);
        tvDashboardStatusTitle = findViewById(R.id.tvDashboardStatusTitle);
        tvDashboardSubmitTime = findViewById(R.id.tvDashboardSubmitTime);
        tvDashboardFileName = findViewById(R.id.tvDashboardFileName);

        imgDashboardStatusIcon = findViewById(R.id.imgDashboardStatusIcon);
        cardTodayActivityStatus = findViewById(R.id.cardTodayActivityStatus);
        layoutDashboardStatusBadge = findViewById(R.id.layoutDashboardStatusBadge);

        // Sidebar Navigation
        findViewById(R.id.moreButton).setOnClickListener(v -> showSidebar());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTodayActivityStatus();
    }

    private void loadTodayActivityStatus() {
        SimpleDateFormat displayDateFmt = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        SimpleDateFormat dateKeyFmt = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());

        Date today = new Date();
        String dateDisplayStr = displayDateFmt.format(today);
        String dateKeyStr = dateKeyFmt.format(today);

        if (tvTodayDate != null) {
            tvTodayDate.setText(dateDisplayStr);
        }

        SharedPreferences prefs = getSharedPreferences("TodayActivityPrefs", MODE_PRIVATE);
        boolean isSubmitted = prefs.getBoolean("submitted_" + dateKeyStr, false);
        String fileName = prefs.getString("file_name_" + dateKeyStr, "Document Uploaded");
        String submitTime = prefs.getString("submit_time_" + dateKeyStr, "Submitted Today");

        if (isSubmitted) {
            if (tvDashboardStatusTitle != null) {
                tvDashboardStatusTitle.setText("Submitted Today");
                tvDashboardStatusTitle.setTextColor(Color.parseColor("#10B23E"));
            }
            if (imgDashboardStatusIcon != null) {
                imgDashboardStatusIcon.setImageResource(R.drawable.check_circle_symbol);
                imgDashboardStatusIcon.setColorFilter(Color.parseColor("#10B23E"));
            }
            if (layoutDashboardStatusBadge != null) {
                layoutDashboardStatusBadge.setBackgroundResource(R.drawable.card_green);
            }
            if (tvDashboardFileName != null) {
                tvDashboardFileName.setText("📄 " + fileName);
                tvDashboardFileName.setTextColor(Color.parseColor("#18233F"));
            }
            if (tvDashboardSubmitTime != null) {
                tvDashboardSubmitTime.setText(submitTime);
            }
        } else {
            if (tvDashboardStatusTitle != null) {
                tvDashboardStatusTitle.setText("Not Submitted Today");
                tvDashboardStatusTitle.setTextColor(Color.parseColor("#D97706"));
            }
            if (imgDashboardStatusIcon != null) {
                imgDashboardStatusIcon.setImageResource(R.drawable.pending_symbol);
                imgDashboardStatusIcon.setColorFilter(Color.parseColor("#D97706"));
            }
            if (layoutDashboardStatusBadge != null) {
                layoutDashboardStatusBadge.setBackgroundResource(R.drawable.card_yellow);
            }
            if (tvDashboardFileName != null) {
                tvDashboardFileName.setText("⚠️ You haven't submitted today's activity yet.");
                tvDashboardFileName.setTextColor(Color.parseColor("#68738A"));
            }
            if (tvDashboardSubmitTime != null) {
                tvDashboardSubmitTime.setText("Tap to submit");
            }
        }

        if (cardTodayActivityStatus != null) {
            cardTodayActivityStatus.setOnClickListener(v ->
                    startActivity(new Intent(this, StudentTodayActivity.class))
            );
        }
    }

    private void showSidebar() {
        View sidebarView = LayoutInflater.from(this)
                .inflate(R.layout.layout_user_student_sidebar, null);

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

        sidebarView.findViewById(R.id.menuHome).setOnClickListener(v -> sidebar.dismiss());
        sidebarView.findViewById(R.id.menuTodayActivity).setOnClickListener(v -> {
            sidebar.dismiss();
            startActivity(new Intent(this, StudentTodayActivity.class));
        });
        sidebarView.findViewById(R.id.menuAttendance).setOnClickListener(v -> {
            sidebar.dismiss();
            startActivity(new Intent(this, StudentAttendanceActivity.class));
        });
        sidebarView.findViewById(R.id.menuReports).setOnClickListener(v -> {
            sidebar.dismiss();
            startActivity(new Intent(this, StudentReportsActivity.class));
        });
        sidebarView.findViewById(R.id.menuProfile).setOnClickListener(v -> {
            sidebar.dismiss();
            startActivity(new Intent(this, StudentProfileActivity.class));
        });
        sidebarView.findViewById(R.id.menuLogout).setOnClickListener(v -> {
            sidebar.dismiss();
            showLogoutDialog();
        });
    }

    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(StudentDashboardActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
