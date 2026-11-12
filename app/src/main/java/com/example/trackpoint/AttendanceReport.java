package com.example.trackpoint;

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
import java.util.ArrayList;

public class AttendanceReport extends AppCompatActivity {

    ImageView btnBack;
    Spinner spMonth;
    LinearLayout studentAttendanceList;
    TextView tvPercentage, tvTotalDays, tvPresentDays, tvAbsentDays;
    Button btnDownloadPdf;

    static class StudentReport {
        String name;
        String percentage;

        StudentReport(String name, String percentage) {
            this.name = name;
            this.percentage = percentage;
        }
    }

    String[] months = {
            "January 2025", "February 2025", "March 2025", "April 2025",
            "May 2025", "June 2025", "July 2025", "August 2025",
            "September 2025", "October 2025", "November 2025", "December 2025"
    };

    ArrayList<StudentReport> currentStudentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report);

        btnBack = findViewById(R.id.btnBack);
        spMonth = findViewById(R.id.spMonth);
        studentAttendanceList = findViewById(R.id.studentAttendanceList);

        tvPercentage = findViewById(R.id.tvPercentage);
        tvTotalDays = findViewById(R.id.tvTotalDays);
        tvPresentDays = findViewById(R.id.tvPresentDays);
        tvAbsentDays = findViewById(R.id.tvAbsentDays);

        btnDownloadPdf = findViewById(R.id.btnDownloadPdf);

        btnBack.setOnClickListener(v -> finish());

        setSpinner(spMonth, months);

        spMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMonthReport(months[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Initial month setup
        updateMonthReport("January 2025");

        if (btnDownloadPdf != null) {
            btnDownloadPdf.setOnClickListener(v -> generateAndDownloadPdf());
        }
    }

    private void updateMonthReport(String month) {
        currentStudentList.clear();

        if (month.startsWith("February")) {
            if (tvPercentage != null) tvPercentage.setText("88%");
            if (tvTotalDays != null) tvTotalDays.setText("20 Days");
            if (tvPresentDays != null) tvPresentDays.setText("18 Days");
            if (tvAbsentDays != null) tvAbsentDays.setText("2 Days");

            currentStudentList.add(new StudentReport("Arun Kumar", "100%"));
            currentStudentList.add(new StudentReport("Divya S", "95%"));
            currentStudentList.add(new StudentReport("Karthik R", "85%"));
            currentStudentList.add(new StudentReport("Nithya P", "90%"));
            currentStudentList.add(new StudentReport("Praveen M", "88%"));
        } else if (month.startsWith("March")) {
            if (tvPercentage != null) tvPercentage.setText("91%");
            if (tvTotalDays != null) tvTotalDays.setText("23 Days");
            if (tvPresentDays != null) tvPresentDays.setText("21 Days");
            if (tvAbsentDays != null) tvAbsentDays.setText("2 Days");

            currentStudentList.add(new StudentReport("Arun Kumar", "96%"));
            currentStudentList.add(new StudentReport("Divya S", "92%"));
            currentStudentList.add(new StudentReport("Karthik R", "88%"));
            currentStudentList.add(new StudentReport("Nithya P", "94%"));
            currentStudentList.add(new StudentReport("Praveen M", "90%"));
        } else if (month.startsWith("April")) {
            if (tvPercentage != null) tvPercentage.setText("82%");
            if (tvTotalDays != null) tvTotalDays.setText("21 Days");
            if (tvPresentDays != null) tvPresentDays.setText("17 Days");
            if (tvAbsentDays != null) tvAbsentDays.setText("4 Days");

            currentStudentList.add(new StudentReport("Arun Kumar", "90%"));
            currentStudentList.add(new StudentReport("Divya S", "86%"));
            currentStudentList.add(new StudentReport("Karthik R", "76%"));
            currentStudentList.add(new StudentReport("Nithya P", "82%"));
            currentStudentList.add(new StudentReport("Praveen M", "85%"));
        } else if (month.startsWith("May")) {
            if (tvPercentage != null) tvPercentage.setText("86%");
            if (tvTotalDays != null) tvTotalDays.setText("22 Days");
            if (tvPresentDays != null) tvPresentDays.setText("19 Days");
            if (tvAbsentDays != null) tvAbsentDays.setText("3 Days");

            currentStudentList.add(new StudentReport("Arun Kumar", "95%"));
            currentStudentList.add(new StudentReport("Divya S", "91%"));
            currentStudentList.add(new StudentReport("Karthik R", "81%"));
            currentStudentList.add(new StudentReport("Nithya P", "86%"));
            currentStudentList.add(new StudentReport("Praveen M", "90%"));
        } else { // January or other months
            if (tvPercentage != null) tvPercentage.setText("85%");
            if (tvTotalDays != null) tvTotalDays.setText("22 Days");
            if (tvPresentDays != null) tvPresentDays.setText("19 Days");
            if (tvAbsentDays != null) tvAbsentDays.setText("3 Days");

            currentStudentList.add(new StudentReport("Arun Kumar", "95%"));
            currentStudentList.add(new StudentReport("Divya S", "90%"));
            currentStudentList.add(new StudentReport("Karthik R", "80%"));
            currentStudentList.add(new StudentReport("Nithya P", "85%"));
            currentStudentList.add(new StudentReport("Praveen M", "92%"));
        }

        renderStudentList();
    }

    private void renderStudentList() {
        if (studentAttendanceList == null) return;
        studentAttendanceList.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (StudentReport item : currentStudentList) {
            View row = inflater.inflate(
                    R.layout.item_attendance_report,
                    studentAttendanceList,
                    false
            );

            TextView name = row.findViewById(R.id.tvReportStudentName);
            TextView percentage = row.findViewById(R.id.tvReportPercentage);

            name.setText(item.name);
            percentage.setText(item.percentage);

            studentAttendanceList.addView(row);
        }
    }

    private void generateAndDownloadPdf() {
        String selectedMonth = spMonth.getSelectedItem() != null ? spMonth.getSelectedItem().toString() : "Report";

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
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

        // Report Subtitle
        paint.setColor(Color.parseColor("#18233F"));
        paint.setTextSize(16);
        paint.setFakeBoldText(true);
        canvas.drawText("Month: " + selectedMonth, 40, 120, paint);

        // Summary Stats Box
        paint.setColor(Color.parseColor("#F5F6FC"));
        canvas.drawRect(40, 140, 555, 230, paint);

        paint.setColor(Color.parseColor("#18233F"));
        paint.setTextSize(14);
        paint.setFakeBoldText(false);

        String overallPerc = tvPercentage != null ? tvPercentage.getText().toString() : "85%";
        String totalD = tvTotalDays != null ? tvTotalDays.getText().toString() : "22 Days";
        String presentD = tvPresentDays != null ? tvPresentDays.getText().toString() : "19 Days";
        String absentD = tvAbsentDays != null ? tvAbsentDays.getText().toString() : "3 Days";

        canvas.drawText("Overall Attendance: " + overallPerc, 60, 170, paint);
        canvas.drawText("Total Working Days: " + totalD, 60, 195, paint);
        canvas.drawText("Present Days: " + presentD + "   |   Absent Days: " + absentD, 60, 220, paint);

        // Student Table Header
        paint.setColor(Color.parseColor("#4935B8"));
        paint.setTextSize(15);
        paint.setFakeBoldText(true);
        canvas.drawText("Student Name", 40, 270, paint);
        canvas.drawText("Attendance %", 400, 270, paint);

        paint.setColor(Color.parseColor("#EEEEF3"));
        canvas.drawLine(40, 280, 555, 280, paint);

        // Student Table Rows
        int y = 310;
        paint.setColor(Color.parseColor("#18233F"));
        paint.setTextSize(13);
        paint.setFakeBoldText(false);

        for (StudentReport s : currentStudentList) {
            canvas.drawText(s.name, 40, y, paint);
            canvas.drawText(s.percentage, 400, y, paint);
            y += 30;
        }

        document.finishPage(page);

        // Save PDF File
        String fileName = "Attendance_Report_" + selectedMonth.replace(" ", "_") + ".pdf";
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
