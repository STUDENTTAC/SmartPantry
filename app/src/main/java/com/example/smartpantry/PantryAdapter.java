package com.example.smartpantry;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Ingredient> ingredientList;
    private DatabaseHelper databaseHelper;

    public PantryAdapter(Context context,
                         ArrayList<Ingredient> ingredientList,
                         DatabaseHelper databaseHelper) {

        this.context = context;
        this.ingredientList = ingredientList;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_ingredient, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        Ingredient ingredient = ingredientList.get(position);

        holder.txtIngredientName.setText(
                ingredient.getName()
        );

        holder.txtIngredientQuantity.setText(
                "Quantity: "
                        + ingredient.getQuantity()
                        + " "
                        + ingredient.getUnit()
        );

        String expiry = ingredient.getExpiryDate();

        if (expiry == null || expiry.isEmpty()) {

            holder.txtIngredientExpiry.setText(
                    "Expiry: Not provided"
            );

        } else {

            holder.txtIngredientExpiry.setText(
                    "Expiry: " + expiry
            );
        }

        holder.btnDeleteIngredient.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Delete Ingredient")
                    .setMessage(
                            "Are you sure you want to delete "
                                    + ingredient.getName()
                                    + "?"
                    )

                    .setPositiveButton("Delete",
                            (dialog, which) -> {

                                boolean deleted =
                                        databaseHelper.deleteIngredient(
                                                ingredient.getId()
                                        );

                                if (deleted) {

                                    ingredientList.remove(position);

                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(
                                            position,
                                            ingredientList.size()
                                    );

                                    Toast.makeText(
                                            context,
                                            "Ingredient deleted",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            })

                    .setNegativeButton("Cancel", null)

                    .show();
        });
    }

    @Override
    public int getItemCount() {

        return ingredientList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtIngredientName;
        TextView txtIngredientQuantity;
        TextView txtIngredientExpiry;

        Button btnEditIngredient;
        Button btnDeleteIngredient;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            txtIngredientName =
                    itemView.findViewById(
                            R.id.txtIngredientName
                    );

            txtIngredientQuantity =
                    itemView.findViewById(
                            R.id.txtIngredientQuantity
                    );

            txtIngredientExpiry =
                    itemView.findViewById(
                            R.id.txtIngredientExpiry
                    );

            btnDeleteIngredient =
                    itemView.findViewById(
                            R.id.btnDeleteIngredient
                    );
            btnEditIngredient =
                    itemView.findViewById(R.id.btnEditIngredient);
        }
    }
}