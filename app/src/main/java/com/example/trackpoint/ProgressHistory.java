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
import java.util.Calendar;
import java.util.Locale;

public class ProgressHistory extends AppCompatActivity {

    ImageView btnBack;
    Spinner spStudent;
    TextView tvFilterDate, tvProgressResultsTitle;
    LinearLayout layoutProgressContent;

    Calendar filterCalendar;
    String[] studentList = {
            "All Students",
            "Arun Kumar",
            "Divya S",
            "Karthik R",
            "Nithya P",
            "Praveen M"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_history);

        btnBack = findViewById(R.id.btnBack);
        spStudent = findViewById(R.id.spStudent);
        tvFilterDate = findViewById(R.id.tvFilterDate);
        tvProgressResultsTitle = findViewById(R.id.tvProgressResultsTitle);
        layoutProgressContent = findViewById(R.id.layoutProgressContent);

        btnBack.setOnClickListener(v -> finish());

        setSpinner(spStudent, studentList);

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

        String selectedStudent = spStudent.getSelectedItem() != null ? spStudent.getSelectedItem().toString() : "All Students";

        if (tvProgressResultsTitle != null) {
            tvProgressResultsTitle.setText("Uploads for " + dateDisplay);
        }

        SharedPreferences prefs = getSharedPreferences("TodayActivityPrefs", MODE_PRIVATE);
        boolean isArunSubmitted = prefs.getBoolean("submitted_" + dateKey, false);
        String arunFileName = prefs.getString("file_name_" + dateKey, "assignment_java.pdf");
        String arunNotes = prefs.getString("notes_" + dateKey, "Java activity & loops completed.");
        String arunTime = prefs.getString("submit_time_" + dateKey, "Submitted at 10:30 AM");

        if ("All Students".equals(selectedStudent)) {
            // Render for Arun Kumar (with real SharedPreferences data)
            if (isArunSubmitted) {
                layoutProgressContent.addView(createStudentUploadCard("Arun Kumar", dateDisplay, arunFileName, arunNotes, arunTime, true));
            } else {
                layoutProgressContent.addView(createStudentUploadCard("Arun Kumar", dateDisplay, "No File Uploaded", "No activity document or description submitted for this date.", "Not Submitted", false));
            }

            // Render for Divya S
            layoutProgressContent.addView(createStudentUploadCard("Divya S", dateDisplay, "dbms_notes.pdf", "DBMS Normalization assignment notes submitted.", "Submitted at 11:15 AM", true));

            // Render for Karthik R
            layoutProgressContent.addView(createStudentUploadCard("Karthik R", dateDisplay, "No File Uploaded", "No activity document or description submitted for this date.", "Not Submitted", false));

            // Render for Nithya P
            layoutProgressContent.addView(createStudentUploadCard("Nithya P", dateDisplay, "ui_design_figma.png", "Mobile App UI Design screenshots uploaded.", "Submitted at 09:45 AM", true));

            // Render for Praveen M
            layoutProgressContent.addView(createStudentUploadCard("Praveen M", dateDisplay, "python_lab_report.pdf", "Python Data Structures lab report completed.", "Submitted at 02:10 PM", true));

        } else if ("Arun Kumar".equals(selectedStudent)) {
            if (isArunSubmitted) {
                layoutProgressContent.addView(createStudentUploadCard("Arun Kumar", dateDisplay, arunFileName, arunNotes, arunTime, true));
            } else {
                layoutProgressContent.addView(createStudentUploadCard("Arun Kumar", dateDisplay, "No File Uploaded", "No activity document or description submitted for this date.", "Not Submitted", false));
            }
        } else if ("Divya S".equals(selectedStudent)) {
            layoutProgressContent.addView(createStudentUploadCard("Divya S", dateDisplay, "dbms_notes.pdf", "DBMS Normalization assignment notes submitted.", "Submitted at 11:15 AM", true));
        } else if ("Nithya P".equals(selectedStudent)) {
            layoutProgressContent.addView(createStudentUploadCard("Nithya P", dateDisplay, "ui_design_figma.png", "Mobile App UI Design screenshots uploaded.", "Submitted at 09:45 AM", true));
        } else if ("Praveen M".equals(selectedStudent)) {
            layoutProgressContent.addView(createStudentUploadCard("Praveen M", dateDisplay, "python_lab_report.pdf", "Python Data Structures lab report completed.", "Submitted at 02:10 PM", true));
        } else {
            layoutProgressContent.addView(createStudentUploadCard(selectedStudent, dateDisplay, "No File Uploaded", "No activity document or description submitted for this date.", "Not Submitted", false));
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
