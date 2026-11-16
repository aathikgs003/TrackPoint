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

public class SuperAdminStaffDetails extends AppCompatActivity {

    private TextView txtStaffName, txtStaffId, txtStaffDepartment, txtStaffEmail, txtStaffPhone, txtStaffStudentAdminCount;
    private StudentRepository repository;
    private String staffId, staffName, department, email, phone;
    private int studentAdminCount;

    private static final String[] DEPARTMENTS = {"CSE", "AI & DS", "ECE", "IT", "MECH"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_super_admin_staff_details);

        repository = new StudentRepository(this);

        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView btnEditStaff = findViewById(R.id.btnEditStaff);
        ImageView btnDeleteStaff = findViewById(R.id.btnDeleteStaff);

        txtStaffName = findViewById(R.id.txtStaffName);
        txtStaffId = findViewById(R.id.txtStaffId);
        txtStaffDepartment = findViewById(R.id.txtStaffDepartment);
        txtStaffEmail = findViewById(R.id.txtStaffEmail);
        txtStaffPhone = findViewById(R.id.txtStaffPhone);
        txtStaffStudentAdminCount = findViewById(R.id.txtStaffStudentAdminCount);
        Button btnViewAssignedStudentAdmins = findViewById(R.id.btnViewAssignedStudentAdmins);

        btnBack.setOnClickListener(v -> finish());

        staffName = getIntent().getStringExtra("staffName");
        staffId = getIntent().getStringExtra("staffId");
        department = getIntent().getStringExtra("department");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        studentAdminCount = getIntent().getIntExtra("studentAdminCount", 0);

        loadStaffDetails();

        if (btnEditStaff != null) {
            btnEditStaff.setOnClickListener(v -> showEditStaffDialog());
        }

        if (btnDeleteStaff != null) {
            btnDeleteStaff.setOnClickListener(v -> showDeleteConfirmationDialog());
        }

        btnViewAssignedStudentAdmins.setOnClickListener(v -> {
            Intent intent = new Intent(this, SuperAdminStudentAdmins.class);
            intent.putExtra("filterDepartment", department);
            intent.putExtra("filterStaffAdmin", staffName);
            startActivity(intent);
        });
    }

    private void loadStaffDetails() {
        StaffAdminModel staff = repository.getStaffAdminByEmail(staffId != null ? staffId : email);
        if (staff != null) {
            staffName = staff.getName();
            staffId = staff.getStaffId();
            department = staff.getDepartment();
            email = staff.getEmail();
            phone = staff.getPhone();
        }

        if (staffName != null) txtStaffName.setText(staffName);
        if (staffId != null) txtStaffId.setText(staffId);
        if (department != null) txtStaffDepartment.setText(department);
        if (email != null) txtStaffEmail.setText(email);
        if (phone != null) txtStaffPhone.setText(phone);
        txtStaffStudentAdminCount.setText(studentAdminCount + " Student Admins");
    }

    private void showEditStaffDialog() {
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
        tvTitle.setText("Edit Staff Admin Profile");
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

        bodyLayout.addView(createFieldLabel("Full Name *"));
        final EditText edtName = createStyledEditText("Staff Name", staffName != null ? staffName : "", InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        bodyLayout.addView(edtName);

        bodyLayout.addView(createFieldLabel("Email Address *"));
        final EditText edtEmail = createStyledEditText("Email Address", email != null ? email : "", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        bodyLayout.addView(edtEmail);

        bodyLayout.addView(createFieldLabel("Phone Number *"));
        final EditText edtPhone = createStyledEditText("Phone Number", phone != null ? phone : "", InputType.TYPE_CLASS_PHONE);
        bodyLayout.addView(edtPhone);

        bodyLayout.addView(createFieldLabel("Department *"));
        final Spinner spDept = createStyledSpinner();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, DEPARTMENTS) {
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
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spDept.setAdapter(adapter);

        if (department != null) {
            for (int i = 0; i < DEPARTMENTS.length; i++) {
                if (DEPARTMENTS[i].equalsIgnoreCase(department)) {
                    spDept.setSelection(i);
                    break;
                }
            }
        }
        bodyLayout.addView(spDept);

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

            if (newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            StaffAdminModel staffToUpdate = repository.getStaffAdminByEmail(staffId != null ? staffId : email);
            if (staffToUpdate == null) {
                staffToUpdate = new StaffAdminModel(newName, staffId != null ? staffId : "STF001", newDept, newEmail, newPhone);
            } else {
                staffToUpdate.setName(newName);
                staffToUpdate.setEmail(newEmail);
                staffToUpdate.setPhone(newPhone);
                staffToUpdate.setDepartment(newDept);
            }

            repository.updateStaffAdmin(staffToUpdate);

            staffName = newName;
            email = newEmail;
            phone = newPhone;
            department = newDept;

            loadStaffDetails();
            setResult(RESULT_OK);
            Toast.makeText(this, "Staff Admin profile updated successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to remove " + (staffName != null ? staffName : "this Staff Admin") + "? This action cannot be undone.")
                .setPositiveButton("Remove", (d, which) -> {
                    if (staffId != null && !staffId.isEmpty()) {
                        repository.deleteStaffAdminByStaffId(staffId);
                    } else if (email != null && !email.isEmpty()) {
                        repository.deleteStaffAdminByStaffId(email);
                    }
                    setResult(RESULT_OK);
                    Toast.makeText(this, (staffName != null ? staffName : "Staff Admin") + " removed successfully", Toast.LENGTH_SHORT).show();
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
