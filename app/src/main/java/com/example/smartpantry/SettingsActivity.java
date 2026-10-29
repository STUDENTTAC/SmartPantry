package com.example.smartpantry;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;

/**
 * Settings screen. The expiry-alerts switch is stored with SharedPreferences
 * and read by the Home and Pantry screens.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        NavHelper.setupBottomNav(this, NavHelper.SETTINGS);

        MaterialSwitch switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);

        // set the saved value BEFORE attaching the listener so no "saved" toast appears on open
        switchExpiryAlerts.setChecked(AppSettings.isExpiryAlertsEnabled(this));

        switchExpiryAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {

            AppSettings.setExpiryAlertsEnabled(SettingsActivity.this, isChecked);

            Toast.makeText(SettingsActivity.this, R.string.settings_saved,
                    Toast.LENGTH_SHORT).show();
        });
    }
}
