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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminDepartments extends AppCompatActivity {

    ImageView btnBack;
    EditText searchDepartment;
    LinearLayout deptListContainer;
    Button btnAddDepartment;

    int purple = Color.parseColor("#4935B8");

    static class DeptModel {
        String code, fullName;
        int staffAdminCount, studentAdminCount, studentCount;

        DeptModel(String code, String fullName, int staffAdminCount, int studentAdminCount, int studentCount) {
            this.code = code;
            this.fullName = fullName;
            this.staffAdminCount = staffAdminCount;
            this.studentAdminCount = studentAdminCount;
            this.studentCount = studentCount;
        }
    }

    List<DeptModel> departments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(purple);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_super_admin_departments);

        btnBack = findViewById(R.id.btnBack);
        searchDepartment = findViewById(R.id.searchDepartment);
        deptListContainer = findViewById(R.id.deptListContainer);
        btnAddDepartment = findViewById(R.id.btnAddDepartment);

        btnBack.setOnClickListener(v -> finish());

        departments.add(new DeptModel("CSE", "Computer Science & Engineering", 2, 4, 96));
        departments.add(new DeptModel("AI & DS", "Artificial Intelligence & Data Science", 2, 3, 68));
        departments.add(new DeptModel("ECE", "Electronics & Communication", 2, 4, 82));
        departments.add(new DeptModel("IT", "Information Technology", 1, 3, 76));
        departments.add(new DeptModel("MECH", "Mechanical Engineering", 1, 2, 50));

        renderDepartments("");

        searchDepartment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                renderDepartments(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAddDepartment.setOnClickListener(v -> showAddDepartmentDialog());
    }

    private void showAddDepartmentDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(8));

        EditText edtCode = new EditText(this);
        edtCode.setHint("Department Code (e.g., CIVIL)");
        layout.addView(edtCode);

        EditText edtName = new EditText(this);
        edtName.setHint("Full Name (e.g., Civil Engineering)");
        layout.addView(edtName);

        new AlertDialog.Builder(this)
                .setTitle("Add New Department")
                .setView(layout)
                .setPositiveButton("Add", (dialog, which) -> {
                    String code = edtCode.getText().toString().trim().toUpperCase();
                    String name = edtName.getText().toString().trim();
                    if (code.isEmpty() || name.isEmpty()) {
                        Toast.makeText(this, "Enter both code and name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    departments.add(new DeptModel(code, name, 0, 0, 0));
                    renderDepartments(searchDepartment.getText().toString().trim());
                    Toast.makeText(this, code + " Department Added", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void renderDepartments(String query) {
        deptListContainer.removeAllViews();
        String q = query.toLowerCase();

        for (DeptModel d : departments) {
            if (!q.isEmpty() && !d.code.toLowerCase().contains(q) && !d.fullName.toLowerCase().contains(q)) {
                continue;
            }

            LinearLayout card = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, dpToPx(10));
            card.setLayoutParams(params);
            card.setOrientation(LinearLayout.HORIZONTAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));
            card.setBackgroundResource(R.drawable.action_card);
            card.setElevation(2f);

            TextView badge = new TextView(this);
            badge.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(56), dpToPx(46)));
            badge.setGravity(Gravity.CENTER);
            badge.setText(d.code);
            badge.setTextColor(purple);
            badge.setTextSize(13);
            badge.setTypeface(null, Typeface.BOLD);
            badge.setBackgroundResource(R.drawable.card_purple);

            LinearLayout info = new LinearLayout(this);
            info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            info.setOrientation(LinearLayout.VERTICAL);
            info.setPadding(dpToPx(12), 0, 0, 0);

            TextView tvTitle = new TextView(this);
            tvTitle.setText(d.fullName);
            tvTitle.setTextSize(15);
            tvTitle.setTypeface(null, Typeface.BOLD);
            tvTitle.setTextColor(Color.parseColor("#18233F"));

            TextView tvCounts = new TextView(this);
            tvCounts.setText("Staff Admins: " + d.staffAdminCount + "  •  Student Admins: " + d.studentAdminCount);
            tvCounts.setTextSize(12);
            tvCounts.setTextColor(Color.parseColor("#68738A"));

            info.addView(tvTitle);
            info.addView(tvCounts);

            TextView arrow = new TextView(this);
            arrow.setText("›");
            arrow.setTextSize(26);
            arrow.setTextColor(purple);

            card.addView(badge);
            card.addView(info);
            card.addView(arrow);

            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, SuperAdminDepartmentDetails.class);
                intent.putExtra("deptCode", d.code);
                intent.putExtra("deptName", d.fullName);
                intent.putExtra("staffCount", d.staffAdminCount);
                intent.putExtra("studentAdminCount", d.studentAdminCount);
                intent.putExtra("studentCount", d.studentCount);
                startActivity(intent);
            });

            deptListContainer.addView(card);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}