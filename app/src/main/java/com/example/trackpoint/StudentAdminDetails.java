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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class StudentAdminDetails extends AppCompatActivity {

    private ImageView btnBack, btnEditAdmin, btnDeleteAdmin;
    private Button btnViewStudents, btnAttendance, btnProgress;

    private TextView txtAdminName, txtAdminId, txtAdminDepartment, txtAdminYear, txtAdminPhone, txtAdminEmail, txtStudentsAssigned;

    private StudentRepository repository;
    private String adminNameParam, adminIdParam, deptParam, yearParam, phoneParam, emailParam;

    private static final String[] DEPARTMENTS = {"CSE", "AI & DS", "ECE", "IT", "MECH"};
    private static final String[] YEARS = {"I Year", "II Year", "III Year", "IV Year"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(
                        getWindow(),
                        getWindow().getDecorView()
                );

        controller.setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_student_admin_details);

        repository = new StudentRepository(this);

        LinearLayout rootLayout = findViewById(R.id.main);
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (view, windowInsets) -> {
                Insets systemBars = windowInsets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                );

                view.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        systemBars.bottom
                );

                return windowInsets;
            });
        }

        btnBack = findViewById(R.id.btnBack);
        btnEditAdmin = findViewById(R.id.btnEditAdmin);
        btnDeleteAdmin = findViewById(R.id.btnDeleteAdmin);
        btnViewStudents = findViewById(R.id.btnViewStudents);
        btnAttendance = findViewById(R.id.btnAttendance);
        btnProgress = findViewById(R.id.btnProgress);

        txtAdminName = findViewById(R.id.txtAdminName);
        txtAdminId = findViewById(R.id.txtAdminId);
        txtAdminDepartment = findViewById(R.id.txtAdminDepartment);
        txtAdminYear = findViewById(R.id.txtAdminYear);
        txtAdminPhone = findViewById(R.id.txtAdminPhone);
        txtAdminEmail = findViewById(R.id.txtAdminEmail);
        txtStudentsAssigned = findViewById(R.id.txtStudentsAssigned);

        Intent intent = getIntent();
        if (intent != null) {
            adminNameParam = intent.getStringExtra("adminName");
            adminIdParam = intent.getStringExtra("adminId");
            deptParam = intent.getStringExtra("department");
            yearParam = intent.getStringExtra("year");
            phoneParam = intent.getStringExtra("phone");
            emailParam = intent.getStringExtra("email");
        }

        btnBack.setOnClickListener(v -> finish());

        if (btnEditAdmin != null) {
            btnEditAdmin.setOnClickListener(v -> showEditAdminDialog());
        }

        if (btnDeleteAdmin != null) {
            btnDeleteAdmin.setOnClickListener(v -> showDeleteConfirmationDialog());
        }

        btnViewStudents.setOnClickListener(v -> {
            Intent viewStudentsIntent = new Intent(
                    StudentAdminDetails.this, StaffAdminStudents.class
            );
            viewStudentsIntent.putExtra("adminName", adminNameParam);
            startActivity(viewStudentsIntent);
        });

        btnAttendance.setOnClickListener(v -> {
            Intent attIntent = new Intent(StudentAdminDetails.this, StaffAttendanceReport.class);
            attIntent.putExtra("adminName", adminNameParam);
            startActivity(attIntent);
        });

        btnProgress.setOnClickListener(v -> {
            Intent progressIntent = new Intent(StudentAdminDetails.this, StaffProgressHistory.class);
            progressIntent.putExtra("adminName", adminNameParam);
            progressIntent.putExtra("dailyAdminMode", true);
            startActivity(progressIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAdminDetails();
    }

    private void loadAdminDetails() {
        String queryKey = adminIdParam != null && !adminIdParam.isEmpty() ? adminIdParam : adminNameParam;
        if (queryKey == null || queryKey.isEmpty()) {
            queryKey = "Aathi";
        }

        StudentAdminModel admin = repository.getStudentAdminById(queryKey);

        if (admin != null) {
            adminNameParam = admin.getName();
            adminIdParam = admin.getAdminId();
            deptParam = admin.getDepartment();
            yearParam = admin.getYear();
            phoneParam = admin.getPhone();
            emailParam = admin.getEmail();

            if (txtAdminName != null) txtAdminName.setText(admin.getName());
            if (txtAdminId != null) txtAdminId.setText(admin.getAdminId());
            if (txtAdminDepartment != null) txtAdminDepartment.setText(admin.getDepartment());
            if (txtAdminYear != null) txtAdminYear.setText(admin.getYear());
            if (txtAdminPhone != null) txtAdminPhone.setText(admin.getPhone());
            if (txtAdminEmail != null) txtAdminEmail.setText(admin.getEmail());

            int realCount = repository.getAssignedStudentsCountForAdmin(admin.getName());
            if (txtStudentsAssigned != null) txtStudentsAssigned.setText(String.valueOf(realCount));
        } else {
            if (txtAdminName != null && adminNameParam != null) txtAdminName.setText(adminNameParam);
            if (txtAdminId != null && adminIdParam != null) txtAdminId.setText(adminIdParam);
            if (txtAdminDepartment != null && deptParam != null) txtAdminDepartment.setText(deptParam);
            if (txtAdminYear != null && yearParam != null) txtAdminYear.setText(yearParam);
            if (txtAdminPhone != null && phoneParam != null) txtAdminPhone.setText(phoneParam);
            if (txtAdminEmail != null && emailParam != null) txtAdminEmail.setText(emailParam);

            int realCount = repository.getAssignedStudentsCountForAdmin(adminNameParam != null ? adminNameParam : "Aathi");
            if (txtStudentsAssigned != null) txtStudentsAssigned.setText(String.valueOf(realCount));
        }
    }

    private void showEditAdminDialog() {
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
        tvTitle.setText("Edit Student Admin Profile");
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

        bodyLayout.addView(createFieldLabel("Student Admin Name *"));
        final EditText edtName = createStyledEditText("Name", adminNameParam != null ? adminNameParam : "", InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        bodyLayout.addView(edtName);

        bodyLayout.addView(createFieldLabel("Email Address *"));
        final EditText edtEmail = createStyledEditText("Email Address", emailParam != null ? emailParam : "", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        bodyLayout.addView(edtEmail);

        bodyLayout.addView(createFieldLabel("Phone Number *"));
        final EditText edtPhone = createStyledEditText("Phone Number", phoneParam != null ? phoneParam : "", InputType.TYPE_CLASS_PHONE);
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
        if (deptParam != null) {
            for (int i = 0; i < DEPARTMENTS.length; i++) {
                if (DEPARTMENTS[i].equalsIgnoreCase(deptParam)) {
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
        if (yearParam != null) {
            for (int i = 0; i < YEARS.length; i++) {
                if (YEARS[i].equalsIgnoreCase(yearParam)) {
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

            String key = adminIdParam != null && !adminIdParam.isEmpty() ? adminIdParam : (adminNameParam != null ? adminNameParam : "Aathi");
            StudentAdminModel adminToUpdate = repository.getStudentAdminById(key);
            if (adminToUpdate == null) {
                adminToUpdate = new StudentAdminModel(newName, adminIdParam != null ? adminIdParam : "SADM001", newDept, newYear, newEmail, newPhone);
            } else {
                adminToUpdate.setName(newName);
                adminToUpdate.setEmail(newEmail);
                adminToUpdate.setPhone(newPhone);
                adminToUpdate.setDepartment(newDept);
                adminToUpdate.setYear(newYear);
            }

            repository.updateStudentAdmin(adminToUpdate);

            adminNameParam = newName;
            emailParam = newEmail;
            phoneParam = newPhone;
            deptParam = newDept;
            yearParam = newYear;

            loadAdminDetails();
            setResult(RESULT_OK);
            Toast.makeText(this, "Student Admin profile updated successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to remove " + (adminNameParam != null ? adminNameParam : "this Student Admin") + "? This action cannot be undone.")
                .setPositiveButton("Remove", (d, which) -> {
                    if (adminIdParam != null && !adminIdParam.isEmpty()) {
                        repository.deleteStudentAdminByAdminId(adminIdParam);
                    } else if (emailParam != null && !emailParam.isEmpty()) {
                        repository.deleteStudentAdminByAdminId(emailParam);
                    }
                    setResult(RESULT_OK);
                    Toast.makeText(this, (adminNameParam != null ? adminNameParam : "Student Admin") + " removed successfully", Toast.LENGTH_SHORT).show();
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
