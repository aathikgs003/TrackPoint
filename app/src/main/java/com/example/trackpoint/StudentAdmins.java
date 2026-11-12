package com.example.trackpoint;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.util.ArrayList;
import java.util.List;

public class StudentAdmins extends AppCompatActivity {

    ImageView btnBack;
    EditText searchAdmin;
    LinearLayout adminListContainer;
    Button btnAddAdmin;

    // Bottom Navigation
    LinearLayout navHome, navAdmins, navStudents, navReports;
    ImageView iconHome, iconAdmins, iconStudents, iconReports;
    TextView textHome, textAdmins, textStudents, textReports;

    int purple = Color.parseColor("#4825B8");
    int grey = Color.parseColor("#888888");

    static class AdminModel {
        String name, id;
        int studentCount;

        AdminModel(String name, String id, int studentCount) {
            this.name = name;
            this.id = id;
            this.studentCount = studentCount;
        }
    }

    List<AdminModel> allAdmins = new ArrayList<>();

    ActivityResultLauncher<Intent> addAdminLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String name = result.getData().getStringExtra("adminName");
                    String id = result.getData().getStringExtra("adminId");
                    int count = result.getData().getIntExtra("studentCount", 0);
                    allAdmins.add(new AdminModel(name, id, count));
                    renderAdminCards(searchAdmin.getText().toString().trim());
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4825B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_student_admins);

        btnBack = findViewById(R.id.btnBack);
        searchAdmin = findViewById(R.id.searchAdmin);
        adminListContainer = findViewById(R.id.adminListContainer);
        btnAddAdmin = findViewById(R.id.btnAddAdmin);

        btnBack.setOnClickListener(v -> finish());

        // Initial Student Admins
        allAdmins.add(new AdminModel("Arun Kumar", "SADM001", 24));
        allAdmins.add(new AdminModel("Divya S", "SADM002", 28));
        allAdmins.add(new AdminModel("Karthik R", "SADM003", 22));
        allAdmins.add(new AdminModel("Nithya P", "SADM004", 22));

        renderAdminCards("");

        // Live Search Filter
        searchAdmin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                renderAdminCards(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Open Add Student Admin Form
        btnAddAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(StudentAdmins.this, AddStudentAdmin.class);
            addAdminLauncher.launch(intent);
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
        navAdmins.setOnClickListener(v -> {});
        navStudents.setOnClickListener(v -> startActivity(new Intent(this, StaffAdminStudents.class)));
        navReports.setOnClickListener(v -> {
            startActivity(new Intent(this, StaffReports.class));
        });
    }

    private void renderAdminCards(String query) {
        adminListContainer.removeAllViews();
        String q = query.toLowerCase();

        for (AdminModel admin : allAdmins) {
            if (!q.isEmpty()
                    && !admin.name.toLowerCase().contains(q)
                    && !admin.id.toLowerCase().contains(q)) {
                continue;
            }

            LinearLayout card = new LinearLayout(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, dpToPx(10));
            card.setLayoutParams(cardParams);
            card.setOrientation(LinearLayout.HORIZONTAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));
            card.setBackgroundColor(Color.parseColor("#F3EEFF"));

            ImageView avatar = new ImageView(this);
            LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(dpToPx(45), dpToPx(45));
            avatar.setLayoutParams(avatarParams);
            avatar.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
            avatar.setImageResource(R.drawable.profile_symbol);
            avatar.setColorFilter(purple);
            avatar.setBackgroundColor(Color.parseColor("#E2D5FF"));

            LinearLayout infoCol = new LinearLayout(this);
            LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            );
            infoCol.setLayoutParams(infoParams);
            infoCol.setOrientation(LinearLayout.VERTICAL);
            infoCol.setPadding(dpToPx(12), 0, 0, 0);

            TextView tvName = new TextView(this);
            tvName.setText(admin.name);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextSize(16);
            tvName.setTextColor(Color.parseColor("#17112D"));

            TextView tvId = new TextView(this);
            tvId.setText(admin.id);
            tvId.setTextSize(13);
            tvId.setTextColor(Color.parseColor("#555555"));

            TextView tvCount = new TextView(this);
            tvCount.setText("Students: " + admin.studentCount);
            tvCount.setTextSize(13);
            tvCount.setTextColor(Color.parseColor("#555555"));

            infoCol.addView(tvName);
            infoCol.addView(tvId);
            infoCol.addView(tvCount);

            TextView arrow = new TextView(this);
            arrow.setText("›");
            arrow.setTextSize(27);
            arrow.setTextColor(purple);

            card.addView(avatar);
            card.addView(infoCol);
            card.addView(arrow);

            card.setOnClickListener(v -> {
                Intent intent = new Intent(StudentAdmins.this, StudentAdminDetails.class);
                intent.putExtra("adminName", admin.name);
                intent.putExtra("adminId", admin.id);
                intent.putExtra("studentCount", admin.studentCount);
                startActivity(intent);
            });

            adminListContainer.addView(card);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void setActiveNavigation() {
        iconHome.setColorFilter(grey);
        textHome.setTextColor(grey);

        iconAdmins.setColorFilter(purple);
        textAdmins.setTextColor(purple);

        iconStudents.setColorFilter(grey);
        textStudents.setTextColor(grey);

        iconReports.setColorFilter(grey);
        textReports.setTextColor(grey);
    }
}