package com.example.trackpoint;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.util.ArrayList;

public class SuperAddStudentAdmin extends AppCompatActivity {

    private EditText edtStudentAdminName, edtStudentAdminId, edtStudentAdminPhone, edtStudentAdminEmail;
    private Spinner spStudentAdminDept, spStudentAdminYear, spAssignedStaffAdmin;
    private Button btnSaveStudentAdmin;
    private StudentRepository repository;

    private static final String[] DEPARTMENTS = {"Select Department", "CSE", "AI & DS", "ECE", "IT", "MECH"};
    private static final String[] YEARS = {"Select Year", "I Year", "II Year", "III Year", "IV Year"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_super_add_student_admin);

        repository = new StudentRepository(this);

        ImageView btnBack = findViewById(R.id.btnBack);
        edtStudentAdminName = findViewById(R.id.edtStudentAdminName);
        edtStudentAdminId = findViewById(R.id.edtStudentAdminId);
        spStudentAdminDept = findViewById(R.id.spStudentAdminDept);
        spStudentAdminYear = findViewById(R.id.spStudentAdminYear);
        spAssignedStaffAdmin = findViewById(R.id.spAssignedStaffAdmin);
        edtStudentAdminPhone = findViewById(R.id.edtStudentAdminPhone);
        edtStudentAdminEmail = findViewById(R.id.edtStudentAdminEmail);
        btnSaveStudentAdmin = findViewById(R.id.btnSaveStudentAdmin);

        ArrayAdapter<String> deptAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, DEPARTMENTS) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(position == 0 ? Color.parseColor("#888888") : Color.parseColor("#18233F"));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(position == 0 ? Color.parseColor("#888888") : Color.parseColor("#17112D"));
                return view;
            }
        };
        deptAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spStudentAdminDept.setAdapter(deptAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, YEARS) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(position == 0 ? Color.parseColor("#888888") : Color.parseColor("#18233F"));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(position == 0 ? Color.parseColor("#888888") : Color.parseColor("#17112D"));
                return view;
            }
        };
        yearAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spStudentAdminYear.setAdapter(yearAdapter);

        String preselectedDept = getIntent().getStringExtra("preselectedDept");
        String preselectedStaff = getIntent().getStringExtra("preselectedStaff");

        if (preselectedDept != null && !preselectedDept.isEmpty()) {
            boolean found = false;
            for (int i = 1; i < DEPARTMENTS.length; i++) {
                if (DEPARTMENTS[i].equalsIgnoreCase(preselectedDept)) {
                    spStudentAdminDept.setSelection(i);
                    found = true;
                    break;
                }
            }
            if (!found) spStudentAdminDept.setSelection(0);
        } else {
            spStudentAdminDept.setSelection(0);
        }

        spStudentAdminYear.setSelection(0);

        String selectedDeptStr = spStudentAdminDept.getSelectedItemPosition() > 0 ? spStudentAdminDept.getSelectedItem().toString() : "";
        updateStaffAdminSpinner(selectedDeptStr, preselectedStaff);

        spStudentAdminDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String deptStr = (position > 0) ? DEPARTMENTS[position] : "";
                updateStaffAdminSpinner(deptStr, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnBack.setOnClickListener(v -> finish());

        btnSaveStudentAdmin.setOnClickListener(v -> saveStudentAdmin());
    }

    private void updateStaffAdminSpinner(String department, String preselectedStaff) {
        ArrayList<StaffAdminModel> allStaff = repository.getAllStaffAdmins();
        ArrayList<String> staffNames = new ArrayList<>();
        staffNames.add("Select Staff Admin");

        for (StaffAdminModel sa : allStaff) {
            if (department == null || department.isEmpty()
                    || sa.getDepartment().equalsIgnoreCase(department)
                    || sa.getDepartment().toLowerCase().contains(department.toLowerCase())) {
                staffNames.add(sa.getName());
            }
        }

        ArrayAdapter<String> staffAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, staffNames.toArray(new String[0])) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(position == 0 ? Color.parseColor("#888888") : Color.parseColor("#18233F"));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(position == 0 ? Color.parseColor("#888888") : Color.parseColor("#17112D"));
                return view;
            }
        };
        staffAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spAssignedStaffAdmin.setAdapter(staffAdapter);

        if (preselectedStaff != null && !preselectedStaff.isEmpty()) {
            boolean found = false;
            for (int i = 1; i < staffNames.size(); i++) {
                if (staffNames.get(i).equalsIgnoreCase(preselectedStaff)) {
                    spAssignedStaffAdmin.setSelection(i);
                    found = true;
                    break;
                }
            }
            if (!found) spAssignedStaffAdmin.setSelection(0);
        } else {
            spAssignedStaffAdmin.setSelection(0);
        }
    }

    private void saveStudentAdmin() {
        String name = edtStudentAdminName.getText().toString().trim();
        String id = edtStudentAdminId.getText().toString().trim();
        String phone = edtStudentAdminPhone.getText().toString().trim();
        String email = edtStudentAdminEmail.getText().toString().trim();

        if (name.isEmpty() || id.isEmpty()) {
            Toast.makeText(this, "Please enter Student Admin Name and Admin ID", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spStudentAdminDept.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a department", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spStudentAdminYear.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a year", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spAssignedStaffAdmin.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select an assigned Staff Admin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (repository.adminIdExists(id)) {
            Toast.makeText(this, "Admin ID already exists in database!", Toast.LENGTH_SHORT).show();
            return;
        }

        String dept = spStudentAdminDept.getSelectedItem().toString();
        String year = spStudentAdminYear.getSelectedItem().toString();
        String assignedStaff = spAssignedStaffAdmin.getSelectedItem().toString();

        if (phone.isEmpty()) {
            phone = "98765 00000";
        }
        if (email.isEmpty()) {
            email = name.toLowerCase().replaceAll("\\s+", ".") + "@college.edu";
        }

        StudentAdminModel newAdmin = new StudentAdminModel(name, id, dept, year, email, phone.startsWith("+91") ? phone : "+91 " + phone);
        newAdmin.setStudentsAssignedCount(0);

        long rowId = repository.addStudentAdmin(newAdmin);

        if (rowId > 0) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("adminName", name);
            resultIntent.putExtra("adminId", id);
            resultIntent.putExtra("department", dept);
            resultIntent.putExtra("year", year);
            resultIntent.putExtra("phone", phone);
            resultIntent.putExtra("email", email);
            resultIntent.putExtra("assignedStaff", assignedStaff);

            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, name + " saved to SQLite database!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save Student Admin", Toast.LENGTH_SHORT).show();
        }
    }
}
