package com.example.trackpoint;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class StaffSubmissionDetails extends AppCompatActivity {

    ImageView btnBack, btnHeaderDownload;
    TextView txtSubStudentName, txtSubRegDept, txtSubAdmin, txtSubDate, txtSubNotes;
    TextView txtAttachmentHeading, txtFileBadge, txtFileName, txtFileSize;
    LinearLayout cardAttachment;
    EditText edtStaffRemark;
    Button btnSaveRemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4825B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_staff_submission_details);

        btnBack = findViewById(R.id.btnBack);
        btnHeaderDownload = findViewById(R.id.btnHeaderDownload);
        txtSubStudentName = findViewById(R.id.txtSubStudentName);
        txtSubRegDept = findViewById(R.id.txtSubRegDept);
        txtSubAdmin = findViewById(R.id.txtSubAdmin);
        txtSubDate = findViewById(R.id.txtSubDate);
        txtSubNotes = findViewById(R.id.txtSubNotes);
        txtAttachmentHeading = findViewById(R.id.txtAttachmentHeading);
        txtFileBadge = findViewById(R.id.txtFileBadge);
        txtFileName = findViewById(R.id.txtFileName);
        txtFileSize = findViewById(R.id.txtFileSize);
        cardAttachment = findViewById(R.id.cardAttachment);
        edtStaffRemark = findViewById(R.id.edtStaffRemark);
        btnSaveRemark = findViewById(R.id.btnSaveRemark);

        btnBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String studentName = intent.getStringExtra("studentName");
        String regId = intent.getStringExtra("regId");
        String dept = intent.getStringExtra("dept");
        String year = intent.getStringExtra("year");
        String assignedAdmin = intent.getStringExtra("assignedAdmin");
        String dateLabel = intent.getStringExtra("dateLabel");
        String notes = intent.getStringExtra("notes");
        String fileType = intent.getStringExtra("fileType");
        String fileName = intent.getStringExtra("fileName");
        String fileSize = intent.getStringExtra("fileSize");

        txtSubStudentName.setText(studentName);
        txtSubRegDept.setText(regId + "  •  " + dept + "  •  " + year);
        txtSubAdmin.setText(assignedAdmin);
        txtSubDate.setText(dateLabel);
        txtSubNotes.setText(notes);

        if ("Notes".equalsIgnoreCase(fileType)) {
            txtAttachmentHeading.setVisibility(View.GONE);
            cardAttachment.setVisibility(View.GONE);
            btnHeaderDownload.setVisibility(View.INVISIBLE);
        } else {
            txtFileName.setText(fileName);
            txtFileSize.setText(fileSize + " • Tap to View Fullscreen");
            if ("Image".equalsIgnoreCase(fileType)) {
                txtFileBadge.setText("IMG");
                txtFileBadge.setBackgroundColor(Color.parseColor("#1565C0"));
            } else {
                txtFileBadge.setText("PDF");
                txtFileBadge.setBackgroundColor(Color.parseColor("#D32F2F"));
            }

            cardAttachment.setOnClickListener(v -> {
                Intent viewerIntent = new Intent(StaffSubmissionDetails.this, StaffFileViewer.class);
                viewerIntent.putExtra("studentName", studentName);
                viewerIntent.putExtra("regId", regId);
                viewerIntent.putExtra("fileType", fileType);
                viewerIntent.putExtra("fileName", fileName);
                viewerIntent.putExtra("notes", notes);
                startActivity(viewerIntent);
            });

            btnHeaderDownload.setOnClickListener(v -> {
                Toast.makeText(this, "Downloading " + fileName + "...", Toast.LENGTH_SHORT).show();
            });
        }

        btnSaveRemark.setOnClickListener(v -> {
            String remark = edtStaffRemark.getText().toString().trim();
            if (remark.isEmpty()) {
                edtStaffRemark.setError("Enter a remark first");
                return;
            }
            Toast.makeText(this, "Remark saved for " + studentName, Toast.LENGTH_SHORT).show();
            edtStaffRemark.setText("");
        });
    }
}