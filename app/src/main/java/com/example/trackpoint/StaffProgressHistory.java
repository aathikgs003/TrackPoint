package com.example.trackpoint;

import android.app.DatePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StaffProgressHistory extends AppCompatActivity {

    ImageView btnBack;
    TextView txtHeaderTitle;
    EditText searchSubmission;
    Spinner spHistAdmin;
    TextView btnCustomDate;
    LinearLayout historyListContainer;

    int purple = Color.parseColor("#4825B8");

    Calendar filterCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    String selectedDateStr = "";
    String preFilterStudentId = null;

    static class SubmissionModel {
        String studentName, regId, dept, year, assignedAdmin;
        String formattedDate, dateLabel, notes, fileType, fileName, fileSize;
        boolean isThisWeek;

        SubmissionModel(String studentName, String regId, String dept, String year,
                        String assignedAdmin, String formattedDate, String dateLabel,
                        String notes, String fileType, String fileName, String fileSize,
                        boolean isThisWeek) {
            this.studentName = studentName;
            this.regId = regId;
            this.dept = dept;
            this.year = year;
            this.assignedAdmin = assignedAdmin;
            this.formattedDate = formattedDate;
            this.dateLabel = dateLabel;
            this.notes = notes;
            this.fileType = fileType;
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.isThisWeek = isThisWeek;
        }
    }

    static class RosterStudent {
        String name, regId, dept, year, admin;
        RosterStudent(String name, String regId, String dept, String year, String admin) {
            this.name = name;
            this.regId = regId;
            this.dept = dept;
            this.year = year;
            this.admin = admin;
        }
    }

    private List<RosterStudent> getStudentsForAdmin(String adminName) {
        List<RosterStudent> list = new ArrayList<>();
        if (adminName.equalsIgnoreCase("Arun Kumar")) {
            list.add(new RosterStudent("Aishu M", "CSE22CS001", "CSE", "II Year", "Arun Kumar"));
            list.add(new RosterStudent("Bala K", "CSE22CS002", "CSE", "II Year", "Arun Kumar"));
        } else if (adminName.equalsIgnoreCase("Divya S")) {
            list.add(new RosterStudent("Dharani R", "AIDS21AD014", "AI & DS", "III Year", "Divya S"));
        } else if (adminName.equalsIgnoreCase("Karthik R")) {
            list.add(new RosterStudent("Gokul S", "ECE20EC029", "ECE", "IV Year", "Karthik R"));
        } else if (adminName.equalsIgnoreCase("Nithya P")) {
            list.add(new RosterStudent("Harini V", "IT22IT018", "IT", "II Year", "Nithya P"));
        }
        return list;
    }

    List<SubmissionModel> allSubmissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(purple);
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_staff_progress_history);

        btnBack = findViewById(R.id.btnBack);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        searchSubmission = findViewById(R.id.searchSubmission);
        spHistAdmin = findViewById(R.id.spHistAdmin);
        btnCustomDate = findViewById(R.id.btnCustomDate);
        historyListContainer = findViewById(R.id.historyListContainer);

        btnBack.setOnClickListener(v -> finish());

        // Setup Student Admin Spinner
        setupSpinner(spHistAdmin, new String[]{"All Admins", "Arun Kumar", "Divya S", "Karthik R", "Nithya P"});

        // Check for Intent Extras
        boolean weeklyOnly = getIntent().getBooleanExtra("weeklyOnly", false);
        boolean dailyAdminMode = getIntent().getBooleanExtra("dailyAdminMode", false);
        String filterAdminName = getIntent().getStringExtra("adminName");
        preFilterStudentId = getIntent().getStringExtra("studentId");
        String preFilterStudentName = getIntent().getStringExtra("studentName");

        String todayFormatted = sdf.format(filterCalendar.getTime());

        if (dailyAdminMode && filterAdminName != null && !filterAdminName.isEmpty()) {
            txtHeaderTitle.setText(filterAdminName + " • Daily Progress");
            spHistAdmin.setVisibility(View.GONE);
            selectedDateStr = todayFormatted;
            btnCustomDate.setText(todayFormatted);
        } else if (weeklyOnly && preFilterStudentName != null && !preFilterStudentName.isEmpty()) {
            txtHeaderTitle.setText(preFilterStudentName + " • This Week");
            spHistAdmin.setVisibility(View.GONE);
            btnCustomDate.setText("This Week (Last 7 Days)");
            selectedDateStr = "";
        } else if (preFilterStudentName != null && !preFilterStudentName.isEmpty()) {
            txtHeaderTitle.setText(preFilterStudentName + " - History");
            selectedDateStr = todayFormatted;
            btnCustomDate.setText(todayFormatted);
        } else {
            selectedDateStr = todayFormatted;
            btnCustomDate.setText(todayFormatted);
        }

        // Populate sample data (including entries for each student within this week)
        allSubmissions.clear();
        allSubmissions.add(new SubmissionModel(
                "Aishu M", "CSE22CS001", "CSE", "II Year", "Arun Kumar",
                todayFormatted, "Today, 10:15 AM",
                "Implemented dynamic live filters in Android interface and tested Module 2 layout.",
                "PDF", "daily_record.pdf", "1.2 MB", true
        ));
        allSubmissions.add(new SubmissionModel(
                "Bala K", "CSE22CS002", "CSE", "II Year", "Arun Kumar",
                todayFormatted, "Today, 09:12 AM",
                "Completed Module 3 UI testing and verified database schema fields.",
                "Notes", "Module 3 UI testing summary", "Text Note", true
        ));
        allSubmissions.add(new SubmissionModel(
                "Dharani R", "AIDS21AD014", "AI & DS", "III Year", "Divya S",
                "Jan 25, 2025", "Jan 25, 2025, 04:30 PM",
                "Trained classification model and captured confusion matrix output screen.",
                "Image", "output_screen.png", "840 KB", false
        ));
        allSubmissions.add(new SubmissionModel(
                "Gokul S", "ECE20EC029", "ECE", "IV Year", "Karthik R",
                todayFormatted, "Today, 02:15 PM",
                "Uploaded IoT sensor calibration log and circuit schematic documentation.",
                "PDF", "sensor_calibration.pdf", "2.1 MB", true
        ));
        allSubmissions.add(new SubmissionModel(
                "Harini V", "IT22IT018", "IT", "II Year", "Nithya P",
                todayFormatted, "Today, 11:40 AM",
                "Uploaded responsive dashboard wireframe screenshot for review.",
                "Image", "dashboard_wireframe.png", "620 KB", true
        ));

        spHistAdmin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        searchSubmission.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Custom Date Selector Button Click
        btnCustomDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        filterCalendar.set(Calendar.YEAR, year);
                        filterCalendar.set(Calendar.MONTH, month);
                        filterCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        selectedDateStr = sdf.format(filterCalendar.getTime());
                        btnCustomDate.setText(selectedDateStr);
                        applyFilters();
                    },
                    filterCalendar.get(Calendar.YEAR),
                    filterCalendar.get(Calendar.MONTH),
                    filterCalendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });

        applyFilters();
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void applyFilters() {
        historyListContainer.removeAllViews();

        boolean weeklyOnly = getIntent().getBooleanExtra("weeklyOnly", false);
        boolean dailyAdminMode = getIntent().getBooleanExtra("dailyAdminMode", false);
        String filterAdminName = getIntent().getStringExtra("adminName");
        String admin = spHistAdmin.getSelectedItem() != null ? spHistAdmin.getSelectedItem().toString() : "All Admins";
        String query = searchSubmission.getText().toString().toLowerCase().trim();

        if (dailyAdminMode && filterAdminName != null && !filterAdminName.isEmpty()) {
            List<RosterStudent> roster = getStudentsForAdmin(filterAdminName);
            for (RosterStudent student : roster) {
                if (!query.isEmpty()
                        && !student.name.toLowerCase().contains(query)
                        && !student.regId.toLowerCase().contains(query)) {
                    continue;
                }

                SubmissionModel matchedSub = null;
                for (SubmissionModel item : allSubmissions) {
                    if (item.regId.equalsIgnoreCase(student.regId)) {
                        if (selectedDateStr == null || selectedDateStr.isEmpty() ||
                                item.formattedDate.equalsIgnoreCase(selectedDateStr) ||
                                item.dateLabel.toLowerCase().contains(selectedDateStr.toLowerCase())) {
                            matchedSub = item;
                            break;
                        }
                    }
                }

                if (matchedSub != null) {
                    addSubmissionCard(matchedSub, true);
                } else {
                    addNotSubmittedCard(student);
                }
            }
            return;
        }

        for (SubmissionModel item : allSubmissions) {
            if (preFilterStudentId != null && !preFilterStudentId.isEmpty()
                    && !item.regId.equalsIgnoreCase(preFilterStudentId)) {
                continue;
            }

            if (weeklyOnly) {
                if (selectedDateStr != null && !selectedDateStr.isEmpty() && !selectedDateStr.equals("This Week (Last 7 Days)")) {
                    if (!item.formattedDate.equalsIgnoreCase(selectedDateStr)
                            && !item.dateLabel.toLowerCase().contains(selectedDateStr.toLowerCase())) {
                        continue;
                    }
                } else {
                    if (!item.isThisWeek) {
                        continue;
                    }
                }
            } else {
                if (spHistAdmin.getVisibility() == View.VISIBLE && !admin.equals("All Admins") && !item.assignedAdmin.equalsIgnoreCase(admin)) {
                    continue;
                }

                if (selectedDateStr != null && !selectedDateStr.isEmpty()) {
                    if (!item.formattedDate.equalsIgnoreCase(selectedDateStr)
                            && !item.dateLabel.toLowerCase().contains(selectedDateStr.toLowerCase())) {
                        continue;
                    }
                }
            }

            if (!query.isEmpty()
                    && !item.studentName.toLowerCase().contains(query)
                    && !item.regId.toLowerCase().contains(query)
                    && !item.notes.toLowerCase().contains(query)) {
                continue;
            }

            addSubmissionCard(item, false);
        }
    }

    private void addSubmissionCard(SubmissionModel item, boolean showSubmittedBadge) {
        LinearLayout card = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(10));
        card.setLayoutParams(params);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));
        card.setBackgroundResource(R.drawable.action_card);
        card.setElevation(2f);

        // Top Row: Avatar + Student Info + Arrow
        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        ImageView avatar = new ImageView(this);
        avatar.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(42), dpToPx(42)));
        avatar.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        avatar.setImageResource(R.drawable.profile_symbol);
        avatar.setColorFilter(purple);
        avatar.setBackgroundColor(Color.parseColor("#E2D5FF"));

        LinearLayout infoCol = new LinearLayout(this);
        infoCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        infoCol.setOrientation(LinearLayout.VERTICAL);
        infoCol.setPadding(dpToPx(12), 0, 0, 0);

        TextView tvName = new TextView(this);
        tvName.setText(item.studentName + " (" + item.regId + ")");
        tvName.setTypeface(null, Typeface.BOLD);
        tvName.setTextSize(15);
        tvName.setTextColor(Color.parseColor("#17112D"));

        TextView tvMeta = new TextView(this);
        tvMeta.setText(item.dept + " • " + item.year + "  |  Admin: " + item.assignedAdmin);
        tvMeta.setTextSize(12);
        tvMeta.setTextColor(Color.parseColor("#555555"));

        TextView tvTime = new TextView(this);
        tvTime.setText("Submitted: " + item.dateLabel);
        tvTime.setTextSize(11);
        tvTime.setTextColor(Color.parseColor("#777777"));

        infoCol.addView(tvName);
        infoCol.addView(tvMeta);
        infoCol.addView(tvTime);

        TextView arrow = new TextView(this);
        arrow.setText("›");
        arrow.setTextSize(26);
        arrow.setTextColor(purple);

        topRow.addView(avatar);
        topRow.addView(infoCol);
        topRow.addView(arrow);

        card.addView(topRow);

        if (showSubmittedBadge) {
            TextView submittedBadge = new TextView(this);
            LinearLayout.LayoutParams sbParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            sbParams.setMargins(0, dpToPx(10), 0, 0);
            submittedBadge.setLayoutParams(sbParams);
            submittedBadge.setPadding(dpToPx(10), dpToPx(5), dpToPx(10), dpToPx(5));
            submittedBadge.setTextSize(12);
            submittedBadge.setTypeface(null, Typeface.BOLD);
            submittedBadge.setText("Submitted ✓");
            submittedBadge.setTextColor(Color.parseColor("#168B43"));
            submittedBadge.setBackgroundColor(Color.parseColor("#E9F9EF"));
            card.addView(submittedBadge);
        }

        // Attachment / Note Pill Badge
        TextView badge = new TextView(this);
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        badgeParams.setMargins(0, dpToPx(8), 0, 0);
        badge.setLayoutParams(badgeParams);
        badge.setPadding(dpToPx(10), dpToPx(5), dpToPx(10), dpToPx(5));
        badge.setTextSize(12);
        badge.setTypeface(null, Typeface.BOLD);

        if (item.fileType.equals("PDF")) {
            badge.setText("PDF: " + item.fileName);
            badge.setTextColor(Color.parseColor("#C62828"));
            badge.setBackgroundColor(Color.parseColor("#FFEBEE"));
        } else if (item.fileType.equals("Image")) {
            badge.setText("Image: " + item.fileName);
            badge.setTextColor(Color.parseColor("#1565C0"));
            badge.setBackgroundColor(Color.parseColor("#E3F2FD"));
        } else {
            badge.setText("Notes: " + item.fileName);
            badge.setTextColor(purple);
            badge.setBackgroundColor(Color.parseColor("#F0EAFE"));
        }

        card.addView(badge);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(StaffProgressHistory.this, StaffSubmissionDetails.class);
            intent.putExtra("studentName", item.studentName);
            intent.putExtra("regId", item.regId);
            intent.putExtra("dept", item.dept);
            intent.putExtra("year", item.year);
            intent.putExtra("assignedAdmin", item.assignedAdmin);
            intent.putExtra("dateLabel", item.dateLabel);
            intent.putExtra("notes", item.notes);
            intent.putExtra("fileType", item.fileType);
            intent.putExtra("fileName", item.fileName);
            intent.putExtra("fileSize", item.fileSize);
            startActivity(intent);
        });

        historyListContainer.addView(card);
    }

    private void addNotSubmittedCard(RosterStudent student) {
        LinearLayout card = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(10));
        card.setLayoutParams(params);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));
        card.setBackgroundResource(R.drawable.action_card);
        card.setElevation(2f);

        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        ImageView avatar = new ImageView(this);
        avatar.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(42), dpToPx(42)));
        avatar.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        avatar.setImageResource(R.drawable.profile_symbol);
        avatar.setColorFilter(purple);
        avatar.setBackgroundColor(Color.parseColor("#E2D5FF"));

        LinearLayout infoCol = new LinearLayout(this);
        infoCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        infoCol.setOrientation(LinearLayout.VERTICAL);
        infoCol.setPadding(dpToPx(12), 0, 0, 0);

        TextView tvName = new TextView(this);
        tvName.setText(student.name + " (" + student.regId + ")");
        tvName.setTypeface(null, Typeface.BOLD);
        tvName.setTextSize(15);
        tvName.setTextColor(Color.parseColor("#17112D"));

        TextView tvMeta = new TextView(this);
        tvMeta.setText(student.dept + " • " + student.year + "  |  Admin: " + student.admin);
        tvMeta.setTextSize(12);
        tvMeta.setTextColor(Color.parseColor("#555555"));

        TextView tvTime = new TextView(this);
        tvTime.setText("No submission recorded for this date");
        tvTime.setTextSize(11);
        tvTime.setTextColor(Color.parseColor("#D94343"));

        infoCol.addView(tvName);
        infoCol.addView(tvMeta);
        infoCol.addView(tvTime);

        topRow.addView(avatar);
        topRow.addView(infoCol);

        // Status Badge: Not Submitted
        TextView badge = new TextView(this);
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        badgeParams.setMargins(0, dpToPx(10), 0, 0);
        badge.setLayoutParams(badgeParams);
        badge.setPadding(dpToPx(10), dpToPx(5), dpToPx(10), dpToPx(5));
        badge.setTextSize(12);
        badge.setTypeface(null, Typeface.BOLD);
        badge.setText("Not Submitted");
        badge.setTextColor(Color.parseColor("#D94343"));
        badge.setBackgroundColor(Color.parseColor("#FDECEC"));

        card.addView(topRow);
        card.addView(badge);

        historyListContainer.addView(card);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}