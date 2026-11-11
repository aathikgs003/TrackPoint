package com.example.trackpoint;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProgressHistory extends AppCompatActivity {

    ImageView btnBack;
    Spinner spStudent, spMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_history);

        btnBack = findViewById(R.id.btnBack);
        spStudent = findViewById(R.id.spStudent);
        spMonth = findViewById(R.id.spMonth);

        setSpinner(spStudent, new String[]{
                "Arun Kumar",
                "Divya S",
                "Karthik R",
                "Nithya P",
                "Praveen M"
        });

        setSpinner(spMonth, new String[]{
                "Jan 2025",
                "Feb 2025",
                "Mar 2025",
                "Apr 2025"
        });

        btnBack.setOnClickListener(v -> finish());

        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String student = spStudent.getSelectedItem() != null ? spStudent.getSelectedItem().toString() : "";
                String month = spMonth.getSelectedItem() != null ? spMonth.getSelectedItem().toString() : "";
                Toast.makeText(ProgressHistory.this, "Filtering history for " + student + " (" + month + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spStudent.setOnItemSelectedListener(filterListener);
        spMonth.setOnItemSelectedListener(filterListener);
    }

    private void setSpinner(Spinner spinner, String[] items) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                items
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }
}
