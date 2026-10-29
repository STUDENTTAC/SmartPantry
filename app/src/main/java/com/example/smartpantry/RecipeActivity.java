package com.example.smartpantry;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Suggested Recipes screen. Shows only recipes that pass the strict-matching
 * rule against the user's current pantry.
 */
public class RecipeActivity extends AppCompatActivity {

    private RecyclerView recyclerRecipes;
    private TextView txtNoRecipes;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        txtNoRecipes = findViewById(R.id.txtNoRecipes);

        databaseHelper = new DatabaseHelper(this);

        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));

        NavHelper.setupBottomNav(this, NavHelper.RECIPES);
    }

    /** Re-run the matching every time the screen is shown (the pantry may have changed). */
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {

        // Assignment core rule: show ONLY recipes the user can make right now.
        ArrayList<Recipe> recipes = databaseHelper.getSuggestedRecipes();

        if (recipes.isEmpty()) {

            // friendly message instead of a blank screen
            recyclerRecipes.setVisibility(View.GONE);
            txtNoRecipes.setVisibility(View.VISIBLE);

        } else {

            txtNoRecipes.setVisibility(View.GONE);
            recyclerRecipes.setVisibility(View.VISIBLE);
            recyclerRecipes.setAdapter(new RecipeAdapter(this, recipes));
        }
    }
}
