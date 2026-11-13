package com.example.trackpoint;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AttendanceReport extends AppCompatActivity {

    ImageView btnBack;
    Spinner spStudent, spMonth;
    Button btnMonthlyMode, btnDateMode, btnDownloadPdf;
    LinearLayout layoutMonthContainer, layoutDateContainer, studentAttendanceList;
    TextView tvSelectDate, tvPercentage, tvTotalDays, tvPresentDays, tvAbsentDays, tvReportListTitle;

    boolean isMonthlyMode = true;
    Calendar selectedDateCal;

    StudentRepository repository;
    String loggedInAdminName = "Aathi";

    static class StudentReportItem {
        String studentName;
        String statusOrPercentage;
        boolean isPresent;

        StudentReportItem(String studentName, String statusOrPercentage, boolean isPresent) {
            this.studentName = studentName;
            this.statusOrPercentage = statusOrPercentage;
            this.isPresent = isPresent;
        }
    }

    ArrayList<String> monthOptionsList = new ArrayList<>();
    ArrayList<StudentReportItem> currentReportList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report);

        repository = new StudentRepository(this);

        SharedPreferences sessionPrefs = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
        loggedInAdminName = sessionPrefs.getString("logged_in_admin_name", "Aathi");

        btnBack = findViewById(R.id.btnBack);
        spStudent = findViewById(R.id.spStudent);
        spMonth = findViewById(R.id.spMonth);

        btnMonthlyMode = findViewById(R.id.btnMonthlyMode);
        btnDateMode = findViewById(R.id.btnDateMode);

        layoutMonthContainer = findViewById(R.id.layoutMonthContainer);
        layoutDateContainer = findViewById(R.id.layoutDateContainer);
        tvSelectDate = findViewById(R.id.tvSelectDate);

        tvPercentage = findViewById(R.id.tvPercentage);
        tvTotalDays = findViewById(R.id.tvTotalDays);
        tvPresentDays = findViewById(R.id.tvPresentDays);
        tvAbsentDays = findViewById(R.id.tvAbsentDays);
        tvReportListTitle = findViewById(R.id.tvReportListTitle);

        studentAttendanceList = findViewById(R.id.studentAttendanceList);
        btnDownloadPdf = findViewById(R.id.btnDownloadPdf);

        btnBack.setOnClickListener(v -> finish());

        // Load Student Admin's Assigned Students for Spinner
        ArrayList<StudentModel> assignedStudents = repository.searchAndFilterStudentsForAdmin("", "All Departments", "All Years", loggedInAdminName);
        ArrayList<String> studentNames = new ArrayList<>();
        studentNames.add("All Assigned Students");
        for (StudentModel s : assignedStudents) {
            studentNames.add(s.getName());
        }

        // Generate Dynamic Month List (Current and Previous Months)
        buildDynamicMonthList();

        setSpinner(spStudent, studentNames.toArray(new String[0]));
        setSpinner(spMonth, monthOptionsList.toArray(new String[0]));

        // Select Current Month by Default
        SimpleDateFormat currentMonthFmt = new SimpleDateFormat("MMMM yyyy", Locale.US);
        String currentMonthStr = currentMonthFmt.format(new Date());
        for (int i = 0; i < monthOptionsList.size(); i++) {
            if (monthOptionsList.get(i).equalsIgnoreCase(currentMonthStr)) {
                spMonth.setSelection(i);
                break;
            }
        }

        selectedDateCal = Calendar.getInstance();
        updateDateText();

        btnMonthlyMode.setOnClickListener(v -> {
            isMonthlyMode = true;
            btnMonthlyMode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4935B8")));
            btnMonthlyMode.setTextColor(Color.WHITE);

            btnDateMode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EAE5FF")));
            btnDateMode.setTextColor(Color.parseColor("#4935B8"));

            if (layoutMonthContainer != null) layoutMonthContainer.setVisibility(View.VISIBLE);
            if (layoutDateContainer != null) layoutDateContainer.setVisibility(View.GONE);

            updateReport();
        });

        btnDateMode.setOnClickListener(v -> {
            isMonthlyMode = false;
            btnDateMode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4935B8")));
            btnDateMode.setTextColor(Color.WHITE);

            btnMonthlyMode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EAE5FF")));
            btnMonthlyMode.setTextColor(Color.parseColor("#4935B8"));

            if (layoutMonthContainer != null) layoutMonthContainer.setVisibility(View.GONE);
            if (layoutDateContainer != null) layoutDateContainer.setVisibility(View.VISIBLE);

            updateReport();
        });

        tvSelectDate.setOnClickListener(v -> showDatePicker());

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateReport();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spStudent.setOnItemSelectedListener(spinnerListener);
        spMonth.setOnItemSelectedListener(spinnerListener);

        if (btnDownloadPdf != null) {
            btnDownloadPdf.setOnClickListener(v -> generateAndDownloadPdf());
        }

        updateReport();
    }

    private void buildDynamicMonthList() {
        monthOptionsList.clear();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat monthFmt = new SimpleDateFormat("MMMM yyyy", Locale.US);

        // Include last 12 months dynamically
        for (int i = 0; i < 12; i++) {
            monthOptionsList.add(monthFmt.format(cal.getTime()));
            cal.add(Calendar.MONTH, -1);
        }
    }

    private String parseMonthPrefix(String monthYearStr) {
        try {
            SimpleDateFormat inFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);
            Date date = inFormat.parse(monthYearStr);
            if (date != null) {
                SimpleDateFormat outFormat = new SimpleDateFormat("yyyy_MM", Locale.US);
                return outFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void updateDateText() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        if (tvSelectDate != null) {
            tvSelectDate.setText("📅   " + sdf.format(selectedDateCal.getTime()));
        }
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateCal.set(Calendar.YEAR, year);
                    selectedDateCal.set(Calendar.MONTH, month);
                    selectedDateCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateText();
                    updateReport();
                },
                selectedDateCal.get(Calendar.YEAR),
                selectedDateCal.get(Calendar.MONTH),
                selectedDateCal.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void updateReport() {
        currentReportList.clear();

        String selectedStudentFilter = spStudent.getSelectedItem() != null ? spStudent.getSelectedItem().toString() : "All Assigned Students";
        ArrayList<StudentModel> assignedStudents = repository.searchAndFilterStudentsForAdmin("", "All Departments", "All Years", loggedInAdminName);

        if (assignedStudents.isEmpty()) {
            if (tvPercentage != null) tvPercentage.setText("0%");
            if (tvTotalDays != null) tvTotalDays.setText("0 Days");
            if (tvPresentDays != null) tvPresentDays.setText("0 Days");
            if (tvAbsentDays != null) tvAbsentDays.setText("0 Days");
            renderReportList();
            return;
        }

        if (isMonthlyMode) {
            String selectedMonthStr = spMonth.getSelectedItem() != null ? spMonth.getSelectedItem().toString() : "";
            String monthPrefix = parseMonthPrefix(selectedMonthStr);

            if (tvReportListTitle != null) {
                tvReportListTitle.setText("Monthly Attendance Report - " + selectedMonthStr);
            }

            int totalDays = 0;
            int presentDays = 0;
            int absentDays = 0;

            for (StudentModel s : assignedStudents) {
                if (!selectedStudentFilter.equals("All Assigned Students") && !s.getName().equalsIgnoreCase(selectedStudentFilter)) {
                    continue;
                }

                int[] stats = repository.getAttendanceStatsForStudent(s.getRegisterNumber(), monthPrefix);
                totalDays += stats[0];
                presentDays += stats[1];
                absentDays += stats[2];

                int perc = stats[0] > 0 ? (stats[1] * 100 / stats[0]) : 100;
                String detailStr = stats[0] > 0 ? (perc + "% (" + stats[1] + "/" + stats[0] + " Present)") : "No records in " + selectedMonthStr;
                currentReportList.add(new StudentReportItem(s.getName(), detailStr, perc >= 75));
            }

            int overallPerc = totalDays > 0 ? (presentDays * 100 / totalDays) : 100;

            if (tvPercentage != null) tvPercentage.setText(overallPerc + "%");
            if (tvTotalDays != null) tvTotalDays.setText(totalDays + " Days");
            if (tvPresentDays != null) tvPresentDays.setText(presentDays + " Days");
            if (tvAbsentDays != null) tvAbsentDays.setText(absentDays + " Days");

        } else {
            // Specific Date View
            SimpleDateFormat displayFmt = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            SimpleDateFormat keyFmt = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());

            String dateDisplay = displayFmt.format(selectedDateCal.getTime());
            String dateKeyStr = keyFmt.format(selectedDateCal.getTime());

            if (tvReportListTitle != null) {
                tvReportListTitle.setText("Specific Date Report - " + dateDisplay);
            }

            int dayOfWeek = selectedDateCal.get(Calendar.DAY_OF_WEEK);
            boolean isWeekend = (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY);

            if (isWeekend) {
                if (tvPercentage != null) tvPercentage.setText("0%");
                if (tvTotalDays != null) tvTotalDays.setText("0 Days");
                if (tvPresentDays != null) tvPresentDays.setText("0 Days");
                if (tvAbsentDays != null) tvAbsentDays.setText("0 Days");

                for (StudentModel s : assignedStudents) {
                    if (!selectedStudentFilter.equals("All Assigned Students") && !s.getName().equalsIgnoreCase(selectedStudentFilter)) {
                        continue;
                    }
                    currentReportList.add(new StudentReportItem(s.getName(), "Weekend Holiday", false));
                }
            } else {
                int total = 0;
                int present = 0;
                int absent = 0;

                for (StudentModel s : assignedStudents) {
                    if (!selectedStudentFilter.equals("All Assigned Students") && !s.getName().equalsIgnoreCase(selectedStudentFilter)) {
                        continue;
                    }
                    total++;
                    String status = repository.getAttendanceStatus(s.getRegisterNumber(), dateKeyStr);
                    boolean isPresent = "Present".equalsIgnoreCase(status);
                    if (isPresent) {
                        present++;
                    } else if ("Absent".equalsIgnoreCase(status)) {
                        absent++;
                    }

                    currentReportList.add(new StudentReportItem(s.getName(), status != null ? (isPresent ? "Present" : "Absent") : "Not Marked", isPresent));
                }

                int datePerc = total > 0 ? (present * 100 / total) : 0;
                if (tvPercentage != null) tvPercentage.setText(datePerc + "%");
                if (tvTotalDays != null) tvTotalDays.setText(total + " Days");
                if (tvPresentDays != null) tvPresentDays.setText(present + " Days");
                if (tvAbsentDays != null) tvAbsentDays.setText(absent + " Days");
            }
        }

        renderReportList();
    }

    private void renderReportList() {
        if (studentAttendanceList == null) return;
        studentAttendanceList.removeAllViews();

        if (currentReportList.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No students assigned to " + loggedInAdminName + " in database");
            emptyView.setPadding(16, 32, 16, 32);
            emptyView.setGravity(android.view.Gravity.CENTER);
            emptyView.setTextColor(android.graphics.Color.parseColor("#68738A"));
            studentAttendanceList.addView(emptyView);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);

        for (StudentReportItem item : currentReportList) {
            View row = inflater.inflate(
                    R.layout.item_attendance_report,
                    studentAttendanceList,
                    false
            );

            TextView tvName = row.findViewById(R.id.tvReportStudentName);
            TextView tvPerc = row.findViewById(R.id.tvReportPercentage);

            if (tvName != null) tvName.setText(item.studentName);
            if (tvPerc != null) {
                tvPerc.setText(item.statusOrPercentage);
                tvPerc.setTextColor(item.isPresent ? Color.parseColor("#10B23E") : Color.parseColor("#D94343"));
            }

            studentAttendanceList.addView(row);
        }
    }

    private void generateAndDownloadPdf() {
        String filterTypeStr = isMonthlyMode ?
                (spMonth.getSelectedItem() != null ? spMonth.getSelectedItem().toString() : "Monthly") :
                new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(selectedDateCal.getTime());

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        // Header Background Bar
        paint.setColor(Color.parseColor("#4935B8"));
        canvas.drawRect(0, 0, 595, 80, paint);

        // Header Title
        paint.setColor(Color.WHITE);
        paint.setTextSize(22);
        paint.setFakeBoldText(true);
        canvas.drawText("TrackPoint - Attendance Report", 40, 50, paint);

        // Subtitle Filters
        paint.setColor(Color.parseColor("#18233F"));
        paint.setTextSize(14);
        paint.setFakeBoldText(true);
        canvas.drawText("Admin: " + loggedInAdminName, 40, 115, paint);
        canvas.drawText("Filter / Time: " + filterTypeStr, 40, 135, paint);

        // Summary Stats Box
        paint.setColor(Color.parseColor("#F5F6FC"));
        canvas.drawRect(40, 155, 555, 235, paint);

        paint.setColor(Color.parseColor("#18233F"));
        paint.setTextSize(13);
        paint.setFakeBoldText(false);

        String overallPerc = tvPercentage != null ? tvPercentage.getText().toString() : "100%";
        String totalD = tvTotalDays != null ? tvTotalDays.getText().toString() : "0 Days";
        String presentD = tvPresentDays != null ? tvPresentDays.getText().toString() : "0 Days";
        String absentD = tvAbsentDays != null ? tvAbsentDays.getText().toString() : "0 Days";

        canvas.drawText("Overall Percentage: " + overallPerc, 60, 180, paint);
        canvas.drawText("Total Range Days: " + totalD, 60, 200, paint);
        canvas.drawText("Present: " + presentD + "   |   Absent: " + absentD, 60, 220, paint);

        // Student Table Header
        paint.setColor(Color.parseColor("#4935B8"));
        paint.setTextSize(14);
        paint.setFakeBoldText(true);
        canvas.drawText("Student Name", 40, 270, paint);
        canvas.drawText("Status / Percentage", 360, 270, paint);

        paint.setColor(Color.parseColor("#EEEEF3"));
        canvas.drawLine(40, 280, 555, 280, paint);

        // Student Table Rows
        int y = 310;
        paint.setColor(Color.parseColor("#18233F"));
        paint.setTextSize(13);
        paint.setFakeBoldText(false);

        for (StudentReportItem s : currentReportList) {
            canvas.drawText(s.studentName, 40, y, paint);
            canvas.drawText(s.statusOrPercentage, 360, y, paint);
            y += 28;
        }

        document.finishPage(page);

        // Save PDF File
        String fileName = "Attendance_Report_" + loggedInAdminName.replace(" ", "_") + "_" + filterTypeStr.replace(" ", "_") + ".pdf";
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "📄 PDF Saved: " + file.getName(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to download PDF", Toast.LENGTH_SHORT).show();
        } finally {
            document.close();
        }
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
