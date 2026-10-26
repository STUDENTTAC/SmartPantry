package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnPantry;
    Button btnRecipes;
    Button btnRescue;
    Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPantry = findViewById(R.id.btnPantry);
        btnRecipes = findViewById(R.id.btnRecipes);
        btnRescue = findViewById(R.id.btnRescue);
        btnSettings = findViewById(R.id.btnSettings);

        btnPantry.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PantryActivity.class);
            startActivity(intent);
        });

        btnRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
            startActivity(intent);
        });

        btnRescue.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RescueActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}