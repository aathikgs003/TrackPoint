package com.example.trackpoint;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MyStudents extends AppCompatActivity {
    ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystudents);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}
