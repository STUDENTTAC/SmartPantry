package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PantryActivity extends AppCompatActivity {

    RecyclerView recyclerPantry;
    Button btnAddIngredient;

    DatabaseHelper databaseHelper;
    ArrayList<Ingredient> ingredientList;
    PantryAdapter pantryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        recyclerPantry = findViewById(R.id.recyclerPantry);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);

        databaseHelper = new DatabaseHelper(this);

        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));

        btnAddIngredient.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PantryActivity.this,
                    AddIngredientActivity.class
            );

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadIngredients();
    }

    private void loadIngredients() {

        ingredientList = databaseHelper.getAllIngredients();

        pantryAdapter = new PantryAdapter(
                this,
                ingredientList,
                databaseHelper
        );

        recyclerPantry.setAdapter(pantryAdapter);
    }
}