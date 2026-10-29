package com.example.smartpantry;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Custom RecyclerView adapter for recipe cards. Used by the Home, Suggested
 * Recipes and Rescue screens. Tapping a card opens the recipe detail screen.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Recipe> recipeList;
    private final DatabaseHelper databaseHelper;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_recipe, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Recipe recipe = recipeList.get(position);

        holder.txtRecipeName.setText(recipe.getName());
        holder.txtRecipeLetter.setText(firstLetter(recipe.getName()));

        // Derive each card's readiness from this recipe's saved requirements
        // and the user's current pantry, rather than showing a static message.
        ArrayList<RecipeRequirement> requirements =
                databaseHelper.getRecipeRequirements(recipe.getId());
        ArrayList<Ingredient> pantry = databaseHelper.getAllIngredients();
        int availableCount = 0;
        for (RecipeRequirement requirement : requirements) {
            double availableBase = RecipeMatcher.availableQuantity(pantry, requirement);
            double requiredBase = RecipeMatcher.toBaseQuantity(
                    requirement.getQuantity(), requirement.getUnit());
            if (availableBase + 1e-9 >= requiredBase) {
                availableCount++;
            }
        }

        if (requirements.isEmpty()) {
            holder.txtRecipeStatus.setText("Ingredients not configured");
        } else if (availableCount == requirements.size()) {
            holder.txtRecipeStatus.setText(context.getString(R.string.recipe_ready));
        } else {
            int missingCount = requirements.size() - availableCount;
            holder.txtRecipeStatus.setText(availableCount + " of " + requirements.size()
                    + " ingredients available — still need " + missingCount);
        }

        View.OnClickListener open = v -> {

            // Explicit Intent carrying the recipe to the detail screen
            Intent intent = new Intent(context, RecipeDetailActivity.class);

            intent.putExtra("recipeId", recipe.getId());
            intent.putExtra("recipeName", recipe.getName());
            intent.putExtra("recipeMethod", recipe.getMethod());

            context.startActivity(intent);
        };

        holder.btnViewRecipe.setOnClickListener(open);
        holder.itemView.setOnClickListener(open);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    /** First letter of the meal, upper-cased, shown in the coloured tile. */
    static String firstLetter(String recipeName) {

        if (recipeName == null || recipeName.trim().isEmpty()) {
            return "?";
        }

        return recipeName.trim().substring(0, 1).toUpperCase(Locale.ROOT);
    }

    /** Holds the views of one recipe card. */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView txtRecipeLetter;
        final TextView txtRecipeName;
        final TextView txtRecipeStatus;
        final Button btnViewRecipe;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            txtRecipeLetter = itemView.findViewById(R.id.txtRecipeLetter);
            txtRecipeName = itemView.findViewById(R.id.txtRecipeName);
            txtRecipeStatus = itemView.findViewById(R.id.txtRecipeStatus);
            btnViewRecipe = itemView.findViewById(R.id.btnViewRecipe);
        }
    }
}
