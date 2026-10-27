package com.example.smartpantry;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RescueActivity extends AppCompatActivity {

    RecyclerView recyclerRescue;
    TextView txtRescueMessage;

    DatabaseHelper databaseHelper;
    ArrayList<Recipe> rescueRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue);

        recyclerRescue = findViewById(R.id.recyclerRescue);
        txtRescueMessage = findViewById(R.id.txtRescueMessage);

        databaseHelper = new DatabaseHelper(this);

        recyclerRescue.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadRescueRecipes();
    }

    private void loadRescueRecipes() {

        rescueRecipes =
                databaseHelper.getSuggestedRecipes();

        if (rescueRecipes.isEmpty()) {

            recyclerRescue.setVisibility(View.GONE);

            txtRescueMessage.setVisibility(View.VISIBLE);

            txtRescueMessage.setText(
                    "No rescue recipes available yet. Add more pantry ingredients."
            );

            return;
        }

        Collections.sort(
                rescueRecipes,
                (recipe1, recipe2) ->
                        Integer.compare(
                                getRescueScore(recipe2),
                                getRescueScore(recipe1)
                        )
        );

        int highestScore =
                getRescueScore(rescueRecipes.get(0));

        if (highestScore > 0) {

            txtRescueMessage.setText(
                    "Recipes using ingredients expiring within 3 days are prioritised."
            );

        } else {

            txtRescueMessage.setText(
                    "No urgent ingredients found. Showing recipes you can make now."
            );
        }

        RecipeAdapter adapter =
                new RecipeAdapter(
                        this,
                        rescueRecipes
                );

        recyclerRescue.setAdapter(adapter);
    }

    private int getRescueScore(Recipe recipe) {

        int score = 0;

        ArrayList<RecipeRequirement> requirements =
                databaseHelper.getRecipeRequirements(
                        recipe.getId()
                );

        ArrayList<Ingredient> pantry =
                databaseHelper.getAllIngredients();

        for (RecipeRequirement requirement : requirements) {

            for (Ingredient ingredient : pantry) {

                if (normalizeName(ingredient.getName())
                        .equals(
                                normalizeName(
                                        requirement.getIngredientName()
                                )
                        )
                        && isExpiringSoon(
                        ingredient.getExpiryDate()
                )) {

                    score++;
                    break;
                }
            }
        }

        return score;
    }

    private boolean isExpiringSoon(String expiryDate) {

        if (expiryDate == null ||
                expiryDate.trim().length() == 0) {

            return false;
        }

        try {

            SimpleDateFormat format =
                    new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                    );

            format.setLenient(false);

            Date expiry =
                    format.parse(expiryDate);

            Date today = new Date();

            if (expiry == null) {
                return false;
            }

            long difference =
                    expiry.getTime()
                            - today.getTime();

            long days =
                    TimeUnit.MILLISECONDS
                            .toDays(difference);

            return days >= 0 && days <= 3;

        } catch (Exception e) {

            return false;
        }
    }

    private String normalizeName(String name) {

        if (name == null) {
            return "";
        }

        String cleaned =
                name.toLowerCase(
                        Locale.ROOT
                ).trim();

        switch (cleaned) {

            case "tomatoes":
                return "tomato";

            case "potatoes":
                return "potato";

            case "eggs":
                return "egg";

            case "beans":
                return "bean";

            case "carrots":
                return "carrot";

            case "bananas":
                return "banana";

            case "apples":
                return "apple";

            case "onions":
                return "onion";

            default:
                return cleaned;
        }
    }
}