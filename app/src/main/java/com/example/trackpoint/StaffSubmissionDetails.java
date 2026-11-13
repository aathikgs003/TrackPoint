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
        String studentNameParam = intent != null ? intent.getStringExtra("studentName") : null;
        String studentIdParam = intent != null ? intent.getStringExtra("studentId") : null;
        if (studentIdParam == null && intent != null) {
            studentIdParam = intent.getStringExtra("regId");
        }
        if (studentIdParam != null && !studentIdParam.isEmpty()) {
            currentRegId = studentIdParam;
        }

        boolean isSubmitted = intent != null && intent.getBooleanExtra("isSubmitted", true);
        String dept = intent != null ? intent.getStringExtra("department") : null;
        String year = intent != null ? intent.getStringExtra("year") : null;
        String assignedAdmin = intent != null ? intent.getStringExtra("assignedAdmin") : null;
        String dateLabel = intent != null ? intent.getStringExtra("submittedDate") : null;
        String notes = intent != null ? intent.getStringExtra("notes") : null;
        String fileType = intent != null ? intent.getStringExtra("fileType") : "PDF";
        String fileName = intent != null ? intent.getStringExtra("fileName") : null;
        String fileSize = intent != null ? intent.getStringExtra("fileSize") : "1.2 MB";

        StudentModel dbStudent = repository.getStudentByRegNo(currentRegId);
        if (dbStudent != null) {
            studentNameParam = dbStudent.getName();
            dept = dbStudent.getDepartment();
            year = dbStudent.getYear();
            assignedAdmin = dbStudent.getAssignedAdmin();
        }

        SimpleDateFormat keyFmt = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());
        todayDateKey = keyFmt.format(new Date());

        DailyActivityModel dbModel = repository.getDailyActivity(currentRegId, todayDateKey);
        if (dbModel != null) {
            isSubmitted = true;
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

        if (fileName != null && (fileName.equals("No File Uploaded") || "None".equalsIgnoreCase(fileType))) {
            isSubmitted = false;
        }

        if (studentNameParam == null || studentNameParam.isEmpty()) studentNameParam = "Arun Kumar";
        if (dept == null) dept = "CSE";
        if (year == null) year = "III Year";
        if (assignedAdmin == null) assignedAdmin = "Aathi";

        if (txtSubStudentName != null) txtSubStudentName.setText(studentNameParam);
        if (txtSubRegDept != null) txtSubRegDept.setText(currentRegId + "  •  " + dept + "  •  " + year);
        if (txtSubAdmin != null) txtSubAdmin.setText(assignedAdmin);

        if (!isSubmitted) {
            if (txtSubDate != null) txtSubDate.setText("Not Submitted");
            if (txtSubNotes != null) txtSubNotes.setText("No activity document or description submitted for this date.");

            if (txtAttachmentHeading != null) txtAttachmentHeading.setVisibility(View.GONE);
            if (cardAttachment != null) cardAttachment.setVisibility(View.GONE);
            if (btnHeaderDownload != null) btnHeaderDownload.setVisibility(View.INVISIBLE);
        } else {
            if (dateLabel == null) dateLabel = "Today, 10:30 AM";
            if (notes == null || notes.isEmpty()) notes = "Daily activity document uploaded and verified.";
            if (fileName == null || fileName.isEmpty()) fileName = "daily_record.pdf";

            if (txtSubDate != null) txtSubDate.setText(dateLabel);
            if (txtSubNotes != null) txtSubNotes.setText(notes);

            if ("Notes".equalsIgnoreCase(fileType) || "None".equalsIgnoreCase(fileType)) {
                if (txtAttachmentHeading != null) txtAttachmentHeading.setVisibility(View.GONE);
                if (cardAttachment != null) cardAttachment.setVisibility(View.GONE);
                if (btnHeaderDownload != null) btnHeaderDownload.setVisibility(View.INVISIBLE);
            } else {
                if (txtAttachmentHeading != null) txtAttachmentHeading.setVisibility(View.VISIBLE);
                if (cardAttachment != null) cardAttachment.setVisibility(View.VISIBLE);
                if (btnHeaderDownload != null) btnHeaderDownload.setVisibility(View.VISIBLE);

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
                final String sName = studentNameParam;
                final String fReg = currentRegId;
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
        }

        final String finalRegId = currentRegId;
        final String finalName = studentNameParam;

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
