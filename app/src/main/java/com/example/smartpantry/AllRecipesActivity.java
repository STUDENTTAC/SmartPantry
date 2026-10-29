package com.example.smartpantry;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Full recipe library. Unlike Suggested Recipes, this screen deliberately shows
 * every seeded recipe and uses RecipeAdapter to explain whether each recipe is
 * currently ready or what is still missing from the pantry.
 */
public class AllRecipesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_recipes);

        NavHelper.setupBackHeader(this, R.string.all_recipes_title);

        RecyclerView recyclerAllRecipes = findViewById(R.id.recyclerAllRecipes);
        recyclerAllRecipes.setLayoutManager(new LinearLayoutManager(this));

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        ArrayList<Recipe> recipes = databaseHelper.getAllRecipes();
        recyclerAllRecipes.setAdapter(new RecipeAdapter(this, recipes));
    }
}
