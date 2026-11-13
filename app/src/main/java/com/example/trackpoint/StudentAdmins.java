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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.util.ArrayList;

public class StudentAdmins extends AppCompatActivity {

    ImageView btnBack;
    EditText searchAdmin;
    LinearLayout adminListContainer;
    Button btnAddAdmin;

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

        setContentView(R.layout.activity_student_admins);

        repository = new StudentRepository(this);

        btnBack = findViewById(R.id.btnBack);
        searchAdmin = findViewById(R.id.searchAdmin);
        adminListContainer = findViewById(R.id.adminListContainer);
        btnAddAdmin = findViewById(R.id.btnAddAdmin);

        btnBack.setOnClickListener(v -> finish());

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
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderAdminCards(searchAdmin.getText().toString().trim());
    }

    private void renderAdminCards(String query) {
        if (adminListContainer == null) return;
        adminListContainer.removeAllViews();

        ArrayList<StudentAdminModel> admins = repository.getAllStudentAdmins();
        String q = query.toLowerCase();

        if (admins.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No Student Admins found in database");
            emptyView.setPadding(16, 32, 16, 32);
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setTextColor(Color.parseColor("#68738A"));
            adminListContainer.addView(emptyView);
            return;
        }

        for (StudentAdminModel admin : admins) {
            if (!q.isEmpty()
                    && !admin.getName().toLowerCase().contains(q)
                    && !admin.getAdminId().toLowerCase().contains(q)
                    && !admin.getDepartment().toLowerCase().contains(q)) {
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
            card.setBackgroundResource(R.drawable.action_card);
            card.setElevation(dpToPx(2));

            ImageView avatar = new ImageView(this);
            LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(dpToPx(45), dpToPx(45));
            avatar.setLayoutParams(avatarParams);
            avatar.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
            avatar.setImageResource(R.drawable.profile_symbol);
            avatar.setColorFilter(purple);
            avatar.setBackgroundResource(R.drawable.card_purple);

            LinearLayout infoCol = new LinearLayout(this);
            LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            );
            infoCol.setLayoutParams(infoParams);
            infoCol.setOrientation(LinearLayout.VERTICAL);
            infoCol.setPadding(dpToPx(12), 0, 0, 0);

            TextView tvName = new TextView(this);
            tvName.setText(admin.getName());
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextSize(16);
            tvName.setTextColor(Color.parseColor("#18233F"));

            TextView tvId = new TextView(this);
            tvId.setText(admin.getAdminId() + " • " + admin.getDepartment() + " • Students: " + admin.getStudentsAssignedCount());
            tvId.setTextSize(13);
            tvId.setTextColor(Color.parseColor("#68738A"));

            infoCol.addView(tvName);
            infoCol.addView(tvId);

            TextView arrow = new TextView(this);
            arrow.setText("›");
            arrow.setTextSize(26);
            arrow.setTextColor(Color.parseColor("#68738A"));

            card.addView(avatar);
            card.addView(infoCol);
            card.addView(arrow);

            card.setOnClickListener(v -> {
                Intent intent = new Intent(StudentAdmins.this, StudentAdminDetails.class);
                intent.putExtra("adminName", admin.getName());
                intent.putExtra("adminId", admin.getAdminId());
                intent.putExtra("department", admin.getDepartment());
                intent.putExtra("year", admin.getYear());
                intent.putExtra("phone", admin.getPhone());
                intent.putExtra("email", admin.getEmail());
                intent.putExtra("studentsAssigned", String.valueOf(admin.getStudentsAssignedCount()));
                startActivity(intent);
            });

            adminListContainer.addView(card);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
