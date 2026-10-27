package com.example.smartpantry;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {

    TextView txtDetailName;
    TextView txtIngredients;
    TextView txtMethod;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_recipe_detail
        );

        txtDetailName =
                findViewById(R.id.txtDetailName);

        txtIngredients =
                findViewById(R.id.txtIngredients);

        txtMethod =
                findViewById(R.id.txtMethod);

        databaseHelper =
                new DatabaseHelper(this);

        int recipeId =
                getIntent().getIntExtra(
                        "recipeId",
                        -1
                );

        String recipeName =
                getIntent().getStringExtra(
                        "recipeName"
                );

        String recipeMethod =
                getIntent().getStringExtra(
                        "recipeMethod"
                );

        txtDetailName.setText(recipeName);
        txtMethod.setText(recipeMethod);

        showIngredients(recipeId);
    }

    private void showIngredients(
            int recipeId) {

        ArrayList<RecipeRequirement> requirements =
                databaseHelper
                        .getRecipeRequirements(
                                recipeId
                        );

        StringBuilder builder =
                new StringBuilder();

        for (RecipeRequirement requirement
                : requirements) {

            builder.append("• ")
                    .append(
                            requirement.getIngredientName()
                    )
                    .append(" - ")
                    .append(
                            requirement.getQuantity()
                    )
                    .append(" ")
                    .append(
                            requirement.getUnit()
                    )
                    .append("\n");
        }

        txtIngredients.setText(
                builder.toString()
        );
    }
}