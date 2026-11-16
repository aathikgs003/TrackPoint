package com.example.trackpoint;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class StaffStudentDetails extends AppCompatActivity {

    private ImageView btnBack, btnEditStudent, btnDeleteStudent;
    private TextView txtStudentName, txtStudentId, txtDepartment, txtYear, txtPhone, txtEmail;
    private TextView txtAttendance, txtProgress, txtTasks;

    private StudentRepository repository;
    private String currentStudentId = "CSE22CS001";
    private String currentStudentName = "Arun Kumar";
    private String currentDepartment = "CSE";
    private String currentYear = "III Year";
    private String currentPhone = "98765 43210";
    private String currentEmail = "arun@college.edu";

    private static final String[] DEPARTMENTS = {"CSE", "AI & DS", "ECE", "IT", "MECH"};
    private static final String[] YEARS = {"I Year", "II Year", "III Year", "IV Year"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_staff_student_details);

        repository = new StudentRepository(this);

        btnBack = findViewById(R.id.btnBack);
        btnEditStudent = findViewById(R.id.btnEditStudent);
        btnDeleteStudent = findViewById(R.id.btnDeleteStudent);

        txtStudentName = findViewById(R.id.txtStudentName);
        txtStudentId = findViewById(R.id.txtStudentId);
        txtDepartment = findViewById(R.id.txtDepartment);
        txtYear = findViewById(R.id.txtYear);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);
        txtAttendance = findViewById(R.id.txtAttendance);
        txtProgress = findViewById(R.id.txtProgress);
        txtTasks = findViewById(R.id.txtTasks);

        Intent intent = getIntent();
        if (intent != null) {
            String sId = intent.getStringExtra("studentId");
            if (sId == null) sId = intent.getStringExtra("student_roll_no");
            if (sId != null && !sId.isEmpty()) currentStudentId = sId;

            String sName = intent.getStringExtra("studentName");
            if (sName == null) sName = intent.getStringExtra("student_name");
            if (sName != null && !sName.isEmpty()) currentStudentName = sName;

            String dept = intent.getStringExtra("department");
            if (dept != null && !dept.isEmpty()) currentDepartment = dept;

            String yr = intent.getStringExtra("year");
            if (yr != null && !yr.isEmpty()) currentYear = yr;

            String ph = intent.getStringExtra("phone");
            if (ph != null && !ph.isEmpty()) currentPhone = ph;

            String em = intent.getStringExtra("email");
            if (em != null && !em.isEmpty()) currentEmail = em;
        }

        btnBack.setOnClickListener(v -> finish());

        if (btnEditStudent != null) {
            btnEditStudent.setOnClickListener(v -> showEditStudentDialog());
        }

        if (btnDeleteStudent != null) {
            btnDeleteStudent.setOnClickListener(v -> showDeleteConfirmationDialog());
        }

        findViewById(R.id.btnViewStudentWeeklySubmissions).setOnClickListener(v -> {
            Intent historyIntent = new Intent(StaffStudentDetails.this, StaffProgressHistory.class);
            historyIntent.putExtra("studentId", currentStudentId);
            historyIntent.putExtra("studentName", currentStudentName);
            historyIntent.putExtra("weeklyOnly", true);
            startActivity(historyIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudentDetails();
    }

    private void loadStudentDetails() {
        StudentModel student = repository.getStudentByRegNo(currentStudentId);

        if (student != null) {
            currentStudentName = student.getName();
            currentDepartment = student.getDepartment();
            currentYear = student.getYear();
            currentPhone = student.getPhone();
            currentEmail = student.getEmail();

            if (txtStudentName != null) txtStudentName.setText(student.getName());
            if (txtStudentId != null) txtStudentId.setText(student.getRegisterNumber());
            if (txtDepartment != null) txtDepartment.setText(student.getDepartment());
            if (txtYear != null) txtYear.setText(student.getYear());
            if (txtPhone != null) txtPhone.setText(student.getPhone());
            if (txtEmail != null) txtEmail.setText(student.getEmail());
            if (txtAttendance != null) txtAttendance.setText("95%");
            if (txtProgress != null) txtProgress.setText("90%");
            if (txtTasks != null) txtTasks.setText("1");
        } else {
            Intent intent = getIntent();
            if (intent != null) {
                if (txtStudentName != null) txtStudentName.setText(currentStudentName);
                if (txtStudentId != null) txtStudentId.setText(currentStudentId);
                if (txtDepartment != null) txtDepartment.setText(currentDepartment);
                if (txtYear != null) txtYear.setText(currentYear);
                if (txtPhone != null) txtPhone.setText(currentPhone);
                if (txtEmail != null) txtEmail.setText(currentEmail);
                if (txtAttendance != null) txtAttendance.setText(intent.getStringExtra("attendance") != null ? intent.getStringExtra("attendance") : "92%");
                if (txtProgress != null) txtProgress.setText(intent.getStringExtra("progress") != null ? intent.getStringExtra("progress") : "88%");
                if (txtTasks != null) txtTasks.setText(intent.getStringExtra("tasks") != null ? intent.getStringExtra("tasks") : "5");
            }
        }
    }

    private void showEditStudentDialog() {
        LinearLayout dialogCard = new LinearLayout(this);
        dialogCard.setOrientation(LinearLayout.VERTICAL);
        dialogCard.setBackgroundResource(R.drawable.bg_dialog_card);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setBackgroundResource(R.drawable.bg_dialog_header);
        header.setPadding(dpToPx(20), dpToPx(16), dpToPx(20), dpToPx(16));

        ImageView headerIcon = new ImageView(this);
        headerIcon.setImageResource(R.drawable.edit_symbol);
        headerIcon.setColorFilter(Color.WHITE);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(22), dpToPx(22));
        iconParams.setMarginEnd(dpToPx(12));
        headerIcon.setLayoutParams(iconParams);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Edit Student Profile");
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(18);
        tvTitle.setTypeface(null, Typeface.BOLD);

        header.addView(headerIcon);
        header.addView(tvTitle);

        // Body
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.WHITE);
        LinearLayout bodyLayout = new LinearLayout(this);
        bodyLayout.setOrientation(LinearLayout.VERTICAL);
        bodyLayout.setBackgroundColor(Color.WHITE);
        int padding = dpToPx(20);
        bodyLayout.setPadding(padding, dpToPx(16), padding, dpToPx(16));
        scrollView.addView(bodyLayout);

        bodyLayout.addView(createFieldLabel("Student Name *"));
        final EditText edtName = createStyledEditText("Name", currentStudentName != null ? currentStudentName : "", InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        bodyLayout.addView(edtName);

        bodyLayout.addView(createFieldLabel("Email Address *"));
        final EditText edtEmail = createStyledEditText("Email Address", currentEmail != null ? currentEmail : "", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        bodyLayout.addView(edtEmail);

        bodyLayout.addView(createFieldLabel("Phone Number *"));
        final EditText edtPhone = createStyledEditText("Phone Number", currentPhone != null ? currentPhone : "", InputType.TYPE_CLASS_PHONE);
        bodyLayout.addView(edtPhone);

        bodyLayout.addView(createFieldLabel("Department *"));
        final Spinner spDept = createStyledSpinner();
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, DEPARTMENTS) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.parseColor("#18233F"));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(Color.parseColor("#17112D"));
                return view;
            }
        };
        deptAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spDept.setAdapter(deptAdapter);
        if (currentDepartment != null) {
            for (int i = 0; i < DEPARTMENTS.length; i++) {
                if (DEPARTMENTS[i].equalsIgnoreCase(currentDepartment)) {
                    spDept.setSelection(i);
                    break;
                }
            }
        }
        bodyLayout.addView(spDept);

        bodyLayout.addView(createFieldLabel("Year *"));
        final Spinner spYear = createStyledSpinner();
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, YEARS) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.parseColor("#18233F"));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(Color.parseColor("#17112D"));
                return view;
            }
        };
        yearAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spYear.setAdapter(yearAdapter);
        if (currentYear != null) {
            for (int i = 0; i < YEARS.length; i++) {
                if (YEARS[i].equalsIgnoreCase(currentYear)) {
                    spYear.setSelection(i);
                    break;
                }
            }
        }
        bodyLayout.addView(spYear);

        // Footer
        LinearLayout footer = new LinearLayout(this);
        footer.setOrientation(LinearLayout.HORIZONTAL);
        footer.setBackgroundColor(Color.WHITE);
        footer.setPadding(dpToPx(20), dpToPx(8), dpToPx(20), dpToPx(16));

        Button btnCancel = new Button(this);
        btnCancel.setText("Cancel");
        btnCancel.setTextColor(Color.parseColor("#68738A"));
        btnCancel.setTextSize(14);
        btnCancel.setTypeface(null, Typeface.BOLD);
        btnCancel.setAllCaps(false);
        btnCancel.setBackgroundResource(R.drawable.bg_dialog_btn_cancel);
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(0, dpToPx(48), 1f);
        cancelParams.setMarginEnd(dpToPx(8));
        btnCancel.setLayoutParams(cancelParams);

        Button btnSave = new Button(this);
        btnSave.setText("Save Changes");
        btnSave.setTextColor(Color.WHITE);
        btnSave.setTextSize(14);
        btnSave.setTypeface(null, Typeface.BOLD);
        btnSave.setAllCaps(false);
        btnSave.setBackgroundResource(R.drawable.action_card);
        btnSave.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4935B8")));
        LinearLayout.LayoutParams saveParams = new LinearLayout.LayoutParams(0, dpToPx(48), 1f);
        saveParams.setMarginStart(dpToPx(8));
        btnSave.setLayoutParams(saveParams);

        footer.addView(btnCancel);
        footer.addView(btnSave);

        dialogCard.addView(header);
        dialogCard.addView(scrollView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        dialogCard.addView(footer);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogCard)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String newName = edtName.getText().toString().trim();
            String newEmail = edtEmail.getText().toString().trim();
            String newPhone = edtPhone.getText().toString().trim();
            String newDept = spDept.getSelectedItem() != null ? spDept.getSelectedItem().toString() : "CSE";
            String newYear = spYear.getSelectedItem() != null ? spYear.getSelectedItem().toString() : "III Year";

            if (newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            StudentModel studentToUpdate = repository.getStudentByRegNo(currentStudentId);
            if (studentToUpdate == null) {
                studentToUpdate = new StudentModel(newName, currentStudentId, newDept, newYear, newEmail, newPhone, "Aathi");
            } else {
                studentToUpdate.setName(newName);
                studentToUpdate.setEmail(newEmail);
                studentToUpdate.setPhone(newPhone);
                studentToUpdate.setDepartment(newDept);
                studentToUpdate.setYear(newYear);
            }

            repository.updateStudent(studentToUpdate);

            currentStudentName = newName;
            currentEmail = newEmail;
            currentPhone = newPhone;
            currentDepartment = newDept;
            currentYear = newYear;

            loadStudentDetails();
            setResult(RESULT_OK);
            Toast.makeText(this, "Student profile updated successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to remove " + (currentStudentName != null ? currentStudentName : "this Student") + "? This action cannot be undone.")
                .setPositiveButton("Remove", (d, which) -> {
                    if (currentStudentId != null && !currentStudentId.isEmpty()) {
                        repository.deleteStudentByRegNo(currentStudentId);
                    }
                    setResult(RESULT_OK);
                    Toast.makeText(this, (currentStudentName != null ? currentStudentName : "Student") + " removed successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.show();
        if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#DC3545"));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD);
        }
        if (dialog.getButton(AlertDialog.BUTTON_NEGATIVE) != null) {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#68738A"));
        }
    }

    private TextView createFieldLabel(String labelText) {
        TextView tv = new TextView(this);
        tv.setText(labelText);
        tv.setTextColor(Color.parseColor("#26334D"));
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextSize(13);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dpToPx(8), 0, dpToPx(4));
        tv.setLayoutParams(params);
        return tv;
    }

    private EditText createStyledEditText(String hint, String text, int inputType) {
        EditText edt = new EditText(this);
        edt.setHint(hint);
        if (text != null) edt.setText(text);
        if (inputType != 0) edt.setInputType(inputType);
        edt.setTextColor(Color.parseColor("#18233F"));
        edt.setHintTextColor(Color.parseColor("#9299AA"));
        edt.setTextSize(14);
        edt.setBackgroundResource(R.drawable.input_box);
        int pLR = dpToPx(14);
        int pTB = dpToPx(10);
        edt.setPadding(pLR, pTB, pLR, pTB);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(48)
        );
        params.setMargins(0, 0, 0, dpToPx(12));
        edt.setLayoutParams(params);
        return edt;
    }

    private Spinner createStyledSpinner() {
        Spinner sp = new Spinner(this);
        sp.setBackgroundResource(R.drawable.spinner_box);
        int pS = dpToPx(12);
        int pE = dpToPx(32);
        sp.setPadding(pS, 0, pE, 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(48)
        );
        params.setMargins(0, 0, 0, dpToPx(12));
        sp.setLayoutParams(params);
        return sp;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
