package com.example.trackpoint;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Notifications extends AppCompatActivity {

    ImageView btnBack;
    TextView tvUnreadCount, btnMarkAllRead;

    LinearLayout notification1, notification2, notification3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        btnBack = findViewById(R.id.btnBack);
        tvUnreadCount = findViewById(R.id.tvUnreadCount);
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);

        notification1 = findViewById(R.id.notification1);
        notification2 = findViewById(R.id.notification2);
        notification3 = findViewById(R.id.notification3);

        btnBack.setOnClickListener(v -> finish());

        btnMarkAllRead.setOnClickListener(v -> markAllRead());

        notification1.setOnClickListener(v -> markRead(notification1));
        notification2.setOnClickListener(v -> markRead(notification2));
        notification3.setOnClickListener(v -> markRead(notification3));
    }

    private void markRead(LinearLayout notification) {
        if (notification == null) return;

        notification.setBackgroundResource(
                R.drawable.notification_read
        );

        if (notification.getChildCount() > 2) {
            notification.getChildAt(2).setVisibility(android.view.View.GONE);
        }

        Toast.makeText(this,
                "Notification marked as read",
                Toast.LENGTH_SHORT).show();

        updateUnreadCount();
    }

    private void markAllRead() {

        markReadWithoutToast(notification1);
        markReadWithoutToast(notification2);
        markReadWithoutToast(notification3);

        Toast.makeText(this,
                "All notifications marked as read",
                Toast.LENGTH_SHORT).show();

        updateUnreadCount();
    }

    private void markReadWithoutToast(LinearLayout notification) {
        if (notification == null) return;

        notification.setBackgroundResource(
                R.drawable.notification_read
        );

        if (notification.getChildCount() > 2) {
            notification.getChildAt(2).setVisibility(android.view.View.GONE);
        }
    }

    private void updateUnreadCount() {

        int count = 0;

        if (notification1 != null && notification1.getChildCount() > 2 &&
                notification1.getChildAt(2).getVisibility() == android.view.View.VISIBLE) count++;
        if (notification2 != null && notification2.getChildCount() > 2 &&
                notification2.getChildAt(2).getVisibility() == android.view.View.VISIBLE) count++;
        if (notification3 != null && notification3.getChildCount() > 2 &&
                notification3.getChildAt(2).getVisibility() == android.view.View.VISIBLE) count++;

        if (tvUnreadCount != null) {
            tvUnreadCount.setText(count + " Unread Notifications");
        }
    }
}
