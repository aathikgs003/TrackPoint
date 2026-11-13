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
import java.util.List;

public class SuperAddStudentAdmin extends AppCompatActivity {

    private EditText edtStudentAdminName, edtStudentAdminId, edtStudentAdminPhone, edtStudentAdminEmail;
    private Spinner spStudentAdminDept, spStudentAdminYear, spAssignedStaffAdmin;
    private Button btnSaveStudentAdmin;

    private static final String[] DEPARTMENTS = {"Select Department", "CSE", "AI & DS", "ECE", "IT", "MECH"};
    private static final String[] YEARS = {"Select Year", "II Year", "III Year", "IV Year"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_super_add_student_admin);

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

        String initialDept = spStudentAdminDept.getSelectedItemPosition() > 0 ? spStudentAdminDept.getSelectedItem().toString() : "CSE";
        updateStaffAdminSpinner(initialDept, preselectedStaff);

        spStudentAdminDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedDept = DEPARTMENTS[position];
                    updateStaffAdminSpinner(selectedDept, null);
                } else {
                    updateStaffAdminSpinner("CSE", null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnBack.setOnClickListener(v -> finish());

        btnSaveStudentAdmin.setOnClickListener(v -> saveStudentAdmin());
    }

    private void updateStaffAdminSpinner(String department, String preselectedStaff) {
        List<String> staffList = new ArrayList<>();
        staffList.add("Select Staff Admin");

        if ("CSE".equalsIgnoreCase(department)) {
            staffList.add("Prof. Ramesh K");
            staffList.add("Dr. Raja");
        } else if ("AI & DS".equalsIgnoreCase(department)) {
            staffList.add("Prof. Meena R");
        } else if ("ECE".equalsIgnoreCase(department)) {
            staffList.add("Prof. Vijay S");
        } else if ("IT".equalsIgnoreCase(department)) {
            staffList.add("Prof. Anitha P");
        } else if ("MECH".equalsIgnoreCase(department)) {
            staffList.add("Prof. Karthikeyan M");
        } else {
            staffList.add("Prof. Ramesh K");
            staffList.add("Dr. Raja");
            staffList.add("Prof. Meena R");
            staffList.add("Prof. Vijay S");
            staffList.add("Prof. Anitha P");
        }

        ArrayAdapter<String> staffAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, staffList) {
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
            for (int i = 1; i < staffList.size(); i++) {
                if (staffList.get(i).equalsIgnoreCase(preselectedStaff)) {
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
            Toast.makeText(this, "Please select a staff admin", Toast.LENGTH_SHORT).show();
            return;
        }

        String dept = spStudentAdminDept.getSelectedItem().toString();
        String year = spStudentAdminYear.getSelectedItem().toString();
        String staff = spAssignedStaffAdmin.getSelectedItem().toString();

        if (phone.isEmpty()) {
            phone = "98765 00000";
        }
        if (email.isEmpty()) {
            email = name.toLowerCase().replaceAll("\\s+", ".") + "@college.edu";
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("adminName", name);
        resultIntent.putExtra("adminId", id);
        resultIntent.putExtra("department", dept);
        resultIntent.putExtra("year", year);
        resultIntent.putExtra("assignedStaffAdmin", staff);
        resultIntent.putExtra("phone", phone);
        resultIntent.putExtra("email", email);

        setResult(RESULT_OK, resultIntent);
        Toast.makeText(this, name + " assigned under " + staff, Toast.LENGTH_SHORT).show();
        finish();
    }
}
