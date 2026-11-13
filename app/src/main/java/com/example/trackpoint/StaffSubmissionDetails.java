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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StaffSubmissionDetails extends AppCompatActivity {

    ImageView btnBack, btnHeaderDownload;
    TextView txtSubStudentName, txtSubRegDept, txtSubAdmin, txtSubDate, txtSubNotes;
    TextView txtAttachmentHeading, txtFileBadge, txtFileName, txtFileSize;
    LinearLayout cardAttachment;
    EditText edtStaffRemark;
    Button btnSaveRemark;

    StudentRepository repository;
    String currentRegId = "CSE22CS001";
    String todayDateKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_staff_submission_details);

        repository = new StudentRepository(this);

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
        String studentName = intent != null ? intent.getStringExtra("studentName") : "Arun Kumar";
        String regId = intent != null ? intent.getStringExtra("studentId") : "CSE22CS001";
        if (regId == null) regId = "CSE22CS001";
        currentRegId = regId;

        String dept = intent != null ? intent.getStringExtra("department") : "CSE";
        String year = intent != null ? intent.getStringExtra("year") : "III Year";
        String assignedAdmin = intent != null ? intent.getStringExtra("assignedAdmin") : "Aathi";
        String dateLabel = intent != null ? intent.getStringExtra("submittedDate") : "Today, 10:30 AM";
        String notes = intent != null ? intent.getStringExtra("notes") : "Java activity & loops completed.";
        String fileType = intent != null ? intent.getStringExtra("fileType") : "PDF";
        String fileName = intent != null ? intent.getStringExtra("fileName") : "daily_record.pdf";
        String fileSize = intent != null ? intent.getStringExtra("fileSize") : "1.2 MB";

        SimpleDateFormat keyFmt = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());
        todayDateKey = keyFmt.format(new Date());

        DailyActivityModel dbModel = repository.getDailyActivity(currentRegId, todayDateKey);
        if (dbModel != null) {
            if (dbModel.getNotes() != null && !dbModel.getNotes().isEmpty()) {
                notes = dbModel.getNotes();
            }
            if (dbModel.getFileName() != null && !dbModel.getFileName().isEmpty()) {
                fileName = dbModel.getFileName();
            }
            if (dbModel.getStaffRemark() != null && !dbModel.getStaffRemark().isEmpty()) {
                if (edtStaffRemark != null) edtStaffRemark.setText(dbModel.getStaffRemark());
            }
        }

        if (txtSubStudentName != null) txtSubStudentName.setText(studentName);
        if (txtSubRegDept != null) txtSubRegDept.setText(regId + "  •  " + dept + "  •  " + year);
        if (txtSubAdmin != null) txtSubAdmin.setText(assignedAdmin != null ? assignedAdmin : "Aathi");
        if (txtSubDate != null) txtSubDate.setText(dateLabel);
        if (txtSubNotes != null) txtSubNotes.setText(notes);

        if ("Notes".equalsIgnoreCase(fileType)) {
            if (txtAttachmentHeading != null) txtAttachmentHeading.setVisibility(View.GONE);
            if (cardAttachment != null) cardAttachment.setVisibility(View.GONE);
            if (btnHeaderDownload != null) btnHeaderDownload.setVisibility(View.INVISIBLE);
        } else {
            if (txtFileName != null) txtFileName.setText(fileName);
            if (txtFileSize != null) txtFileSize.setText(fileSize + " • Tap to View Fullscreen");
            if (txtFileBadge != null) {
                if ("Image".equalsIgnoreCase(fileType)) {
                    txtFileBadge.setText("IMG");
                    txtFileBadge.setBackgroundColor(Color.parseColor("#1565C0"));
                } else {
                    txtFileBadge.setText("PDF");
                    txtFileBadge.setBackgroundColor(Color.parseColor("#D32F2F"));
                }
            }

            final String fName = fileName;
            final String sName = studentName;
            final String fReg = regId;
            final String fType = fileType;
            final String fNotes = notes;

            if (cardAttachment != null) {
                cardAttachment.setOnClickListener(v -> {
                    Intent viewerIntent = new Intent(StaffSubmissionDetails.this, StaffFileViewer.class);
                    viewerIntent.putExtra("studentName", sName);
                    viewerIntent.putExtra("regId", fReg);
                    viewerIntent.putExtra("fileType", fType);
                    viewerIntent.putExtra("fileName", fName);
                    viewerIntent.putExtra("notes", fNotes);
                    startActivity(viewerIntent);
                });
            }

            if (btnHeaderDownload != null) {
                btnHeaderDownload.setOnClickListener(v -> Toast.makeText(this, "Downloading " + fName + "...", Toast.LENGTH_SHORT).show());
            }
        }

        final String finalRegId = currentRegId;
        final String finalName = studentName;

        if (btnSaveRemark != null) {
            btnSaveRemark.setOnClickListener(v -> {
                String remark = edtStaffRemark != null ? edtStaffRemark.getText().toString().trim() : "";
                if (remark.isEmpty()) {
                    if (edtStaffRemark != null) edtStaffRemark.setError("Enter a remark first");
                    return;
                }

                repository.updateStaffRemark(finalRegId, todayDateKey, remark);
                Toast.makeText(this, "Remark saved for " + finalName + " in SQLite database!", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
