package com.example.smartpantry;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/** Recipe Detail screen: full ingredient list, method, and Cook & Deduct action. */
public class RecipeDetailActivity extends AppCompatActivity {

    private TextView txtDetailName;
    private TextView txtIngredients;
    private TextView txtMethod;
    private TextView txtRecipeReadiness;
    private TextView txtAfterCook;
    private Button btnCookRecipe;

    private DatabaseHelper databaseHelper;
    private int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        NavHelper.setupBackHeader(this, R.string.detail_title);

        txtDetailName = findViewById(R.id.txtDetailName);
        txtIngredients = findViewById(R.id.txtIngredients);
        txtMethod = findViewById(R.id.txtMethod);
        txtRecipeReadiness = findViewById(R.id.txtRecipeReadiness);
        txtAfterCook = findViewById(R.id.txtAfterCook);
        btnCookRecipe = findViewById(R.id.btnCookRecipe);

        databaseHelper = new DatabaseHelper(this);

        recipeId = getIntent().getIntExtra("recipeId", -1);
        String recipeName = getIntent().getStringExtra("recipeName");
        String recipeMethod = getIntent().getStringExtra("recipeMethod");

        txtDetailName.setText(recipeName);
        txtMethod.setText(formatSteps(recipeMethod));

        btnCookRecipe.setOnClickListener(v -> cookRecipe());

        refreshRecipeState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRecipeState();
    }

    private void refreshRecipeState() {
        showIngredients(recipeId);

        ArrayList<RecipeRequirement> requirements = databaseHelper.getRecipeRequirements(recipeId);
        ArrayList<Ingredient> pantry = databaseHelper.getAllIngredients();
        boolean ready = RecipeMatcher.canMake(requirements, pantry);

        btnCookRecipe.setEnabled(ready);
        btnCookRecipe.setAlpha(ready ? 1f : 0.45f);
    }

    private void cookRecipe() {
        DatabaseHelper.CookResult result = databaseHelper.consumeRecipe(recipeId);

        if (!result.isSuccess()) {
            Toast.makeText(this, R.string.cook_failed, Toast.LENGTH_SHORT).show();
            refreshRecipeState();
            return;
        }

        AppSettings.recordCookedMeal(this, result.getRescuedIngredients());
        Toast.makeText(this, R.string.cook_success, Toast.LENGTH_SHORT).show();

        ArrayList<Recipe> nextRecipes = databaseHelper.getSuggestedRecipes();
        txtAfterCook.setVisibility(View.VISIBLE);

        if (nextRecipes.isEmpty()) {
            txtAfterCook.setText(R.string.cook_next_none);
        } else {
            txtAfterCook.setText(getString(
                    R.string.cook_next_recipe,
                    nextRecipes.get(0).getName()));
        }

        refreshRecipeState();
    }

    private void showIngredients(int recipeId) {

        ArrayList<RecipeRequirement> requirements =
                databaseHelper.getRecipeRequirements(recipeId);
        ArrayList<Ingredient> pantry = databaseHelper.getAllIngredients();

        StringBuilder builder = new StringBuilder();
        int availableCount = 0;
        int missingCount = 0;

        for (RecipeRequirement requirement : requirements) {
            double availableBase = RecipeMatcher.availableQuantity(pantry, requirement);
            double requiredBase = RecipeMatcher.toBaseQuantity(
                    requirement.getQuantity(), requirement.getUnit());
            boolean enough = availableBase + 1e-9 >= requiredBase;

            if (enough) {
                availableCount++;
            } else {
                missingCount++;
            }

            if (builder.length() > 0) {
                builder.append("\n\n");
            }

            builder.append(enough ? "☑  " : "☐  ")
                    .append(requirement.getIngredientName())
                    .append(" — ")
                    .append(Ingredient.formatQuantity(requirement.getQuantity()))
                    .append(" ")
                    .append(requirement.getUnit());

            if (!enough) {
                builder.append("\n     ");
                if (availableBase <= 1e-9) {
                    builder.append(getString(R.string.detail_missing_ingredient));
                } else {
                    builder.append(getString(R.string.detail_not_enough));
                }
            } else {
                builder.append("\n     ")
                        .append(getString(R.string.detail_available));
            }
        }

        if (requirements.isEmpty()) {
            builder.append(getString(R.string.detail_requirements_missing));
            txtRecipeReadiness.setText(R.string.detail_not_configured);
        } else if (missingCount == 0) {
            txtRecipeReadiness.setText(getString(
                    R.string.detail_ready_count,
                    requirements.size()));
        } else {
            txtRecipeReadiness.setText(getString(
                    R.string.detail_missing_count,
                    availableCount,
                    requirements.size(),
                    missingCount));
        }

        txtIngredients.setText(builder.toString());
    }

    /** Splits the method into sentences and numbers them: "1. Toast the bread." */
    private String formatSteps(String method) {

        if (method == null || method.trim().isEmpty()) {
            return "";
        }

        String[] sentences = method.trim().split("(?<=\\.)\\s+");

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < sentences.length; i++) {

            if (i > 0) {
                builder.append("\n");
            }

            builder.append(i + 1).append(".  ").append(sentences[i]);
        }

        return builder.toString();
    }
}
