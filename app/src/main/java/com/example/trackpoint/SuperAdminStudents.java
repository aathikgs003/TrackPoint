package com.example.trackpoint;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminStudents extends AppCompatActivity {

    ImageView btnBack;
    EditText searchSuperStudent;
    Spinner spSuperStudentDept, spSuperStudentYear;
    LinearLayout superStudentListContainer;

    int purple = Color.parseColor("#4935B8");

    static class CollegeStudentModel {
        String name, regId, department, year, assignedAdmin, phone, email, attendance, progress, tasks;

        CollegeStudentModel(String name, String regId, String department, String year,
                            String assignedAdmin, String phone, String email,
                            String attendance, String progress, String tasks) {
            this.name = name;
            this.regId = regId;
            this.department = department;
            this.year = year;
            this.assignedAdmin = assignedAdmin;
            this.phone = phone;
            this.email = email;
            this.attendance = attendance;
            this.progress = progress;
            this.tasks = tasks;
        }
    }

    List<CollegeStudentModel> students = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(purple);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_super_admin_students);

        btnBack = findViewById(R.id.btnBack);
        searchSuperStudent = findViewById(R.id.searchSuperStudent);
        spSuperStudentDept = findViewById(R.id.spSuperStudentDept);
        spSuperStudentYear = findViewById(R.id.spSuperStudentYear);
        superStudentListContainer = findViewById(R.id.superStudentListContainer);

        btnBack.setOnClickListener(v -> finish());

        setupSpinner(spSuperStudentDept, new String[]{"All Departments", "CSE", "AI & DS", "ECE", "IT", "MECH"});
        setupSpinner(spSuperStudentYear, new String[]{"All Years", "II Year", "III Year", "IV Year"});

        students.add(new CollegeStudentModel("Aishu M", "CSE22CS001", "CSE", "II Year", "Arun Kumar", "98765 43210", "aishu@college.edu", "92%", "88%", "5"));
        students.add(new CollegeStudentModel("Bala K", "CSE22CS002", "CSE", "II Year", "Arun Kumar", "98765 43211", "bala@college.edu", "89%", "84%", "4"));
        students.add(new CollegeStudentModel("Dharani R", "AIDS21AD014", "AI & DS", "III Year", "Divya S", "98765 43212", "dharani@college.edu", "94%", "91%", "6"));
        students.add(new CollegeStudentModel("Gokul S", "ECE20EC029", "ECE", "IV Year", "Karthik R", "98765 43213", "gokul@college.edu", "86%", "85%", "5"));
        students.add(new CollegeStudentModel("Harini V", "IT22IT018", "IT", "II Year", "Nithya P", "98765 43214", "harini@college.edu", "90%", "87%", "5"));

        renderList();

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renderList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spSuperStudentDept.setOnItemSelectedListener(listener);
        spSuperStudentYear.setOnItemSelectedListener(listener);

        searchSuperStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                renderList();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void renderList() {
        superStudentListContainer.removeAllViews();
        String dept = spSuperStudentDept.getSelectedItem().toString();
        String year = spSuperStudentYear.getSelectedItem().toString();
        String query = searchSuperStudent.getText().toString().toLowerCase().trim();

        for (CollegeStudentModel s : students) {
            if (!dept.equals("All Departments") && !s.department.equalsIgnoreCase(dept)) continue;
            if (!year.equals("All Years") && !s.year.equalsIgnoreCase(year)) continue;
            if (!query.isEmpty() && !s.name.toLowerCase().contains(query) && !s.regId.toLowerCase().contains(query)) continue;

            LinearLayout card = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, dpToPx(10));
            card.setLayoutParams(params);
            card.setOrientation(LinearLayout.HORIZONTAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));
            card.setBackgroundResource(R.drawable.action_card);
            card.setElevation(dpToPx(2));

            ImageView avatar = new ImageView(this);
            avatar.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(44), dpToPx(44)));
            avatar.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
            avatar.setImageResource(R.drawable.profile_symbol);
            avatar.setColorFilter(purple);
            avatar.setBackgroundResource(R.drawable.card_purple);

            LinearLayout info = new LinearLayout(this);
            info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            info.setOrientation(LinearLayout.VERTICAL);
            info.setPadding(dpToPx(12), 0, 0, 0);

            TextView tvName = new TextView(this);
            tvName.setText(s.name);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextSize(16);
            tvName.setTextColor(Color.parseColor("#18233F"));

            TextView tvMeta = new TextView(this);
            tvMeta.setText(s.regId + "  •  " + s.department + " (" + s.year + ")");
            tvMeta.setTextSize(12);
            tvMeta.setTextColor(Color.parseColor("#68738A"));

            TextView tvAdmin = new TextView(this);
            tvAdmin.setText("Student Admin: " + s.assignedAdmin);
            tvAdmin.setTextSize(12);
            tvAdmin.setTextColor(purple);

            info.addView(tvName);
            info.addView(tvMeta);
            info.addView(tvAdmin);

            TextView arrow = new TextView(this);
            arrow.setText("›");
            arrow.setTextSize(27);
            arrow.setTextColor(purple);

            card.addView(avatar);
            card.addView(info);
            card.addView(arrow);

            // Reuse existing StaffStudentDetails screen
            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, StaffStudentDetails.class);
                intent.putExtra("studentName", s.name);
                intent.putExtra("studentId", s.regId);
                intent.putExtra("department", s.department);
                intent.putExtra("year", s.year);
                intent.putExtra("phone", s.phone);
                intent.putExtra("email", s.email);
                intent.putExtra("attendance", s.attendance);
                intent.putExtra("progress", s.progress);
                intent.putExtra("tasks", s.tasks);
                startActivity(intent);
            });

            superStudentListContainer.addView(card);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}