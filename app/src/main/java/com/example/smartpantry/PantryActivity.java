package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Pantry list screen (READ). Shows every ingredient from the database in a
 * RecyclerView and links to the Add (CREATE) screen.
 */
public class PantryActivity extends AppCompatActivity {

    private RecyclerView recyclerPantry;
    private TextView txtEmptyPantry;
    private TextView txtPantrySubtitle;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        recyclerPantry = findViewById(R.id.recyclerPantry);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);
        txtPantrySubtitle = findViewById(R.id.txtPantrySubtitle);

        databaseHelper = new DatabaseHelper(this);

        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));

        NavHelper.setupBottomNav(this, NavHelper.PANTRY);

        // Explicit Intent: open the Add Ingredient screen
        findViewById(R.id.btnAddIngredient).setOnClickListener(v ->
                startActivity(new Intent(PantryActivity.this, AddIngredientActivity.class)));
    }

    /** Reload from the database whenever we come back from Add/Edit. */
    @Override
    protected void onResume() {
        super.onResume();
        loadIngredients();
    }

    private void loadIngredients() {

        ArrayList<Ingredient> ingredients = databaseHelper.getAllIngredients();

        txtPantrySubtitle.setText(getResources().getQuantityString(
                R.plurals.pantry_count, ingredients.size(), ingredients.size()));

        boolean empty = ingredients.isEmpty();

        txtEmptyPantry.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerPantry.setVisibility(empty ? View.GONE : View.VISIBLE);

        recyclerPantry.setAdapter(new PantryAdapter(this, ingredients, databaseHelper));
    }
}
