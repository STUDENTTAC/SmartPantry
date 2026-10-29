package com.example.smartpantry;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Pantry Rescue Mode: the same strictly-matched recipes as the Suggested
 * Recipes screen, but sorted so recipes that use ingredients expiring within
 * 3 days come first.
 */
public class RescueActivity extends AppCompatActivity {

    private RecyclerView recyclerRescue;
    private TextView txtRescueMessage;
    private TextView txtRescueEmpty;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue);

        recyclerRescue = findViewById(R.id.recyclerRescue);
        txtRescueMessage = findViewById(R.id.txtRescueMessage);
        txtRescueEmpty = findViewById(R.id.txtRescueEmpty);

        databaseHelper = new DatabaseHelper(this);

        recyclerRescue.setLayoutManager(new LinearLayoutManager(this));

        NavHelper.setupBottomNav(this, NavHelper.RESCUE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRescueRecipes();
    }

    private void loadRescueRecipes() {

        ArrayList<Recipe> recipes = databaseHelper.getSuggestedRecipes();

        if (recipes.isEmpty()) {
            recyclerRescue.setVisibility(View.GONE);
            txtRescueMessage.setVisibility(View.GONE);
            txtRescueEmpty.setVisibility(View.VISIBLE);
            return;
        }

        txtRescueEmpty.setVisibility(View.GONE);
        txtRescueMessage.setVisibility(View.VISIBLE);
        recyclerRescue.setVisibility(View.VISIBLE);

        ArrayList<Ingredient> pantry = databaseHelper.getAllIngredients();

        // score each recipe once, then sort by score (highest first)
        final Map<Integer, Integer> scores = new HashMap<>();
        int highest = 0;

        for (Recipe recipe : recipes) {
            int score = rescueScore(recipe, pantry);
            scores.put(recipe.getId(), score);
            highest = Math.max(highest, score);
        }

        Collections.sort(recipes, (a, b) ->
                Integer.compare(scores.get(b.getId()), scores.get(a.getId())));

        txtRescueMessage.setText(highest > 0
                ? R.string.rescue_priority
                : R.string.rescue_none_urgent);

        recyclerRescue.setAdapter(new RecipeAdapter(this, recipes));
    }

    /** How many of the recipe's ingredients are in the pantry AND expiring soon. */
    private int rescueScore(Recipe recipe, ArrayList<Ingredient> pantry) {

        int score = 0;

        for (RecipeRequirement requirement
                : databaseHelper.getRecipeRequirements(recipe.getId())) {

            String needed = RecipeMatcher.normalizeName(requirement.getIngredientName());

            for (Ingredient ingredient : pantry) {

                if (RecipeMatcher.normalizeName(ingredient.getName()).equals(needed)
                        && ExpiryUtils.isExpiringSoon(ingredient.getExpiryDate())) {

                    score++;
                    break; // count each required ingredient once
                }
            }
        }

        return score;
    }
}
