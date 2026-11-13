package com.example.trackpoint;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class SuperAdminProfileActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvSuperAdminName, tvSuperAdminSubtitle;
    private TextView tvSuperAdminInfoName, tvSuperAdminEmail, tvSuperAdminPhone, tvSuperAdminRole;
    private Button btnEditProfile;

    private static final String PREF_NAME = "SuperAdminPrefs";
    private static final String KEY_NAME = "super_admin_name";
    private static final String KEY_EMAIL = "super_admin_email";
    private static final String KEY_PHONE = "super_admin_phone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#4935B8"));
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_super_admin_profile);

        btnBack = findViewById(R.id.btnBack);
        tvSuperAdminName = findViewById(R.id.tvSuperAdminName);
        tvSuperAdminSubtitle = findViewById(R.id.tvSuperAdminSubtitle);
        tvSuperAdminInfoName = findViewById(R.id.tvSuperAdminInfoName);
        tvSuperAdminEmail = findViewById(R.id.tvSuperAdminEmail);
        tvSuperAdminPhone = findViewById(R.id.tvSuperAdminPhone);
        tvSuperAdminRole = findViewById(R.id.tvSuperAdminRole);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        btnBack.setOnClickListener(v -> finish());

        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        loadProfileData();
    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String name = prefs.getString(KEY_NAME, "Super Admin");
        String email = prefs.getString(KEY_EMAIL, "admin@college.edu");
        String phone = prefs.getString(KEY_PHONE, "98765 43210");

        tvSuperAdminName.setText(name);
        tvSuperAdminInfoName.setText(name);
        tvSuperAdminEmail.setText(email);
        tvSuperAdminPhone.setText(phone);
        tvSuperAdminRole.setText("Super Admin");
        tvSuperAdminSubtitle.setText("College Administrator");
    }

    private void showEditProfileDialog() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String currentName = prefs.getString(KEY_NAME, "Super Admin");
        String currentEmail = prefs.getString(KEY_EMAIL, "admin@college.edu");
        String currentPhone = prefs.getString(KEY_PHONE, "98765 43210");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        final EditText edtName = new EditText(this);
        edtName.setHint("Name");
        edtName.setText(currentName);
        layout.addView(edtName);

        final EditText edtEmail = new EditText(this);
        edtEmail.setHint("Email");
        edtEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        edtEmail.setText(currentEmail);
        layout.addView(edtEmail);

        final EditText edtPhone = new EditText(this);
        edtPhone.setHint("Phone");
        edtPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        edtPhone.setText(currentPhone);
        layout.addView(edtPhone);

        new AlertDialog.Builder(this)
                .setTitle("Edit Profile")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = edtName.getText().toString().trim();
                    String newEmail = edtEmail.getText().toString().trim();
                    String newPhone = edtPhone.getText().toString().trim();

                    if (newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
                        Toast.makeText(SuperAdminProfileActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    prefs.edit()
                            .putString(KEY_NAME, newName)
                            .putString(KEY_EMAIL, newEmail)
                            .putString(KEY_PHONE, newPhone)
                            .apply();

                    loadProfileData();
                    Toast.makeText(SuperAdminProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
