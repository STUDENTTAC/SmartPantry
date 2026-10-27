package com.example.smartpantry;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {

    RecyclerView recyclerRecipes;
    TextView txtNoRecipes;

    DatabaseHelper databaseHelper;
    ArrayList<Recipe> recipeList;
    RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        txtNoRecipes = findViewById(R.id.txtNoRecipes);

        databaseHelper = new DatabaseHelper(this);

        recyclerRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {

        recipeList = databaseHelper.getSuggestedRecipes();

        if (recipeList.isEmpty()) {

            recyclerRecipes.setVisibility(View.GONE);
            txtNoRecipes.setVisibility(View.VISIBLE);

        } else {

            recyclerRecipes.setVisibility(View.VISIBLE);
            txtNoRecipes.setVisibility(View.GONE);

            recipeAdapter = new RecipeAdapter(
                    this,
                    recipeList
            );

            recyclerRecipes.setAdapter(recipeAdapter);
        }
    }
}