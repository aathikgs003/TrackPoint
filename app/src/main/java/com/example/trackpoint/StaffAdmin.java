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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class StaffAdmin extends AppCompatActivity {

    TextView quickAdmins;

    LinearLayout navHome, navAdmins, navStudents, navReports;

    ImageView iconHome, iconAdmins, iconStudents, iconReports;

    TextView textHome, textAdmins, textStudents, textReports;

    int purple = Color.parseColor("#4825B8");
    int grey = Color.parseColor("#888888");

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

        // Bottom Navigation Layouts
        navHome = findViewById(R.id.navHome);
        navAdmins = findViewById(R.id.navAdmins);
        navStudents = findViewById(R.id.navStudents);
        navReports = findViewById(R.id.navReports);

        // Bottom Navigation Icons
        iconHome = findViewById(R.id.iconHome);
        iconAdmins = findViewById(R.id.iconAdmins);
        iconStudents = findViewById(R.id.iconStudents);
        iconReports = findViewById(R.id.iconReports);

        // Bottom Navigation Labels
        textHome = findViewById(R.id.textHome);
        textAdmins = findViewById(R.id.textAdmins);
        textStudents = findViewById(R.id.textStudents);
        textReports = findViewById(R.id.textReports);

        // Highlight Home as the active page
        setActiveNavigation();

        // Top Header - Notifications
        findViewById(R.id.btnStaffNotifications).setOnClickListener(v -> {
            startActivity(new Intent(StaffAdmin.this, StaffNotifications.class));
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

            sidebarView.findViewById(R.id.menuStaffNotifications).setOnClickListener(item -> {
                startActivity(new Intent(StaffAdmin.this, StaffNotifications.class));
                sidebar.dismiss();
            });

            sidebarView.findViewById(R.id.menuStaffProfile).setOnClickListener(item -> {
                Toast.makeText(StaffAdmin.this, "Staff Admin: Academic Staff", Toast.LENGTH_SHORT).show();
                sidebar.dismiss();
            });

            sidebarView.findViewById(R.id.menuStaffLogout).setOnClickListener(item -> {
                sidebar.dismiss();
                finish();
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

        // Bottom Navigation Clicks
        navHome.setOnClickListener(v -> {});

        navAdmins.setOnClickListener(v -> {
            startActivity(new Intent(StaffAdmin.this, StudentAdmins.class));
        });

        navStudents.setOnClickListener(v -> {
            startActivity(new Intent(StaffAdmin.this, StaffAdminStudents.class));
        });

        navReports.setOnClickListener(v -> {
            startActivity(new Intent(StaffAdmin.this, StaffReports.class));
        });
    }

    private void setActiveNavigation() {
        iconHome.setColorFilter(purple);
        textHome.setTextColor(purple);

        iconAdmins.setColorFilter(grey);
        textAdmins.setTextColor(grey);

        iconStudents.setColorFilter(grey);
        textStudents.setTextColor(grey);

        iconReports.setColorFilter(grey);
        textReports.setTextColor(grey);
    }
}