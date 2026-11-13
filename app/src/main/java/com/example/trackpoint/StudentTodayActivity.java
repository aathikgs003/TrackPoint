package com.example.trackpoint;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudentTodayActivity extends AppCompatActivity {

    ImageView btnBack;
    TextView tvCurrentDate, tvUploadTitle, tvUploadSubtitle, tvFileName;
    TextView tvSubmittedTime, tvSubmittedFileName, tvSubmittedNotes;
    LinearLayout layoutUploadBox, layoutSelectedFile;
    LinearLayout layoutUploadForm, layoutSubmittedView;
    EditText etActivityNotes;
    Button btnSubmitActivity, btnEditSubmission, btnDeleteSubmission;

    Uri selectedFileUri;
    String todayDateKey;
    String todayDateDisplay;

    StudentRepository repository;
    String loggedInRegNo = "CSE22CS001";

    private final ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedFileUri = uri;
                    String fileName = getFileNameFromUri(uri);

                    if (tvFileName != null) {
                        tvFileName.setText(fileName);
                    }
                    if (tvUploadTitle != null) {
                        tvUploadTitle.setText("File Selected");
                    }
                    if (tvUploadSubtitle != null) {
                        tvUploadSubtitle.setText("Tap to change file");
                    }
                    if (layoutSelectedFile != null) {
                        layoutSelectedFile.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(this, "File selected: " + fileName, Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_today_activity);

        repository = new StudentRepository(this);

        SharedPreferences sessionPrefs = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
        loggedInRegNo = sessionPrefs.getString("logged_in_reg_no", "CSE22CS001");

        btnBack = findViewById(R.id.btnBack);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        tvUploadTitle = findViewById(R.id.tvUploadTitle);
        tvUploadSubtitle = findViewById(R.id.tvUploadSubtitle);
        tvFileName = findViewById(R.id.tvFileName);

        tvSubmittedTime = findViewById(R.id.tvSubmittedTime);
        tvSubmittedFileName = findViewById(R.id.tvSubmittedFileName);
        tvSubmittedNotes = findViewById(R.id.tvSubmittedNotes);

        layoutUploadBox = findViewById(R.id.layoutUploadBox);
        layoutSelectedFile = findViewById(R.id.layoutSelectedFile);
        layoutUploadForm = findViewById(R.id.layoutUploadForm);
        layoutSubmittedView = findViewById(R.id.layoutSubmittedView);

        etActivityNotes = findViewById(R.id.etActivityNotes);
        btnSubmitActivity = findViewById(R.id.btnSubmitActivity);
        btnEditSubmission = findViewById(R.id.btnEditSubmission);
        btnDeleteSubmission = findViewById(R.id.btnDeleteSubmission);

        btnBack.setOnClickListener(v -> finish());

        // Setup Date Keys
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        SimpleDateFormat keyFmt = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());

        todayDateDisplay = sdf.format(today);
        todayDateKey = keyFmt.format(today);

        if (tvCurrentDate != null) {
            tvCurrentDate.setText(todayDateDisplay);
        }

        // Upload Box Listener
        if (layoutUploadBox != null) {
            layoutUploadBox.setOnClickListener(v -> filePickerLauncher.launch("*/*"));
        }

        // Submit Button Listener
        if (btnSubmitActivity != null) {
            btnSubmitActivity.setOnClickListener(v -> submitTodayActivity());
        }

        // Edit Button Listener
        if (btnEditSubmission != null) {
            btnEditSubmission.setOnClickListener(v -> prepareEditMode());
        }

        // Delete Button Listener
        if (btnDeleteSubmission != null) {
            btnDeleteSubmission.setOnClickListener(v -> showDeleteConfirmationDialog());
        }

        loadSubmissionState();
    }

    private void loadSubmissionState() {
        SharedPreferences prefs = getSharedPreferences("TodayActivityPrefs", MODE_PRIVATE);
        boolean isSubmitted = prefs.getBoolean("submitted_" + todayDateKey, false);

        DailyActivityModel dbModel = repository.getDailyActivity(loggedInRegNo, todayDateKey);
        if (dbModel != null) {
            isSubmitted = true;
        }

        if (isSubmitted) {
            String fileName = dbModel != null ? dbModel.getFileName() : prefs.getString("file_name_" + todayDateKey, "Document Uploaded");
            String submitTime = dbModel != null ? dbModel.getSubmitTime() : prefs.getString("submit_time_" + todayDateKey, "Submitted Today");
            String notes = dbModel != null ? dbModel.getNotes() : prefs.getString("notes_" + todayDateKey, "No description added");

            if (tvSubmittedFileName != null) tvSubmittedFileName.setText("📄 " + fileName);
            if (tvSubmittedTime != null) tvSubmittedTime.setText(submitTime);
            if (tvSubmittedNotes != null) tvSubmittedNotes.setText(notes.isEmpty() ? "No description added" : notes);

            if (layoutSubmittedView != null) layoutSubmittedView.setVisibility(View.VISIBLE);
            if (layoutUploadForm != null) layoutUploadForm.setVisibility(View.GONE);
        } else {
            if (layoutSubmittedView != null) layoutSubmittedView.setVisibility(View.GONE);
            if (layoutUploadForm != null) layoutUploadForm.setVisibility(View.VISIBLE);
        }
    }

    private void submitTodayActivity() {
        String notes = etActivityNotes != null ? etActivityNotes.getText().toString().trim() : "";

        if (selectedFileUri == null && notes.isEmpty()) {
            Toast.makeText(this, "Please upload a file or enter notes before submitting", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = selectedFileUri != null ? getFileNameFromUri(selectedFileUri) : "Activity Notes Submitted";
        SimpleDateFormat timeFmt = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String timeStr = timeFmt.format(new Date());

        SharedPreferences prefs = getSharedPreferences("TodayActivityPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("submitted_" + todayDateKey, true);
        editor.putString("file_name_" + todayDateKey, fileName);
        editor.putString("notes_" + todayDateKey, notes);
        editor.putString("submit_time_" + todayDateKey, "Submitted at " + timeStr);
        editor.putString("date_display_" + todayDateKey, todayDateDisplay);
        editor.apply();

        // Save into SQLite Database
        repository.saveOrUpdateDailyActivity(loggedInRegNo, todayDateKey, fileName, notes, "Submitted at " + timeStr);

        Toast.makeText(this, "Today's activity submitted successfully!", Toast.LENGTH_LONG).show();
        loadSubmissionState();
    }

    private void prepareEditMode() {
        SharedPreferences prefs = getSharedPreferences("TodayActivityPrefs", MODE_PRIVATE);
        String notes = prefs.getString("notes_" + todayDateKey, "");
        String fileName = prefs.getString("file_name_" + todayDateKey, "");

        DailyActivityModel dbModel = repository.getDailyActivity(loggedInRegNo, todayDateKey);
        if (dbModel != null) {
            notes = dbModel.getNotes();
            fileName = dbModel.getFileName();
        }

        if (etActivityNotes != null) {
            etActivityNotes.setText(notes);
        }

        if (tvFileName != null && !fileName.isEmpty()) {
            tvFileName.setText(fileName);
            if (layoutSelectedFile != null) layoutSelectedFile.setVisibility(View.VISIBLE);
        }

        if (btnSubmitActivity != null) {
            btnSubmitActivity.setText("Update Activity");
        }

        if (layoutSubmittedView != null) layoutSubmittedView.setVisibility(View.GONE);
        if (layoutUploadForm != null) layoutUploadForm.setVisibility(View.VISIBLE);
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Activity")
                .setMessage("Are you sure you want to delete today's activity submission?")
                .setPositiveButton("Yes, Delete", (dialog, which) -> deleteTodayActivity())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteTodayActivity() {
        SharedPreferences prefs = getSharedPreferences("TodayActivityPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("submitted_" + todayDateKey);
        editor.remove("file_name_" + todayDateKey);
        editor.remove("notes_" + todayDateKey);
        editor.remove("submit_time_" + todayDateKey);
        editor.remove("date_display_" + todayDateKey);
        editor.apply();

        selectedFileUri = null;
        if (etActivityNotes != null) etActivityNotes.setText("");
        if (layoutSelectedFile != null) layoutSelectedFile.setVisibility(View.GONE);
        if (tvUploadTitle != null) tvUploadTitle.setText("Click here to upload file");
        if (tvUploadSubtitle != null) tvUploadSubtitle.setText("Supports images, PDF, Word & documents");
        if (btnSubmitActivity != null) btnSubmitActivity.setText("Submit Activity");

        Toast.makeText(this, "Today's activity deleted.", Toast.LENGTH_SHORT).show();
        loadSubmissionState();
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri != null && uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null && uri != null) {
            result = uri.getPath();
            int cut = result != null ? result.lastIndexOf('/') : -1;
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result != null ? result : "Selected File";
    }
}
