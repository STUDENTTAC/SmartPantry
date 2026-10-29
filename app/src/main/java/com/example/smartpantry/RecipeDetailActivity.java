package com.example.smartpantry;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/** Recipe Detail screen: full ingredient list and numbered method steps. */
public class RecipeDetailActivity extends AppCompatActivity {

    private TextView txtDetailName;
    private TextView txtIngredients;
    private TextView txtMethod;
    private TextView txtRecipeReadiness;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        NavHelper.setupBackHeader(this, R.string.detail_title);

        txtDetailName = findViewById(R.id.txtDetailName);
        txtIngredients = findViewById(R.id.txtIngredients);
        txtMethod = findViewById(R.id.txtMethod);
        txtRecipeReadiness = findViewById(R.id.txtRecipeReadiness);

        databaseHelper = new DatabaseHelper(this);

        // data sent by RecipeAdapter through the Intent
        int recipeId = getIntent().getIntExtra("recipeId", -1);
        String recipeName = getIntent().getStringExtra("recipeName");
        String recipeMethod = getIntent().getStringExtra("recipeMethod");

        txtDetailName.setText(recipeName);
        txtMethod.setText(formatSteps(recipeMethod));

        showIngredients(recipeId);
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
                    builder.append("Still need to add this ingredient");
                } else {
                    builder.append("Not enough in your pantry yet");
                }
            } else {
                builder.append("\n     Available in your pantry");
            }
        }

        if (requirements.isEmpty()) {
            builder.append("No ingredient requirements have been saved for this recipe yet.");
            txtRecipeReadiness.setText("Ingredients not configured");
        } else if (missingCount == 0) {
            txtRecipeReadiness.setText("All " + requirements.size()
                    + " ingredients available — ready to make");
        } else {
            txtRecipeReadiness.setText(availableCount + " of " + requirements.size()
                    + " ingredients available — still need " + missingCount);
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
