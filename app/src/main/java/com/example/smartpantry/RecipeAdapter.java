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
import java.util.Map;

/** Custom RecyclerView adapter for Home, Suggested Recipes and Rescue screens. */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Recipe> recipeList;
    private final DatabaseHelper databaseHelper;
    private final Map<Integer, Integer> rescueScores;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipeList) {
        this(context, recipeList, null);
    }

    /** Rescue Mode can pass scores so each recipe visibly shows its rescue value. */
    public RecipeAdapter(Context context,
                         ArrayList<Recipe> recipeList,
                         Map<Integer, Integer> rescueScores) {
        this.context = context;
        this.recipeList = recipeList;
        this.rescueScores = rescueScores;
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

        if (rescueScores != null) {
            int score = rescueScores.containsKey(recipe.getId())
                    ? rescueScores.get(recipe.getId())
                    : 0;
            holder.txtRecipeStatus.setText(context.getResources().getQuantityString(
                    R.plurals.rescue_score,
                    score,
                    score));
        } else {
            showReadiness(holder, recipe);
        }

        View.OnClickListener open = v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("recipeId", recipe.getId());
            intent.putExtra("recipeName", recipe.getName());
            intent.putExtra("recipeMethod", recipe.getMethod());
            context.startActivity(intent);
        };

        holder.btnViewRecipe.setOnClickListener(open);
        holder.itemView.setOnClickListener(open);
    }

    private void showReadiness(ViewHolder holder, Recipe recipe) {
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
            holder.txtRecipeStatus.setText(R.string.detail_not_configured);
        } else if (availableCount == requirements.size()) {
            holder.txtRecipeStatus.setText(context.getString(R.string.recipe_ready));
        } else {
            int missingCount = requirements.size() - availableCount;
            holder.txtRecipeStatus.setText(context.getString(
                    R.string.recipe_availability,
                    availableCount,
                    requirements.size(),
                    missingCount));
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    static String firstLetter(String recipeName) {
        if (recipeName == null || recipeName.trim().isEmpty()) {
            return "?";
        }
        return recipeName.trim().substring(0, 1).toUpperCase(Locale.ROOT);
    }

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
