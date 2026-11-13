package com.example.trackpoint;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SuperAdminDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_super_admin);

        TextView txtSuperDashboardDate = findViewById(R.id.txtSuperDashboardDate);
        SimpleDateFormat sdf = new SimpleDateFormat("'Today, 'MMM dd, yyyy", Locale.US);
        txtSuperDashboardDate.setText(sdf.format(new Date()));

        findViewById(R.id.btnSuperHeaderLogout).setOnClickListener(v -> showLogoutDialog());

        // Sidebar Menu
        findViewById(R.id.btnSuperMenu).setOnClickListener(v -> {
            View sidebarView = LayoutInflater.from(SuperAdminDashboard.this)
                    .inflate(R.layout.layout_super_admin_sidebar, null);

            PopupWindow sidebar = new PopupWindow(
                    sidebarView,
                    (int) (280 * getResources().getDisplayMetrics().density),
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    true
            );

            sidebar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            sidebar.setOutsideTouchable(true);
            sidebar.setElevation(20);
            sidebar.showAtLocation(findViewById(android.R.id.content), Gravity.START, 0, 0);

            sidebarView.findViewById(R.id.menuSuperDashboard).setOnClickListener(item -> sidebar.dismiss());

            sidebarView.findViewById(R.id.menuSuperDepartments).setOnClickListener(item -> {
                startActivity(new Intent(SuperAdminDashboard.this, SuperAdminDepartments.class));
                sidebar.dismiss();
            });

            sidebarView.findViewById(R.id.menuSuperStaffAdmins).setOnClickListener(item -> {
                startActivity(new Intent(SuperAdminDashboard.this, SuperAdminStaffAdmins.class));
                sidebar.dismiss();
            });

            sidebarView.findViewById(R.id.menuSuperStudentAdmins).setOnClickListener(item -> {
                startActivity(new Intent(SuperAdminDashboard.this, SuperAdminStudentAdmins.class));
                sidebar.dismiss();
            });

            View menuStudents = sidebarView.findViewById(R.id.menuSuperStudents);
            if (menuStudents != null) {
                menuStudents.setOnClickListener(item -> {
                    startActivity(new Intent(SuperAdminDashboard.this, SuperAdminStudents.class));
                    sidebar.dismiss();
                });
            }

            View headerProfile = sidebarView.findViewById(R.id.headerSuperProfile);
            if (headerProfile != null) {
                headerProfile.setOnClickListener(item -> {
                    startActivity(new Intent(SuperAdminDashboard.this, SuperAdminProfileActivity.class));
                    sidebar.dismiss();
                });
            }

            View menuProfile = sidebarView.findViewById(R.id.menuSuperProfile);
            if (menuProfile != null) {
                menuProfile.setOnClickListener(item -> {
                    startActivity(new Intent(SuperAdminDashboard.this, SuperAdminProfileActivity.class));
                    sidebar.dismiss();
                });
            }

            sidebarView.findViewById(R.id.menuSuperLogout).setOnClickListener(item -> {
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
                    Intent intent = new Intent(SuperAdminDashboard.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}