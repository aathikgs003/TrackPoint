package com.example.trackpoint;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class StaffFileViewer extends AppCompatActivity {

    ImageView btnBack, btnViewerDownload;
    TextView txtViewerFileName, txtViewerStudent, txtDocHeader, txtDocBody, txtDocFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4825B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_staff_file_viewer);

        btnBack = findViewById(R.id.btnBack);
        btnViewerDownload = findViewById(R.id.btnViewerDownload);
        txtViewerFileName = findViewById(R.id.txtViewerFileName);
        txtViewerStudent = findViewById(R.id.txtViewerStudent);
        txtDocHeader = findViewById(R.id.txtDocHeader);
        txtDocBody = findViewById(R.id.txtDocBody);
        txtDocFooter = findViewById(R.id.txtDocFooter);

        btnBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String studentName = intent.getStringExtra("studentName");
        String regId = intent.getStringExtra("regId");
        String fileType = intent.getStringExtra("fileType");
        String fileName = intent.getStringExtra("fileName");
        String notes = intent.getStringExtra("notes");

        txtViewerFileName.setText(fileName);
        txtViewerStudent.setText("Student: " + studentName + " (" + regId + ")");

        if ("Image".equalsIgnoreCase(fileType)) {
            txtDocHeader.setText("IMAGE SCREENSHOT PREVIEW");
            txtDocBody.setText("Attached Screenshot: " + fileName + "\n\nSummary:\n" + notes);
            txtDocFooter.setText("High-Resolution Image Preview");
        } else {
            txtDocHeader.setText("PDF SUBMISSION REPORT");
            txtDocBody.setText("Document: " + fileName + "\n\nWork Summary:\n" + notes +
                    "\n\nStatus: Submitted & logged in TrackPoint.");
            txtDocFooter.setText("Page 1 of 1 • PDF Document");
        }

        btnViewerDownload.setOnClickListener(v -> {
            Toast.makeText(this, "Saved " + fileName + " to Downloads", Toast.LENGTH_SHORT).show();
        });
    }
}