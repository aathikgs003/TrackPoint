package com.example.trackpoint;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StudentAttendanceActivity extends AppCompatActivity {

    ImageView btnBack, imgTodayStatusIcon;
    TextView tvTodayDate, tvTodayStatus, tvTodayNote;
    LinearLayout layoutTodayBadge;

    TextView btnPrevMonth, btnNextMonth, tvCurrentMonth;
    GridLayout calendarGrid;

    int currentMonth;
    int currentYear;

    int todayDay;
    int todayMonth;
    int todayYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);

        btnBack = findViewById(R.id.btnBack);

        tvTodayDate = findViewById(R.id.tvTodayDate);
        tvTodayStatus = findViewById(R.id.tvTodayStatus);
        tvTodayNote = findViewById(R.id.tvTodayNote);
        imgTodayStatusIcon = findViewById(R.id.imgTodayStatusIcon);
        layoutTodayBadge = findViewById(R.id.layoutTodayBadge);

        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        calendarGrid = findViewById(R.id.calendarGrid);

        btnBack.setOnClickListener(v -> finish());

        // Initialize today's date info
        Calendar todayCal = Calendar.getInstance();
        todayDay = todayCal.get(Calendar.DAY_OF_MONTH);
        todayMonth = todayCal.get(Calendar.MONTH);
        todayYear = todayCal.get(Calendar.YEAR);

        currentMonth = todayMonth;
        currentYear = todayYear;

        loadTodayAttendance();
        updateCalendar();

        if (btnPrevMonth != null) {
            btnPrevMonth.setOnClickListener(v -> {
                if (currentMonth == 0) {
                    currentMonth = 11;
                    currentYear--;
                } else {
                    currentMonth--;
                }
                updateCalendar();
            });
        }

        if (btnNextMonth != null) {
            btnNextMonth.setOnClickListener(v -> {
                if (currentMonth == 11) {
                    currentMonth = 0;
                    currentYear++;
                } else {
                    currentMonth++;
                }
                updateCalendar();
            });
        }
    }

    private void loadTodayAttendance() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());
        String currentDateStr = sdf.format(new Date());

        if (tvTodayDate != null) {
            tvTodayDate.setText(currentDateStr);
        }

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
            if (tvTodayStatus != null) {
                tvTodayStatus.setText("HOLIDAY");
                tvTodayStatus.setTextColor(Color.parseColor("#9299AA"));
            }
            if (tvTodayNote != null) {
                tvTodayNote.setText("Weekend Holiday");
            }
            if (imgTodayStatusIcon != null) {
                imgTodayStatusIcon.setImageResource(R.drawable.timeline_dot);
                imgTodayStatusIcon.setColorFilter(Color.parseColor("#9299AA"));
            }
            if (layoutTodayBadge != null) {
                layoutTodayBadge.setBackgroundResource(R.drawable.card_purple);
            }
        } else {
            if (tvTodayStatus != null) {
                tvTodayStatus.setText("PRESENT");
                tvTodayStatus.setTextColor(Color.parseColor("#10B23E"));
            }
            if (tvTodayNote != null) {
                tvTodayNote.setText("Marked at 09:15 AM");
            }
            if (imgTodayStatusIcon != null) {
                imgTodayStatusIcon.setImageResource(R.drawable.check_circle_symbol);
                imgTodayStatusIcon.setColorFilter(Color.parseColor("#10B23E"));
            }
            if (layoutTodayBadge != null) {
                layoutTodayBadge.setBackgroundResource(R.drawable.card_green);
            }
        }
    }

    private void updateCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, currentYear);
        cal.set(Calendar.MONTH, currentMonth);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        if (tvCurrentMonth != null) {
            tvCurrentMonth.setText(sdf.format(cal.getTime()));
        }

        if (calendarGrid == null) return;
        calendarGrid.removeAllViews();

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        float density = getResources().getDisplayMetrics().density;
        int cellHeight = (int) (44 * density);
        int margin = (int) (3 * density);

        for (int i = 1; i < firstDayOfWeek; i++) {
            View emptyView = new View(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = cellHeight;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(margin, margin, margin, margin);
            emptyView.setLayoutParams(params);
            calendarGrid.addView(emptyView);
        }

        for (int day = 1; day <= totalDays; day++) {
            TextView dayView = new TextView(this);
            dayView.setText(String.valueOf(day));
            dayView.setGravity(Gravity.CENTER);
            dayView.setTextSize(14f);
            dayView.setTypeface(null, android.graphics.Typeface.BOLD);

            cal.set(Calendar.DAY_OF_MONTH, day);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

            boolean isToday = (currentYear == todayYear && currentMonth == todayMonth && day == todayDay);
            boolean isFuture = false;

            if (currentYear > todayYear) {
                isFuture = true;
            } else if (currentYear == todayYear) {
                if (currentMonth > todayMonth) {
                    isFuture = true;
                } else if (currentMonth == todayMonth) {
                    if (day > todayDay) {
                        isFuture = true;
                    }
                }
            }

            String status;

            if (isFuture) {
                if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
                    status = "Upcoming Weekend";
                    dayView.setTextColor(Color.parseColor("#9299AA"));
                    dayView.setBackgroundResource(R.drawable.action_card);
                } else {
                    status = "Upcoming Day";
                    dayView.setTextColor(Color.parseColor("#68738A"));
                    dayView.setBackgroundResource(R.drawable.action_card);
                }
            } else {
                if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
                    status = "Holiday (Weekend)";
                    dayView.setTextColor(Color.parseColor("#7A8294"));
                    dayView.setBackgroundResource(R.drawable.bg_day_holiday);
                } else if (day == 8 || day == 18 || day == 22) {
                    status = "Absent";
                    dayView.setTextColor(Color.parseColor("#D94343"));
                    dayView.setBackgroundResource(R.drawable.bg_day_absent);
                } else {
                    status = "Present";
                    dayView.setTextColor(Color.parseColor("#10B23E"));
                    dayView.setBackgroundResource(R.drawable.bg_day_present);
                }
            }

            if (isToday) {
                dayView.setBackgroundResource(R.drawable.bg_day_today);
            }

            final String finalStatus = status;
            SimpleDateFormat daySdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            String dateStr = daySdf.format(cal.getTime());

            dayView.setOnClickListener(v ->
                    Toast.makeText(this, dateStr + "\nStatus: " + finalStatus, Toast.LENGTH_SHORT).show()
            );

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = cellHeight;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(margin, margin, margin, margin);
            dayView.setLayoutParams(params);

            calendarGrid.addView(dayView);
        }
    }
}
