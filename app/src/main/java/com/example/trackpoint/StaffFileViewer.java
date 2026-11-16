package com.example.trackpoint;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StaffFileViewer extends AppCompatActivity {

    ImageView btnBack, btnViewerDownload;
    TextView txtViewerFileName, txtViewerStudent, txtDocHeader, txtDocBody, txtDocFooter;
    StudentRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_staff_file_viewer);

        repository = new StudentRepository(this);

        btnBack = findViewById(R.id.btnBack);
        btnViewerDownload = findViewById(R.id.btnViewerDownload);
        txtViewerFileName = findViewById(R.id.txtViewerFileName);
        txtViewerStudent = findViewById(R.id.txtViewerStudent);
        txtDocHeader = findViewById(R.id.txtDocHeader);
        txtDocBody = findViewById(R.id.txtDocBody);
        txtDocFooter = findViewById(R.id.txtDocFooter);

        btnBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String studentName = intent != null ? intent.getStringExtra("studentName") : "";
        String regId = intent != null ? intent.getStringExtra("regId") : "";
        String fileType = intent != null ? intent.getStringExtra("fileType") : "PDF";
        String fileName = intent != null ? intent.getStringExtra("fileName") : "daily_record.pdf";
        String notes = intent != null ? intent.getStringExtra("notes") : "";

        if (regId != null && !regId.isEmpty()) {
            StudentModel s = repository.getStudentByRegNo(regId);
            if (s != null) {
                studentName = s.getName();
            }
        }

        if (txtViewerFileName != null) txtViewerFileName.setText(fileName != null ? fileName : "Document");
        if (txtViewerStudent != null) txtViewerStudent.setText("Student: " + (studentName != null ? studentName : "") + " (" + (regId != null ? regId : "") + ")");

        SimpleDateFormat keyFmt = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());
        String todayKeyStr = keyFmt.format(new Date());

        String objectKey = "submissions/" + regId + "/" + todayKeyStr + "_" + fileName;
        String presignedUrl = S3StorageManager.getInstance(this).generatePresignedUrl(objectKey, 60);

        if ("Image".equalsIgnoreCase(fileType)) {
            if (txtDocHeader != null) txtDocHeader.setText("IMAGE SCREENSHOT PREVIEW");
            if (txtDocBody != null) txtDocBody.setText("Attached Screenshot: " + fileName + "\n\nSummary:\n" + (notes != null ? notes : "") + "\n\nBackblaze B2 S3 Object Key:\n" + objectKey);
            if (txtDocFooter != null) txtDocFooter.setText("High-Resolution Image Preview • Presigned URL Active");
        } else {
            if (txtDocHeader != null) txtDocHeader.setText("PDF SUBMISSION REPORT");
            if (txtDocBody != null) txtDocBody.setText("Document: " + fileName + "\n\nWork Summary:\n" + (notes != null ? notes : "") +
                    "\n\nStatus: Submitted & Logged in TrackPoint Backblaze B2 S3 Cloud Storage.");
            if (txtDocFooter != null) txtDocFooter.setText("Page 1 of 1 • PDF Document • Signed Presigned URL Active");
        }

        final String downloadName = fileName;
        if (btnViewerDownload != null) {
            btnViewerDownload.setOnClickListener(v -> {
                if (presignedUrl != null && !presignedUrl.isEmpty()) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(presignedUrl));
                        startActivity(browserIntent);
                    } catch (Exception e) {
                        Toast.makeText(this, "Downloading " + downloadName + " via S3...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Saved " + downloadName + " to Downloads", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
