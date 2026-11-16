package com.example.trackpoint;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
        tvTitle.setText("Edit Profile");
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
        final EditText edtName = createStyledEditText("Enter name", currentName, InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        bodyLayout.addView(edtName);

        bodyLayout.addView(createFieldLabel("Email Address *"));
        final EditText edtEmail = createStyledEditText("Enter email", currentEmail, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        bodyLayout.addView(edtEmail);

        bodyLayout.addView(createFieldLabel("Phone Number *"));
        final EditText edtPhone = createStyledEditText("Enter phone number", currentPhone, InputType.TYPE_CLASS_PHONE);
        bodyLayout.addView(edtPhone);

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

            if (newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
                Toast.makeText(SuperAdminProfileActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(SuperAdminProfileActivity.this, "Enter valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            prefs.edit()
                    .putString(KEY_NAME, newName)
                    .putString(KEY_EMAIL, newEmail)
                    .putString(KEY_PHONE, newPhone)
                    .apply();

            loadProfileData();
            Toast.makeText(SuperAdminProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
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

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
