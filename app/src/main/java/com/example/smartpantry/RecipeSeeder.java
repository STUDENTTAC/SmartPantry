package com.example.smartpantry;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Pre-loads the recipe collection the first time the database is created.
 *
 * <p>Each ingredient is written as "name:quantity:unit", e.g. "bread:2:slice".
 * Method steps are separate sentences, which the detail screen shows as a
 * numbered list.</p>
 */
final class RecipeSeeder {

    private RecipeSeeder() {
        // utility class - no instances
    }

    /** Inserts all seed recipes. Must be called inside onCreate/onUpgrade. */
    static void seed(SQLiteDatabase db) {

        add(db, "Tomato Toast",
                "Toast the bread. Slice the tomato and place it on the toast.",
                "bread:2:slice",
                "tomato:1:piece");

        add(db, "Cheese Toast",
                "Toast the bread and add cheese while the bread is warm.",
                "bread:2:slice",
                "cheese:50:g");

        add(db, "Egg Toast",
                "Cook the eggs and serve them on toasted bread.",
                "egg:2:piece",
                "bread:2:slice");

        add(db, "Banana Oats",
                "Cook the oats with milk and serve with sliced banana.",
                "oats:50:g",
                "milk:200:ml",
                "banana:1:piece");

        add(db, "Apple Oats",
                "Cook oats with milk and add chopped apple.",
                "oats:50:g",
                "milk:200:ml",
                "apple:1:piece");

        add(db, "Tomato Egg Scramble",
                "Heat the oil. Add tomato and eggs, then scramble until cooked.",
                "egg:2:piece",
                "tomato:1:piece",
                "oil:1:tbsp");

        add(db, "Cheese Omelette",
                "Beat the eggs, cook in oil and add cheese before folding.",
                "egg:2:piece",
                "cheese:40:g",
                "oil:1:tsp");

        add(db, "Fried Potatoes",
                "Slice the potatoes and fry them in oil. Season with salt.",
                "potato:2:piece",
                "oil:1:tbsp",
                "salt:1:tsp");

        add(db, "Carrot Potato Soup",
                "Boil the carrot and potato in water until soft. Season and blend.",
                "carrot:2:piece",
                "potato:2:piece",
                "water:500:ml",
                "salt:1:tsp");

        add(db, "Egg Fried Rice",
                "Cook the rice, then fry with egg and a small amount of oil.",
                "rice:150:g",
                "egg:2:piece",
                "oil:1:tsp");

        add(db, "Tomato Rice",
                "Cook the rice and mix with cooked tomato and oil.",
                "rice:150:g",
                "tomato:2:piece",
                "oil:1:tbsp");

        add(db, "Bean Rice",
                "Cook the rice and beans separately, then combine.",
                "rice:150:g",
                "bean:150:g");

        add(db, "Chicken Rice",
                "Cook the chicken thoroughly and serve with cooked rice.",
                "chicken:200:g",
                "rice:150:g",
                "oil:1:tbsp");

        add(db, "Chicken Potatoes",
                "Cook the chicken and potatoes in oil until fully cooked.",
                "chicken:200:g",
                "potato:2:piece",
                "oil:1:tbsp");

        add(db, "Tomato Pasta",
                "Cook the pasta. Cook tomatoes in oil and combine.",
                "pasta:150:g",
                "tomato:2:piece",
                "oil:1:tbsp");

        add(db, "Cheesy Pasta",
                "Cook pasta. Add milk and cheese and stir until creamy.",
                "pasta:150:g",
                "cheese:50:g",
                "milk:100:ml");

        add(db, "Tuna Pasta",
                "Cook the pasta and mix with tuna.",
                "pasta:150:g",
                "tuna:120:g");

        add(db, "Tuna Sandwich",
                "Place tuna between two slices of bread.",
                "bread:2:slice",
                "tuna:120:g");

        add(db, "Apple Banana Bowl",
                "Chop the fruit and serve with yogurt.",
                "apple:1:piece",
                "banana:1:piece",
                "yogurt:150:g");

        add(db, "Vegetable Omelette",
                "Beat the eggs. Add tomato and onion, then cook in oil.",
                "egg:2:piece",
                "tomato:1:piece",
                "onion:1:piece",
                "oil:1:tsp");
    }

    private static void add(SQLiteDatabase db,
                            String name,
                            String method,
                            String... ingredients) {

        ContentValues recipe = new ContentValues();
        recipe.put(DatabaseHelper.RECIPE_NAME, name);
        recipe.put(DatabaseHelper.RECIPE_METHOD, method);

        long recipeId = db.insert(DatabaseHelper.TABLE_RECIPES, null, recipe);

        for (String entry : ingredients) {

            String[] parts = entry.split(":");

            ContentValues row = new ContentValues();
            row.put(DatabaseHelper.RI_RECIPE_ID, recipeId);
            row.put(DatabaseHelper.RI_NAME, parts[0]);
            row.put(DatabaseHelper.RI_QUANTITY, Double.parseDouble(parts[1]));
            row.put(DatabaseHelper.RI_UNIT, parts[2]);

            db.insert(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, row);
        }
    }
}
