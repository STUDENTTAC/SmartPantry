package com.example.smartpantry;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    Switch switchExpiryAlerts;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchExpiryAlerts =
                findViewById(
                        R.id.switchExpiryAlerts
                );

        preferences =
                getSharedPreferences(
                        "SmartPantrySettings",
                        MODE_PRIVATE
                );

        boolean expiryAlerts =
                preferences.getBoolean(
                        "expiryAlerts",
                        true
                );

        switchExpiryAlerts.setChecked(
                expiryAlerts
        );

        switchExpiryAlerts
                .setOnCheckedChangeListener(
                        (buttonView, isChecked) -> {

                            preferences.edit()
                                    .putBoolean(
                                            "expiryAlerts",
                                            isChecked
                                    )
                                    .apply();

                            Toast.makeText(
                                    SettingsActivity.this,
                                    "Setting saved",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }
}