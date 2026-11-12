package com.example.trackpoint;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class StudentReportsActivity extends AppCompatActivity {

    ImageView btnBack;
    TextView tvFilterDate, tvReportSectionTitle;
    Button btnFilterSelectedDate, btnShowAllHistory;
    LinearLayout layoutReportContent;

    Calendar filterCalendar;
    boolean isFilterBySingleDate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_reports);

        btnBack = findViewById(R.id.btnBack);
        tvFilterDate = findViewById(R.id.tvFilterDate);
        tvReportSectionTitle = findViewById(R.id.tvReportSectionTitle);

        btnFilterSelectedDate = findViewById(R.id.btnFilterSelectedDate);
        btnShowAllHistory = findViewById(R.id.btnShowAllHistory);

        layoutReportContent = findViewById(R.id.layoutReportContent);

        btnBack.setOnClickListener(v -> finish());

        filterCalendar = Calendar.getInstance();
        updateFilterDateDisplay();

        tvFilterDate.setOnClickListener(v -> showDatePicker());

        btnFilterSelectedDate.setOnClickListener(v -> {
            isFilterBySingleDate = true;
            btnFilterSelectedDate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4935B8")));
            btnFilterSelectedDate.setTextColor(Color.WHITE);

            btnShowAllHistory.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EAE5FF")));
            btnShowAllHistory.setTextColor(Color.parseColor("#4935B8"));

            if (tvReportSectionTitle != null) {
                tvReportSectionTitle.setText("Uploaded Activity Details (Selected Date)");
            }

            renderReport();
        });

        btnShowAllHistory.setOnClickListener(v -> {
            isFilterBySingleDate = false;
            btnShowAllHistory.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4935B8")));
            btnShowAllHistory.setTextColor(Color.WHITE);

            btnFilterSelectedDate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EAE5FF")));
            btnFilterSelectedDate.setTextColor(Color.parseColor("#4935B8"));

            if (tvReportSectionTitle != null) {
                tvReportSectionTitle.setText("Uploaded Activity Details (All Uploads)");
            }

            renderReport();
        });

        renderReport();
    }

    private void updateFilterDateDisplay() {
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
                    updateFilterDateDisplay();
                    isFilterBySingleDate = true;

                    if (btnFilterSelectedDate != null) {
                        btnFilterSelectedDate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4935B8")));
                        btnFilterSelectedDate.setTextColor(Color.WHITE);
                    }
                    if (btnShowAllHistory != null) {
                        btnShowAllHistory.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EAE5FF")));
                        btnShowAllHistory.setTextColor(Color.parseColor("#4935B8"));
                    }

                    renderReport();
                },
                filterCalendar.get(Calendar.YEAR),
                filterCalendar.get(Calendar.MONTH),
                filterCalendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void renderReport() {
        if (layoutReportContent == null) return;
        layoutReportContent.removeAllViews();

        SharedPreferences prefs = getSharedPreferences("TodayActivityPrefs", MODE_PRIVATE);

        if (isFilterBySingleDate) {
            SimpleDateFormat keyFmt = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());
            SimpleDateFormat displayFmt = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());

            String dateKey = keyFmt.format(filterCalendar.getTime());
            String dateDisplay = displayFmt.format(filterCalendar.getTime());

            boolean isSubmitted = prefs.getBoolean("submitted_" + dateKey, false);

            if (isSubmitted) {
                String fileName = prefs.getString("file_name_" + dateKey, "Document Uploaded");
                String notes = prefs.getString("notes_" + dateKey, "No description provided");
                String submitTime = prefs.getString("submit_time_" + dateKey, "Submitted");

                layoutReportContent.addView(createReportCardView(dateDisplay, fileName, notes, submitTime, true));
            } else {
                layoutReportContent.addView(createReportCardView(dateDisplay, "No File Uploaded", "No activity document or description was submitted for this date.", "Not Submitted", false));
            }
        } else {
            // Show all stored history entries
            Map<String, ?> allPrefs = prefs.getAll();
            int count = 0;

            for (Map.Entry<String, ?> entry : allPrefs.entrySet()) {
                if (entry.getKey().startsWith("submitted_") && Boolean.TRUE.equals(entry.getValue())) {
                    String dateKey = entry.getKey().replace("submitted_", "");
                    String fileName = prefs.getString("file_name_" + dateKey, "Document Uploaded");
                    String notes = prefs.getString("notes_" + dateKey, "No description provided");
                    String submitTime = prefs.getString("submit_time_" + dateKey, "Submitted");
                    String dateDisplay = prefs.getString("date_display_" + dateKey, dateKey);

                    layoutReportContent.addView(createReportCardView(dateDisplay, fileName, notes, submitTime, true));
                    count++;
                }
            }

            if (count == 0) {
                layoutReportContent.addView(createReportCardView("All History", "No Submissions Found", "No activity documents have been submitted yet.", "No Data", false));
            }
        }
    }

    private View createReportCardView(String dateStr, String fileName, String notesStr, String timeStr, boolean isVerified) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_report_card, layoutReportContent, false);

        TextView tvDate = card.findViewById(R.id.tvReportCardDate);
        TextView tvTime = card.findViewById(R.id.tvReportCardTime);

        LinearLayout layoutBadge = card.findViewById(R.id.layoutReportBadge);
        ImageView imgBadgeIcon = card.findViewById(R.id.imgReportBadgeIcon);
        TextView tvBadgeText = card.findViewById(R.id.tvReportBadgeText);

        TextView tvDocName = card.findViewById(R.id.tvReportDocName);
        TextView tvNotes = card.findViewById(R.id.tvReportNotes);

        if (tvDate != null) tvDate.setText(dateStr);
        if (tvTime != null) tvTime.setText(timeStr);

        if (isVerified) {
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
}
