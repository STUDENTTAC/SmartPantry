package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/** Home dashboard: pantry health, impact counters, expiry status and ready recipes. */
public class MainActivity extends AppCompatActivity {

    private static final int PREVIEW_COUNT = 3;

    private TextView txtBanner;
    private TextView txtStatItems;
    private TextView txtStatRecipes;
    private TextView txtStatExpiring;
    private TextView txtPantryHealth;
    private TextView txtMealsCooked;
    private TextView txtIngredientsRescued;
    private TextView txtHomeEmpty;
    private RecyclerView recyclerHome;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtBanner = findViewById(R.id.txtBanner);
        txtStatItems = findViewById(R.id.txtStatItems);
        txtStatRecipes = findViewById(R.id.txtStatRecipes);
        txtStatExpiring = findViewById(R.id.txtStatExpiring);
        txtPantryHealth = findViewById(R.id.txtPantryHealth);
        txtMealsCooked = findViewById(R.id.txtMealsCooked);
        txtIngredientsRescued = findViewById(R.id.txtIngredientsRescued);
        txtHomeEmpty = findViewById(R.id.txtHomeEmpty);
        recyclerHome = findViewById(R.id.recyclerHome);

        databaseHelper = new DatabaseHelper(this);
        recyclerHome.setLayoutManager(new LinearLayoutManager(this));

        NavHelper.setupBottomNav(this, NavHelper.HOME);

        findViewById(R.id.btnBannerRescue).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RescueActivity.class)));

        findViewById(R.id.btnSeeAll).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RecipeActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboard();
    }

    private void refreshDashboard() {

        ArrayList<Ingredient> pantry = databaseHelper.getAllIngredients();
        ArrayList<Recipe> suggested = databaseHelper.getSuggestedRecipes();

        int expiring = 0;
        int expired = 0;

        for (Ingredient ingredient : pantry) {
            if (ExpiryUtils.isExpired(ingredient.getExpiryDate())) {
                expired++;
            } else if (ExpiryUtils.isExpiringSoon(ingredient.getExpiryDate())) {
                expiring++;
            }
        }

        txtStatItems.setText(String.valueOf(pantry.size()));
        txtStatRecipes.setText(String.valueOf(suggested.size()));
        txtStatExpiring.setText(String.valueOf(expiring));
        txtMealsCooked.setText(String.valueOf(AppSettings.getMealsCooked(this)));
        txtIngredientsRescued.setText(String.valueOf(AppSettings.getIngredientsRescued(this)));
        updatePantryHealth(pantry.size(), expiring, expired);

        if (!AppSettings.isExpiryAlertsEnabled(this)) {
            txtBanner.setText(R.string.home_banner_off);
        } else if (expired > 0) {
            txtBanner.setText(getResources().getQuantityString(
                    R.plurals.home_banner_expired, expired, expired));
        } else if (expiring > 0) {
            txtBanner.setText(getResources().getQuantityString(
                    R.plurals.home_banner_expiring, expiring, expiring));
        } else {
            txtBanner.setText(R.string.home_banner_none);
        }

        if (suggested.isEmpty()) {
            recyclerHome.setVisibility(View.GONE);
            txtHomeEmpty.setVisibility(View.VISIBLE);
        } else {
            txtHomeEmpty.setVisibility(View.GONE);
            recyclerHome.setVisibility(View.VISIBLE);

            ArrayList<Recipe> preview = new ArrayList<>(
                    suggested.subList(0, Math.min(PREVIEW_COUNT, suggested.size())));

            recyclerHome.setAdapter(new RecipeAdapter(this, preview));
        }
    }

    private void updatePantryHealth(int itemCount, int expiring, int expired) {
        if (itemCount == 0) {
            txtPantryHealth.setText(R.string.health_empty);
            txtPantryHealth.setTextColor(ContextCompat.getColor(this, R.color.text_muted));
        } else if (expired > 0) {
            txtPantryHealth.setText(R.string.health_high_risk);
            txtPantryHealth.setTextColor(ContextCompat.getColor(this, R.color.danger));
        } else if (expiring > 0) {
            txtPantryHealth.setText(R.string.health_use_soon);
            txtPantryHealth.setTextColor(ContextCompat.getColor(this, R.color.warn));
        } else {
            txtPantryHealth.setText(R.string.health_healthy);
            txtPantryHealth.setTextColor(ContextCompat.getColor(this, R.color.accent));
        }
    }
}
