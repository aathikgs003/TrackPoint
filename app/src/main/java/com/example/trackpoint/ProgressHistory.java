package com.example.trackpoint;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ProgressHistory extends AppCompatActivity {

    ImageView btnBack;
    Spinner spStudent;
    TextView tvFilterDate, tvProgressResultsTitle;
    LinearLayout layoutProgressContent;

    Calendar filterCalendar;
    StudentRepository repository;
    String loggedInAdminName = "Aathi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_history);

        repository = new StudentRepository(this);

        SharedPreferences sessionPrefs = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
        loggedInAdminName = sessionPrefs.getString("logged_in_admin_name", "Aathi");

        btnBack = findViewById(R.id.btnBack);
        spStudent = findViewById(R.id.spStudent);
        tvFilterDate = findViewById(R.id.tvFilterDate);
        tvProgressResultsTitle = findViewById(R.id.tvProgressResultsTitle);
        layoutProgressContent = findViewById(R.id.layoutProgressContent);

        btnBack.setOnClickListener(v -> finish());

        ArrayList<StudentModel> assignedStudents = repository.searchAndFilterStudentsForAdmin("", "All Departments", "All Years", loggedInAdminName);
        ArrayList<String> studentNames = new ArrayList<>();
        studentNames.add("All Assigned Students");
        for (StudentModel s : assignedStudents) {
            studentNames.add(s.getName());
        }

        setSpinner(spStudent, studentNames.toArray(new String[0]));

        filterCalendar = Calendar.getInstance();
        updateDateDisplay();

        tvFilterDate.setOnClickListener(v -> showDatePicker());

        spStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renderProgressResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        renderProgressResults();
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderProgressResults();
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        if (tvFilterDate != null) {
            tvFilterDate.setText("📅   " + sdf.format(filterCalendar.getTime()));
        }
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    filterCalendar.set(Calendar.YEAR, year);
                    filterCalendar.set(Calendar.MONTH, month);
                    filterCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateDisplay();
                    renderProgressResults();
                },
                filterCalendar.get(Calendar.YEAR),
                filterCalendar.get(Calendar.MONTH),
                filterCalendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void renderProgressResults() {
        if (layoutProgressContent == null) return;
        layoutProgressContent.removeAllViews();

        SimpleDateFormat keyFmt = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());
        SimpleDateFormat displayFmt = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());

        String dateKey = keyFmt.format(filterCalendar.getTime());
        String dateDisplay = displayFmt.format(filterCalendar.getTime());

        if (tvProgressResultsTitle != null) {
            tvProgressResultsTitle.setText("Uploads for " + dateDisplay);
        }

        String selectedStudent = spStudent.getSelectedItem() != null ? spStudent.getSelectedItem().toString() : "All Assigned Students";
        ArrayList<StudentModel> assignedStudents = repository.searchAndFilterStudentsForAdmin("", "All Departments", "All Years", loggedInAdminName);

        if (assignedStudents.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No students assigned to " + loggedInAdminName + " in database");
            emptyView.setPadding(16, 32, 16, 32);
            emptyView.setGravity(android.view.Gravity.CENTER);
            emptyView.setTextColor(android.graphics.Color.parseColor("#68738A"));
            layoutProgressContent.addView(emptyView);
            return;
        }

        SharedPreferences prefs = getSharedPreferences("TodayActivityPrefs", MODE_PRIVATE);
        int renderedCount = 0;

        for (StudentModel s : assignedStudents) {
            if (!selectedStudent.equals("All Assigned Students") && !s.getName().equalsIgnoreCase(selectedStudent)) {
                continue;
            }

            DailyActivityModel dbModel = repository.getDailyActivity(s.getRegisterNumber(), dateKey);
            boolean isSubmitted = (dbModel != null) || prefs.getBoolean("submitted_" + s.getRegisterNumber() + "_" + dateKey, false);

            String fileName = dbModel != null ? dbModel.getFileName() : prefs.getString("file_name_" + s.getRegisterNumber() + "_" + dateKey, "No File Uploaded");
            String notes = dbModel != null ? dbModel.getNotes() : prefs.getString("notes_" + s.getRegisterNumber() + "_" + dateKey, "No activity document or description submitted for this date.");
            String timeStr = dbModel != null ? dbModel.getSubmitTime() : prefs.getString("submit_time_" + s.getRegisterNumber() + "_" + dateKey, "Not Submitted");

            if (isSubmitted) {
                layoutProgressContent.addView(createStudentUploadCard(s.getName(), dateDisplay, fileName, notes, timeStr, true));
            } else {
                layoutProgressContent.addView(createStudentUploadCard(s.getName(), dateDisplay, "No File Uploaded", "No activity document or description submitted for this date.", "Not Submitted", false));
            }
            renderedCount++;
        }

        if (renderedCount == 0) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No progress records found for selected filter");
            emptyView.setPadding(16, 32, 16, 32);
            emptyView.setGravity(android.view.Gravity.CENTER);
            emptyView.setTextColor(android.graphics.Color.parseColor("#68738A"));
            layoutProgressContent.addView(emptyView);
        }
    }

    private View createStudentUploadCard(String studentName, String dateStr, String fileName, String notesStr, String timeStr, boolean isSubmitted) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_report_card, layoutProgressContent, false);

        TextView tvDate = card.findViewById(R.id.tvReportCardDate);
        TextView tvTime = card.findViewById(R.id.tvReportCardTime);

        LinearLayout layoutBadge = card.findViewById(R.id.layoutReportBadge);
        ImageView imgBadgeIcon = card.findViewById(R.id.imgReportBadgeIcon);
        TextView tvBadgeText = card.findViewById(R.id.tvReportBadgeText);

        TextView tvDocName = card.findViewById(R.id.tvReportDocName);
        TextView tvNotes = card.findViewById(R.id.tvReportNotes);

        if (tvDate != null) tvDate.setText("👤 " + studentName + "   (" + dateStr + ")");
        if (tvTime != null) tvTime.setText(timeStr);

        if (isSubmitted) {
            if (layoutBadge != null) layoutBadge.setBackgroundResource(R.drawable.card_green);
            if (imgBadgeIcon != null) {
                imgBadgeIcon.setImageResource(R.drawable.check_circle_symbol);
                imgBadgeIcon.setColorFilter(Color.parseColor("#10B23E"));
            }
            if (tvBadgeText != null) {
                tvBadgeText.setText("Verified Processing");
                tvBadgeText.setTextColor(Color.parseColor("#10B23E"));
            }
            if (tvDocName != null) {
                tvDocName.setText("📄 " + fileName);
                tvDocName.setTextColor(Color.parseColor("#18233F"));
            }
        } else {
            if (layoutBadge != null) layoutBadge.setBackgroundResource(R.drawable.card_yellow);
            if (imgBadgeIcon != null) {
                imgBadgeIcon.setImageResource(R.drawable.pending_symbol);
                imgBadgeIcon.setColorFilter(Color.parseColor("#D97706"));
            }
            if (tvBadgeText != null) {
                tvBadgeText.setText("Not Submitted");
                tvBadgeText.setTextColor(Color.parseColor("#D97706"));
            }
            if (tvDocName != null) {
                tvDocName.setText("⚠️ " + fileName);
                tvDocName.setTextColor(Color.parseColor("#68738A"));
            }
        }

        if (tvNotes != null) {
            tvNotes.setText(notesStr.isEmpty() ? "No description provided" : notesStr);
        }

        return card;
    }

    private void setSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                items
        );

        adapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }
}
