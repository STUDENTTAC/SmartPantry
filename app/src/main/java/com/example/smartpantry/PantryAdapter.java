package com.example.smartpantry;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

/**
 * Custom RecyclerView adapter that turns each Ingredient row from the database
 * into an item_ingredient card, with Edit and Delete buttons and an expiry badge.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Ingredient> ingredientList;
    private final DatabaseHelper databaseHelper;

    public PantryAdapter(Context context,
                         ArrayList<Ingredient> ingredientList,
                         DatabaseHelper databaseHelper) {

        this.context = context;
        this.ingredientList = ingredientList;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_ingredient, parent, false);

        return new ViewHolder(view);
    }

    /** Fills one card with the data of the ingredient at this position. */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Ingredient ingredient = ingredientList.get(position);

        holder.txtIngredientName.setText(ingredient.getName());

        holder.txtIngredientQuantity.setText(context.getString(
                R.string.quantity_format,
                Ingredient.formatQuantity(ingredient.getQuantity()),
                ingredient.getUnit()));

        bindExpiryBadge(holder.txtIngredientExpiry, ingredient.getExpiryDate());

        // UPDATE: pass the current values to the Edit screen with an Intent
        holder.btnEditIngredient.setOnClickListener(v -> {

            Intent intent = new Intent(context, EditIngredientActivity.class);

            intent.putExtra("id", ingredient.getId());
            intent.putExtra("name", ingredient.getName());
            intent.putExtra("quantity", ingredient.getQuantity());
            intent.putExtra("unit", ingredient.getUnit());
            intent.putExtra("expiry", ingredient.getExpiryDate());

            context.startActivity(intent);
        });

        // DELETE: ask for confirmation first
        holder.btnDeleteIngredient.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.delete_title)
                        .setMessage(context.getString(
                                R.string.delete_message, ingredient.getName()))
                        .setPositiveButton(R.string.delete, (dialog, which) -> {

                            // position can change while the dialog is open, so ask the holder
                            int pos = holder.getBindingAdapterPosition();

                            if (pos != RecyclerView.NO_POSITION
                                    && databaseHelper.deleteIngredient(ingredient.getId())) {

                                ingredientList.remove(pos);
                                notifyItemRemoved(pos);
                                notifyItemRangeChanged(pos, ingredientList.size());

                                Toast.makeText(context, R.string.ingredient_deleted,
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show());
    }

    /**
     * Colours the badge: red = expired, amber = expiring soon (only if alerts are
     * switched on in Settings), grey = fine / no date.
     */
    private void bindExpiryBadge(TextView badge, String expiry) {

        int days = ExpiryUtils.daysUntilExpiry(expiry);

        int background = R.drawable.bg_badge_neutral;
        int textColor = R.color.text_muted;
        String text;

        if (expiry == null || expiry.trim().isEmpty()) {

            text = context.getString(R.string.expiry_none);

        } else if (days == ExpiryUtils.NO_DATE) {

            text = context.getString(R.string.expiry_date_format, expiry);

        } else if (days < 0) {

            text = context.getString(R.string.expiry_expired);
            background = R.drawable.bg_badge_danger;
            textColor = R.color.danger;

        } else if (days <= ExpiryUtils.SOON_DAYS
                && AppSettings.isExpiryAlertsEnabled(context)) {

            if (days == 0) {
                text = context.getString(R.string.expiry_today);
            } else if (days == 1) {
                text = context.getString(R.string.expiry_tomorrow);
            } else {
                text = context.getString(R.string.expiry_in_days, days);
            }

            background = R.drawable.bg_badge_warn;
            textColor = R.color.warn;

        } else {

            text = context.getString(R.string.expiry_date_format, expiry);
        }

        badge.setText(text);
        badge.setBackgroundResource(background);
        badge.setTextColor(ContextCompat.getColor(context, textColor));
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    /** Holds the views of one list row so they are looked up only once. */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView txtIngredientName;
        final TextView txtIngredientQuantity;
        final TextView txtIngredientExpiry;
        final Button btnEditIngredient;
        final Button btnDeleteIngredient;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            txtIngredientName = itemView.findViewById(R.id.txtIngredientName);
            txtIngredientQuantity = itemView.findViewById(R.id.txtIngredientQuantity);
            txtIngredientExpiry = itemView.findViewById(R.id.txtIngredientExpiry);
            btnEditIngredient = itemView.findViewById(R.id.btnEditIngredient);
            btnDeleteIngredient = itemView.findViewById(R.id.btnDeleteIngredient);
        }
    }
}
