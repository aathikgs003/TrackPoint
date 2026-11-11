
package com.example.trackpoint;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MyProfile extends AppCompatActivity {

    ImageView btnBack;
    Button btnEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        btnBack = findViewById(R.id.btnBack);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        btnBack.setOnClickListener(v -> finish());

        btnEditProfile.setOnClickListener(v ->
                Toast.makeText(this,
                        "Edit Profile feature coming soon",
                        Toast.LENGTH_SHORT).show()
        );
    }
}
