package com.example.trackpoint;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Locale;

public class StaffProgressHistory extends AppCompatActivity {

    ImageView btnBack;
    TextView txtHeaderTitle;
    EditText searchSubmission;
    Spinner spHistAdmin;
    TextView btnCustomDate;
    LinearLayout historyListContainer;

    StudentRepository repository;
    int purple = Color.parseColor("#4935B8");

    Calendar filterCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    String selectedDateStr = "";
    String preFilterStudentId = null;
    String preFilterStudentName = null;
    String filterAdminName = null;

    static class SubmissionModel {
        String studentName, regId, dept, year, assignedAdmin;
        String formattedDate, dateLabel, notes, fileType, fileName, fileSize;
        boolean isSubmitted;

        SubmissionModel(String studentName, String regId, String dept, String year,
                        String assignedAdmin, String formattedDate, String dateLabel,
                        String notes, String fileType, String fileName, String fileSize,
                        boolean isSubmitted) {
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
            this.isSubmitted = isSubmitted;
        }
    }

    ArrayList<SubmissionModel> allSubmissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(purple);
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_staff_progress_history);

        repository = new StudentRepository(this);

        btnBack = findViewById(R.id.btnBack);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        searchSubmission = findViewById(R.id.searchSubmission);
        spHistAdmin = findViewById(R.id.spHistAdmin);
        btnCustomDate = findViewById(R.id.btnCustomDate);
        historyListContainer = findViewById(R.id.historyListContainer);

        btnBack.setOnClickListener(v -> finish());

        // Setup Student Admin Spinner
        ArrayList<StudentAdminModel> adminList = repository.getAllStudentAdmins();
        ArrayList<String> adminNames = new ArrayList<>();
        adminNames.add("All Admins");
        if (adminList.isEmpty()) {
            adminNames.add("Aathi");
        } else {
            for (StudentAdminModel sa : adminList) {
                adminNames.add(sa.getName());
            }
        }
        setupSpinner(spHistAdmin, adminNames.toArray(new String[0]));

        // Check for Intent Extras
        boolean weeklyOnly = getIntent().getBooleanExtra("weeklyOnly", false);
        boolean dailyAdminMode = getIntent().getBooleanExtra("dailyAdminMode", false);
        filterAdminName = getIntent().getStringExtra("adminName");
        preFilterStudentId = getIntent().getStringExtra("studentId");
        preFilterStudentName = getIntent().getStringExtra("studentName");

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
            spHistAdmin.setVisibility(View.GONE);
            selectedDateStr = todayFormatted;
            btnCustomDate.setText(todayFormatted);
        } else {
            selectedDateStr = todayFormatted;
            btnCustomDate.setText(todayFormatted);
        }

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSubmissionsFromDatabase();
        applyFilters();
    }

    private void loadSubmissionsFromDatabase() {
        allSubmissions.clear();

        SimpleDateFormat keyFmt = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());
        String todayKeyStr = keyFmt.format(filterCalendar.getTime());
        String todayFormatted = sdf.format(filterCalendar.getTime());

        SharedPreferences prefs = getSharedPreferences("TodayActivityPrefs", MODE_PRIVATE);

        if (preFilterStudentId != null && !preFilterStudentId.isEmpty()) {
            StudentModel s = repository.getStudentByRegNo(preFilterStudentId);
            if (s != null) {
                DailyActivityModel dbModel = repository.getDailyActivity(s.getRegisterNumber(), todayKeyStr);
                boolean isSub = (dbModel != null) || prefs.getBoolean("submitted_" + s.getRegisterNumber() + "_" + todayKeyStr, false);

                String notes = dbModel != null && dbModel.getNotes() != null && !dbModel.getNotes().isEmpty() ? dbModel.getNotes() : prefs.getString("notes_" + s.getRegisterNumber() + "_" + todayKeyStr, "No activity document or description submitted for this date.");
                String fileName = dbModel != null && dbModel.getFileName() != null && !dbModel.getFileName().isEmpty() ? dbModel.getFileName() : prefs.getString("file_name_" + s.getRegisterNumber() + "_" + todayKeyStr, "No File Uploaded");
                String submitTime = dbModel != null && dbModel.getSubmitTime() != null && !dbModel.getSubmitTime().isEmpty() ? dbModel.getSubmitTime() : prefs.getString("submit_time_" + s.getRegisterNumber() + "_" + todayKeyStr, "Not Submitted");
                String admin = s.getAssignedAdmin() != null ? s.getAssignedAdmin() : "Aathi";

                String fileType = isSub && !fileName.equals("No File Uploaded") ? (fileName.endsWith(".png") || fileName.endsWith(".jpg") ? "Image" : "PDF") : "None";

                allSubmissions.add(new SubmissionModel(
                        s.getName(), s.getRegisterNumber(), s.getDepartment(), s.getYear(), admin,
                        todayFormatted, isSub ? submitTime : "Not Submitted",
                        notes,
                        fileType, isSub ? fileName : "No File Uploaded", "1.2 MB", isSub
                ));
            }
            return;
        }

        if (preFilterStudentName != null && !preFilterStudentName.isEmpty()) {
            ArrayList<StudentModel> students = repository.getAllStudents();
            for (StudentModel s : students) {
                if (s.getName().equalsIgnoreCase(preFilterStudentName)) {
                    DailyActivityModel dbModel = repository.getDailyActivity(s.getRegisterNumber(), todayKeyStr);
                    boolean isSub = (dbModel != null) || prefs.getBoolean("submitted_" + s.getRegisterNumber() + "_" + todayKeyStr, false);

                    String notes = dbModel != null && dbModel.getNotes() != null && !dbModel.getNotes().isEmpty() ? dbModel.getNotes() : prefs.getString("notes_" + s.getRegisterNumber() + "_" + todayKeyStr, "No activity document or description submitted for this date.");
                    String fileName = dbModel != null && dbModel.getFileName() != null && !dbModel.getFileName().isEmpty() ? dbModel.getFileName() : prefs.getString("file_name_" + s.getRegisterNumber() + "_" + todayKeyStr, "No File Uploaded");
                    String submitTime = dbModel != null && dbModel.getSubmitTime() != null && !dbModel.getSubmitTime().isEmpty() ? dbModel.getSubmitTime() : prefs.getString("submit_time_" + s.getRegisterNumber() + "_" + todayKeyStr, "Not Submitted");
                    String admin = s.getAssignedAdmin() != null ? s.getAssignedAdmin() : "Aathi";

                    String fileType = isSub && !fileName.equals("No File Uploaded") ? (fileName.endsWith(".png") || fileName.endsWith(".jpg") ? "Image" : "PDF") : "None";

                    allSubmissions.add(new SubmissionModel(
                            s.getName(), s.getRegisterNumber(), s.getDepartment(), s.getYear(), admin,
                            todayFormatted, isSub ? submitTime : "Not Submitted",
                            notes,
                            fileType, isSub ? fileName : "No File Uploaded", "1.2 MB", isSub
                    ));
                    break;
                }
            }
            return;
        }

        ArrayList<StudentModel> students = repository.searchAndFilterStudentsForAdmin("", "All Departments", "All Years", filterAdminName);
        for (StudentModel s : students) {
            DailyActivityModel dbModel = repository.getDailyActivity(s.getRegisterNumber(), todayKeyStr);
            boolean isSub = (dbModel != null) || prefs.getBoolean("submitted_" + s.getRegisterNumber() + "_" + todayKeyStr, false);

            String notes = dbModel != null && dbModel.getNotes() != null && !dbModel.getNotes().isEmpty() ? dbModel.getNotes() : prefs.getString("notes_" + s.getRegisterNumber() + "_" + todayKeyStr, "No activity document or description submitted for this date.");
            String fileName = dbModel != null && dbModel.getFileName() != null && !dbModel.getFileName().isEmpty() ? dbModel.getFileName() : prefs.getString("file_name_" + s.getRegisterNumber() + "_" + todayKeyStr, "No File Uploaded");
            String submitTime = dbModel != null && dbModel.getSubmitTime() != null && !dbModel.getSubmitTime().isEmpty() ? dbModel.getSubmitTime() : prefs.getString("submit_time_" + s.getRegisterNumber() + "_" + todayKeyStr, "Not Submitted");
            String admin = s.getAssignedAdmin() != null ? s.getAssignedAdmin() : "Aathi";

            String fileType = isSub && !fileName.equals("No File Uploaded") ? (fileName.endsWith(".png") || fileName.endsWith(".jpg") ? "Image" : "PDF") : "None";

            allSubmissions.add(new SubmissionModel(
                    s.getName(), s.getRegisterNumber(), s.getDepartment(), s.getYear(), admin,
                    todayFormatted, isSub ? submitTime : "Not Submitted",
                    notes,
                    fileType, isSub ? fileName : "No File Uploaded", "1.2 MB", isSub
            ));
        }
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void applyFilters() {
        if (historyListContainer == null) return;
        historyListContainer.removeAllViews();

        String query = searchSubmission.getText().toString().toLowerCase().trim();

        if (allSubmissions.isEmpty()) {
            TextView emptyView = new TextView(this);
            if (preFilterStudentName != null && !preFilterStudentName.isEmpty()) {
                emptyView.setText("No submissions found for " + preFilterStudentName);
            } else if (filterAdminName != null && !filterAdminName.isEmpty()) {
                emptyView.setText("No submissions for " + filterAdminName + "'s assigned students");
            } else {
                emptyView.setText("No submissions found in database");
            }
            emptyView.setPadding(16, 32, 16, 32);
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setTextColor(Color.parseColor("#68738A"));
            historyListContainer.addView(emptyView);
            return;
        }

        for (SubmissionModel sub : allSubmissions) {
            if (preFilterStudentId != null && !preFilterStudentId.isEmpty() && !sub.regId.equalsIgnoreCase(preFilterStudentId)) {
                continue;
            }

            if (preFilterStudentName != null && !preFilterStudentName.isEmpty() && !sub.studentName.equalsIgnoreCase(preFilterStudentName)) {
                continue;
            }

            if (!query.isEmpty()
                    && !sub.studentName.toLowerCase().contains(query)
                    && !sub.regId.toLowerCase().contains(query)) {
                continue;
            }

            addSubmissionCard(sub);
        }
    }

    private void addSubmissionCard(SubmissionModel sub) {
        LinearLayout card = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(12));
        card.setLayoutParams(params);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.action_card);
        card.setElevation(dpToPx(2));
        card.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));

        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);

        ImageView avatar = new ImageView(this);
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(dpToPx(40), dpToPx(40));
        avatar.setLayoutParams(avatarParams);
        avatar.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        avatar.setImageResource(R.drawable.profile_symbol);
        avatar.setColorFilter(purple);
        avatar.setBackgroundResource(R.drawable.card_purple);

        LinearLayout infoCol = new LinearLayout(this);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        infoCol.setLayoutParams(infoParams);
        infoCol.setOrientation(LinearLayout.VERTICAL);
        infoCol.setPadding(dpToPx(10), 0, 0, 0);

        TextView tvName = new TextView(this);
        tvName.setText(sub.studentName);
        tvName.setTypeface(null, Typeface.BOLD);
        tvName.setTextSize(15);
        tvName.setTextColor(Color.parseColor("#18233F"));

        TextView tvSub = new TextView(this);
        tvSub.setText(sub.regId + " • " + sub.dept + " (" + sub.year + ")");
        tvSub.setTextSize(12);
        tvSub.setTextColor(Color.parseColor("#68738A"));

        infoCol.addView(tvName);
        infoCol.addView(tvSub);

        TextView tvDate = new TextView(this);
        tvDate.setText(sub.dateLabel);
        tvDate.setTextSize(11);
        tvDate.setTextColor(Color.parseColor("#68738A"));

        headerRow.addView(avatar);
        headerRow.addView(infoCol);
        headerRow.addView(tvDate);

        TextView tvNotes = new TextView(this);
        tvNotes.setText(sub.notes);
        tvNotes.setTextSize(13);
        tvNotes.setTextColor(Color.parseColor("#18233F"));
        tvNotes.setPadding(0, dpToPx(10), 0, dpToPx(10));

        card.addView(headerRow);
        card.addView(tvNotes);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(StaffProgressHistory.this, StaffSubmissionDetails.class);
            intent.putExtra("studentName", sub.studentName);
            intent.putExtra("studentId", sub.regId);
            intent.putExtra("department", sub.dept);
            intent.putExtra("year", sub.year);
            intent.putExtra("assignedAdmin", sub.assignedAdmin);
            intent.putExtra("submittedDate", sub.dateLabel);
            intent.putExtra("notes", sub.notes);
            intent.putExtra("fileName", sub.fileName);
            intent.putExtra("fileSize", sub.fileSize);
            intent.putExtra("fileType", sub.fileType);
            intent.putExtra("isSubmitted", sub.isSubmitted);
            startActivity(intent);
        });

        historyListContainer.addView(card);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
