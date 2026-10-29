package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Home dashboard: pantry statistics, an "expiring soon" banner and a short
 * preview of the recipes the user can cook right now.
 */
public class MainActivity extends AppCompatActivity {

    private static final int PREVIEW_COUNT = 3;

    private TextView txtBanner;
    private TextView txtStatItems;
    private TextView txtStatRecipes;
    private TextView txtStatExpiring;
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

    /** Reloaded every time the screen returns so the numbers are always current. */
    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboard();
    }

    private void refreshDashboard() {

        ArrayList<Ingredient> pantry = databaseHelper.getAllIngredients();
        ArrayList<Recipe> suggested = databaseHelper.getSuggestedRecipes();

        int expiring = 0;

        for (Ingredient ingredient : pantry) {
            if (ExpiryUtils.isExpiringSoon(ingredient.getExpiryDate())) {
                expiring++;
            }
        }

        txtStatItems.setText(String.valueOf(pantry.size()));
        txtStatRecipes.setText(String.valueOf(suggested.size()));
        txtStatExpiring.setText(String.valueOf(expiring));

        // banner text depends on the "expiry alerts" setting
        if (!AppSettings.isExpiryAlertsEnabled(this)) {
            txtBanner.setText(R.string.home_banner_off);
        } else if (expiring > 0) {
            txtBanner.setText(getResources().getQuantityString(
                    R.plurals.home_banner_expiring, expiring, expiring));
        } else {
            txtBanner.setText(R.string.home_banner_none);
        }

        // preview list of recipes that can be cooked now
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
}
