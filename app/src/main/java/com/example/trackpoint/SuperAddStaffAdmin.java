package com.example.trackpoint;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class SuperAddStaffAdmin extends AppCompatActivity {

    private EditText edtStaffName, edtStaffId, edtStaffEmail, edtStaffPhone;
    private Spinner spStaffDept;
    private Button btnSaveStaffAdmin;

    private static final String[] DEPARTMENTS = {"Select Department", "CSE", "AI & DS", "ECE", "IT", "MECH"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_super_add_staff_admin);

        ImageView btnBack = findViewById(R.id.btnBack);
        edtStaffName = findViewById(R.id.edtStaffName);
        edtStaffId = findViewById(R.id.edtStaffId);
        spStaffDept = findViewById(R.id.spStaffDept);
        edtStaffEmail = findViewById(R.id.edtStaffEmail);
        edtStaffPhone = findViewById(R.id.edtStaffPhone);
        btnSaveStaffAdmin = findViewById(R.id.btnSaveStaffAdmin);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.spinner_item,
                DEPARTMENTS
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                if (position == 0) {
                    view.setTextColor(Color.parseColor("#888888"));
                } else {
                    view.setTextColor(Color.parseColor("#18233F"));
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                if (position == 0) {
                    view.setTextColor(Color.parseColor("#888888"));
                } else {
                    view.setTextColor(Color.parseColor("#17112D"));
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spStaffDept.setAdapter(adapter);

        String preselectedDept = getIntent().getStringExtra("preselectedDept");
        if (preselectedDept != null && !preselectedDept.isEmpty()) {
            boolean found = false;
            for (int i = 1; i < DEPARTMENTS.length; i++) {
                if (DEPARTMENTS[i].equalsIgnoreCase(preselectedDept)) {
                    spStaffDept.setSelection(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                spStaffDept.setSelection(0);
            }
        } else {
            spStaffDept.setSelection(0);
        }

        btnBack.setOnClickListener(v -> finish());

        btnSaveStaffAdmin.setOnClickListener(v -> saveStaffAdmin());
    }

    private void saveStaffAdmin() {
        String name = edtStaffName.getText().toString().trim();
        String id = edtStaffId.getText().toString().trim();
        String email = edtStaffEmail.getText().toString().trim();
        String phone = edtStaffPhone.getText().toString().trim();

        if (name.isEmpty() || id.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please enter Name, Staff ID, and Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spStaffDept.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a department", Toast.LENGTH_SHORT).show();
            return;
        }

        String dept = spStaffDept.getSelectedItem().toString();

        if (phone.isEmpty()) {
            phone = "98765 00000";
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("staffName", name);
        resultIntent.putExtra("staffId", id);
        resultIntent.putExtra("department", dept);
        resultIntent.putExtra("email", email);
        resultIntent.putExtra("phone", phone);

        setResult(RESULT_OK, resultIntent);
        Toast.makeText(this, name + " assigned to " + dept, Toast.LENGTH_SHORT).show();
        finish();
    }
}
