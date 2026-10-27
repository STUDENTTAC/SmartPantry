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

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Recipe> recipeList;

    public RecipeAdapter(
            Context context,
            ArrayList<Recipe> recipeList) {

        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater
                .from(context)
                .inflate(
                        R.layout.item_recipe,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Recipe recipe = recipeList.get(position);

        holder.txtRecipeName.setText(
                recipe.getName()
        );

        holder.btnViewRecipe.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    RecipeDetailActivity.class
            );

            intent.putExtra(
                    "recipeId",
                    recipe.getId()
            );

            intent.putExtra(
                    "recipeName",
                    recipe.getName()
            );

            intent.putExtra(
                    "recipeMethod",
                    recipe.getMethod()
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtRecipeName;
        Button btnViewRecipe;

        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtRecipeName =
                    itemView.findViewById(
                            R.id.txtRecipeName
                    );

            btnViewRecipe =
                    itemView.findViewById(
                            R.id.btnViewRecipe
                    );
        }
    }
}