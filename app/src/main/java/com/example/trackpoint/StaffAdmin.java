package com.example.trackpoint;

import android.content.Intent;
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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StaffAdmin extends AppCompatActivity {

    TextView quickAdmins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Staff Admin status bar color
        getWindow().setStatusBarColor(Color.parseColor("#4825B8"));

        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_staff_admin);

        // Quick Access
        quickAdmins = findViewById(R.id.quickAdmins);

        TextView txtDashboardDate = findViewById(R.id.txtDashboardDate);
        if (txtDashboardDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("'Today, 'MMM dd, yyyy", Locale.US);
            txtDashboardDate.setText(sdf.format(new Date()));
        }

        // Top Header - Logout
        findViewById(R.id.btnStaffHeaderLogout).setOnClickListener(v -> {
            showLogoutDialog();
        });

        // Top Header - Side Menu
        findViewById(R.id.btnStaffMenu).setOnClickListener(v -> {
            View sidebarView = LayoutInflater.from(StaffAdmin.this)
                    .inflate(R.layout.layout_staff_sidebar, null);

            PopupWindow sidebar = new PopupWindow(
                    sidebarView,
                    (int) (280 * getResources().getDisplayMetrics().density),
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    true
            );

            sidebar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            sidebar.setOutsideTouchable(true);
            sidebar.setElevation(20);

            sidebar.showAtLocation(
                    findViewById(android.R.id.content),
                    Gravity.START,
                    0,
                    0
            );

            sidebarView.findViewById(R.id.menuStaffDashboard).setOnClickListener(item -> sidebar.dismiss());

            sidebarView.findViewById(R.id.menuStaffAdmins).setOnClickListener(item -> {
                startActivity(new Intent(StaffAdmin.this, StudentAdmins.class));
                sidebar.dismiss();
            });

            sidebarView.findViewById(R.id.menuStaffStudents).setOnClickListener(item -> {
                startActivity(new Intent(StaffAdmin.this, StaffAdminStudents.class));
                sidebar.dismiss();
            });

            sidebarView.findViewById(R.id.menuStaffReports).setOnClickListener(item -> {
                startActivity(new Intent(StaffAdmin.this, StaffReports.class));
                sidebar.dismiss();
            });

            sidebarView.findViewById(R.id.menuStaffProfile).setOnClickListener(item -> {
                Toast.makeText(StaffAdmin.this, "Staff Admin: Academic Staff", Toast.LENGTH_SHORT).show();
                sidebar.dismiss();
            });

            sidebarView.findViewById(R.id.menuStaffLogout).setOnClickListener(item -> {
                sidebar.dismiss();
                showLogoutDialog();
            });
        });

        // Quick Access - Student Admins
        quickAdmins.setOnClickListener(v -> {
            startActivity(new Intent(StaffAdmin.this, StudentAdmins.class));
        });

        // Quick Access - Students
        TextView quickStudents = findViewById(R.id.quickStudents);
        if (quickStudents != null) {
            quickStudents.setOnClickListener(v -> {
                startActivity(new Intent(StaffAdmin.this, StaffAdminStudents.class));
            });
        }

        // Quick Access - Progress History
        TextView quickProgressHistory = findViewById(R.id.quickProgressHistory);
        if (quickProgressHistory != null) {
            quickProgressHistory.setOnClickListener(v -> {
                startActivity(new Intent(StaffAdmin.this, StaffProgressHistory.class));
            });
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(StaffAdmin.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}