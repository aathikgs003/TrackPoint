package com.example.trackpoint;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StudentProgressHistoryActivity extends AppCompatActivity {

    ImageView btnBack;
    TextView tabAll, tabCompleted, tabInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_progress_history);

        btnBack = findViewById(R.id.btnBack);
        tabAll = findViewById(R.id.tabAll);
        tabCompleted = findViewById(R.id.tabCompleted);
        tabInProgress = findViewById(R.id.tabInProgress);

        btnBack.setOnClickListener(v -> finish());

        tabAll.setOnClickListener(v -> selectTab("ALL"));
        tabCompleted.setOnClickListener(v -> selectTab("COMPLETED"));
        tabInProgress.setOnClickListener(v -> selectTab("IN_PROGRESS"));
    }

    private void selectTab(String tab) {
        tabAll.setBackgroundResource(R.drawable.action_card);
        tabAll.setTextColor(Color.parseColor("#52627A"));

        tabCompleted.setBackgroundResource(R.drawable.action_card);
        tabCompleted.setTextColor(Color.parseColor("#52627A"));

        tabInProgress.setBackgroundResource(R.drawable.action_card);
        tabInProgress.setTextColor(Color.parseColor("#52627A"));

        if (tab.equals("ALL")) {
            tabAll.setBackgroundResource(R.drawable.add_button);
            tabAll.setTextColor(Color.WHITE);
        } else if (tab.equals("COMPLETED")) {
            tabCompleted.setBackgroundResource(R.drawable.add_button);
            tabCompleted.setTextColor(Color.WHITE);
        } else {
            tabInProgress.setBackgroundResource(R.drawable.add_button);
            tabInProgress.setTextColor(Color.WHITE);
        }

        Toast.makeText(this, "Filter: " + tab, Toast.LENGTH_SHORT).show();
    }
}
