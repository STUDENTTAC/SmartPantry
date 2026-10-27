package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";

    // Increased because we are adding recipe tables
    private static final int DATABASE_VERSION = 2;

    // INGREDIENT TABLE
    private static final String TABLE_INGREDIENTS = "ingredients";

    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_QUANTITY = "quantity";
    private static final String COL_UNIT = "unit";
    private static final String COL_EXPIRY = "expiry_date";

    // RECIPE TABLE
    private static final String TABLE_RECIPES = "recipes";

    private static final String RECIPE_ID = "id";
    private static final String RECIPE_NAME = "name";
    private static final String RECIPE_METHOD = "method";

    // RECIPE INGREDIENT TABLE
    private static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";

    private static final String RI_ID = "id";
    private static final String RI_RECIPE_ID = "recipe_id";
    private static final String RI_NAME = "ingredient_name";
    private static final String RI_QUANTITY = "quantity";
    private static final String RI_UNIT = "unit";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String createIngredientsTable =
                "CREATE TABLE " + TABLE_INGREDIENTS + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_NAME + " TEXT NOT NULL, " +
                        COL_QUANTITY + " REAL NOT NULL, " +
                        COL_UNIT + " TEXT NOT NULL, " +
                        COL_EXPIRY + " TEXT)";

        db.execSQL(createIngredientsTable);


        String createRecipesTable =
                "CREATE TABLE " + TABLE_RECIPES + " (" +
                        RECIPE_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        RECIPE_NAME +
                        " TEXT NOT NULL, " +

                        RECIPE_METHOD +
                        " TEXT NOT NULL)";

        db.execSQL(createRecipesTable);


        String createRecipeIngredientsTable =
                "CREATE TABLE " +
                        TABLE_RECIPE_INGREDIENTS + " (" +

                        RI_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        RI_RECIPE_ID +
                        " INTEGER NOT NULL, " +

                        RI_NAME +
                        " TEXT NOT NULL, " +

                        RI_QUANTITY +
                        " REAL NOT NULL, " +

                        RI_UNIT +
                        " TEXT NOT NULL)";

        db.execSQL(createRecipeIngredientsTable);


        seedRecipes(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_RECIPE_INGREDIENTS
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_RECIPES
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_INGREDIENTS
        );

        onCreate(db);
    }


    // =====================================================
    // PANTRY CRUD
    // =====================================================

    public boolean addIngredient(Ingredient ingredient) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NAME, ingredient.getName());
        values.put(COL_QUANTITY, ingredient.getQuantity());
        values.put(COL_UNIT, ingredient.getUnit());
        values.put(COL_EXPIRY, ingredient.getExpiryDate());

        long result = db.insert(
                TABLE_INGREDIENTS,
                null,
                values
        );

        return result != -1;
    }


    public ArrayList<Ingredient> getAllIngredients() {

        ArrayList<Ingredient> list =
                new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " +
                        TABLE_INGREDIENTS +
                        " ORDER BY " +
                        COL_NAME +
                        " ASC",
                null
        );

        if (cursor.moveToFirst()) {

            do {

                Ingredient ingredient =
                        new Ingredient();

                ingredient.setId(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        COL_ID
                                )
                        )
                );

                ingredient.setName(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        COL_NAME
                                )
                        )
                );

                ingredient.setQuantity(
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        COL_QUANTITY
                                )
                        )
                );

                ingredient.setUnit(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        COL_UNIT
                                )
                        )
                );

                ingredient.setExpiryDate(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        COL_EXPIRY
                                )
                        )
                );

                list.add(ingredient);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }


    public boolean updateIngredient(
            Ingredient ingredient) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COL_NAME,
                ingredient.getName()
        );

        values.put(
                COL_QUANTITY,
                ingredient.getQuantity()
        );

        values.put(
                COL_UNIT,
                ingredient.getUnit()
        );

        values.put(
                COL_EXPIRY,
                ingredient.getExpiryDate()
        );

        int rows = db.update(
                TABLE_INGREDIENTS,
                values,
                COL_ID + "=?",
                new String[]{
                        String.valueOf(
                                ingredient.getId()
                        )
                }
        );

        return rows > 0;
    }


    public boolean deleteIngredient(int id) {

        SQLiteDatabase db =
                getWritableDatabase();

        int rows = db.delete(
                TABLE_INGREDIENTS,
                COL_ID + "=?",
                new String[]{
                        String.valueOf(id)
                }
        );

        return rows > 0;
    }


    // =====================================================
    // RECIPE SEEDING
    // =====================================================

    private void seedRecipes(SQLiteDatabase db) {

        addSeedRecipe(
                db,
                "Tomato Toast",
                "Toast the bread. Slice the tomato and place it on the toast.",
                new String[]{"bread", "tomato"},
                new double[]{2, 1},
                new String[]{"slice", "piece"}
        );

        addSeedRecipe(
                db,
                "Cheese Toast",
                "Toast the bread and add cheese while the bread is warm.",
                new String[]{"bread", "cheese"},
                new double[]{2, 50},
                new String[]{"slice", "g"}
        );

        addSeedRecipe(
                db,
                "Egg Toast",
                "Cook the eggs and serve them on toasted bread.",
                new String[]{"egg", "bread"},
                new double[]{2, 2},
                new String[]{"piece", "slice"}
        );

        addSeedRecipe(
                db,
                "Banana Oats",
                "Cook the oats with milk and serve with sliced banana.",
                new String[]{"oats", "milk", "banana"},
                new double[]{50, 200, 1},
                new String[]{"g", "ml", "piece"}
        );

        addSeedRecipe(
                db,
                "Apple Oats",
                "Cook oats with milk and add chopped apple.",
                new String[]{"oats", "milk", "apple"},
                new double[]{50, 200, 1},
                new String[]{"g", "ml", "piece"}
        );

        addSeedRecipe(
                db,
                "Tomato Egg Scramble",
                "Heat the oil. Add tomato and eggs, then scramble until cooked.",
                new String[]{"egg", "tomato", "oil"},
                new double[]{2, 1, 1},
                new String[]{"piece", "piece", "tbsp"}
        );

        addSeedRecipe(
                db,
                "Cheese Omelette",
                "Beat the eggs, cook in oil and add cheese before folding.",
                new String[]{"egg", "cheese", "oil"},
                new double[]{2, 40, 1},
                new String[]{"piece", "g", "tsp"}
        );

        addSeedRecipe(
                db,
                "Fried Potatoes",
                "Slice the potatoes and fry them in oil. Season with salt.",
                new String[]{"potato", "oil", "salt"},
                new double[]{2, 1, 1},
                new String[]{"piece", "tbsp", "tsp"}
        );

        addSeedRecipe(
                db,
                "Carrot Potato Soup",
                "Boil the carrot and potato in water until soft. Season and blend.",
                new String[]{
                        "carrot",
                        "potato",
                        "water",
                        "salt"
                },
                new double[]{
                        2,
                        2,
                        500,
                        1
                },
                new String[]{
                        "piece",
                        "piece",
                        "ml",
                        "tsp"
                }
        );

        addSeedRecipe(
                db,
                "Egg Fried Rice",
                "Cook the rice, then fry with egg and a small amount of oil.",
                new String[]{"rice", "egg", "oil"},
                new double[]{150, 2, 1},
                new String[]{"g", "piece", "tsp"}
        );

        addSeedRecipe(
                db,
                "Tomato Rice",
                "Cook the rice and mix with cooked tomato and oil.",
                new String[]{"rice", "tomato", "oil"},
                new double[]{150, 2, 1},
                new String[]{"g", "piece", "tbsp"}
        );

        addSeedRecipe(
                db,
                "Bean Rice",
                "Cook the rice and beans separately, then combine.",
                new String[]{"rice", "bean"},
                new double[]{150, 150},
                new String[]{"g", "g"}
        );

        addSeedRecipe(
                db,
                "Chicken Rice",
                "Cook the chicken thoroughly and serve with cooked rice.",
                new String[]{"chicken", "rice", "oil"},
                new double[]{200, 150, 1},
                new String[]{"g", "g", "tbsp"}
        );

        addSeedRecipe(
                db,
                "Chicken Potatoes",
                "Cook the chicken and potatoes in oil until fully cooked.",
                new String[]{"chicken", "potato", "oil"},
                new double[]{200, 2, 1},
                new String[]{"g", "piece", "tbsp"}
        );

        addSeedRecipe(
                db,
                "Tomato Pasta",
                "Cook the pasta. Cook tomatoes in oil and combine.",
                new String[]{"pasta", "tomato", "oil"},
                new double[]{150, 2, 1},
                new String[]{"g", "piece", "tbsp"}
        );

        addSeedRecipe(
                db,
                "Cheesy Pasta",
                "Cook pasta. Add milk and cheese and stir until creamy.",
                new String[]{"pasta", "cheese", "milk"},
                new double[]{150, 50, 100},
                new String[]{"g", "g", "ml"}
        );

        addSeedRecipe(
                db,
                "Tuna Pasta",
                "Cook the pasta and mix with tuna.",
                new String[]{"pasta", "tuna"},
                new double[]{150, 120},
                new String[]{"g", "g"}
        );

        addSeedRecipe(
                db,
                "Tuna Sandwich",
                "Place tuna between two slices of bread.",
                new String[]{"bread", "tuna"},
                new double[]{2, 120},
                new String[]{"slice", "g"}
        );

        addSeedRecipe(
                db,
                "Apple Banana Bowl",
                "Chop the fruit and serve with yogurt.",
                new String[]{
                        "apple",
                        "banana",
                        "yogurt"
                },
                new double[]{
                        1,
                        1,
                        150
                },
                new String[]{
                        "piece",
                        "piece",
                        "g"
                }
        );

        addSeedRecipe(
                db,
                "Vegetable Omelette",
                "Beat the eggs. Add tomato and onion, then cook in oil.",
                new String[]{
                        "egg",
                        "tomato",
                        "onion",
                        "oil"
                },
                new double[]{
                        2,
                        1,
                        1,
                        1
                },
                new String[]{
                        "piece",
                        "piece",
                        "piece",
                        "tsp"
                }
        );
    }


    private void addSeedRecipe(
            SQLiteDatabase db,
            String name,
            String method,
            String[] ingredients,
            double[] quantities,
            String[] units) {

        ContentValues recipeValues =
                new ContentValues();

        recipeValues.put(
                RECIPE_NAME,
                name
        );

        recipeValues.put(
                RECIPE_METHOD,
                method
        );

        long recipeId = db.insert(
                TABLE_RECIPES,
                null,
                recipeValues
        );

        for (int i = 0;
             i < ingredients.length;
             i++) {

            ContentValues ingredientValues =
                    new ContentValues();

            ingredientValues.put(
                    RI_RECIPE_ID,
                    recipeId
            );

            ingredientValues.put(
                    RI_NAME,
                    ingredients[i]
            );

            ingredientValues.put(
                    RI_QUANTITY,
                    quantities[i]
            );

            ingredientValues.put(
                    RI_UNIT,
                    units[i]
            );

            db.insert(
                    TABLE_RECIPE_INGREDIENTS,
                    null,
                    ingredientValues
            );
        }
    }


    // =====================================================
    // GET ALL RECIPES
    // =====================================================

    public ArrayList<Recipe> getAllRecipes() {

        ArrayList<Recipe> recipes =
                new ArrayList<>();

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " +
                        TABLE_RECIPES +
                        " ORDER BY " +
                        RECIPE_NAME +
                        " ASC",
                null
        );

        if (cursor.moveToFirst()) {

            do {

                int id =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        RECIPE_ID
                                )
                        );

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        RECIPE_NAME
                                )
                        );

                String method =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        RECIPE_METHOD
                                )
                        );

                recipes.add(
                        new Recipe(
                                id,
                                name,
                                method
                        )
                );

            } while (cursor.moveToNext());
        }

        cursor.close();

        return recipes;
    }


    // =====================================================
    // GET REQUIREMENTS FOR ONE RECIPE
    // =====================================================

    public ArrayList<RecipeRequirement>
    getRecipeRequirements(int recipeId) {

        ArrayList<RecipeRequirement> requirements =
                new ArrayList<>();

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                RI_RECIPE_ID + "=?",
                new String[]{
                        String.valueOf(recipeId)
                },
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {

            do {

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        RI_NAME
                                )
                        );

                double quantity =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        RI_QUANTITY
                                )
                        );

                String unit =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        RI_UNIT
                                )
                        );

                requirements.add(
                        new RecipeRequirement(
                                name,
                                quantity,
                                unit
                        )
                );

            } while (cursor.moveToNext());
        }

        cursor.close();

        return requirements;
    }


    // =====================================================
    // STRICT RECIPE MATCHING
    // =====================================================

    public ArrayList<Recipe>
    getSuggestedRecipes() {

        ArrayList<Recipe> suggestedRecipes =
                new ArrayList<>();

        ArrayList<Recipe> allRecipes =
                getAllRecipes();

        ArrayList<Ingredient> pantry =
                getAllIngredients();

        for (Recipe recipe : allRecipes) {

            ArrayList<RecipeRequirement>
                    requirements =
                    getRecipeRequirements(
                            recipe.getId()
                    );

            boolean canMakeRecipe = true;

            for (RecipeRequirement requirement :
                    requirements) {

                double available =
                        getAvailableQuantity(
                                pantry,
                                requirement
                        );

                double required =
                        convertQuantity(
                                requirement.getQuantity(),
                                requirement.getUnit()
                        );

                if (available < required) {

                    canMakeRecipe = false;
                    break;
                }
            }

            if (canMakeRecipe) {

                suggestedRecipes.add(recipe);
            }
        }

        return suggestedRecipes;
    }


    private double getAvailableQuantity(
            ArrayList<Ingredient> pantry,
            RecipeRequirement requirement) {

        double totalAvailable = 0;

        String requiredName =
                normalizeIngredientName(
                        requirement.getIngredientName()
                );

        String requiredUnit =
                canonicalUnit(
                        requirement.getUnit()
                );

        for (Ingredient ingredient : pantry) {

            String pantryName =
                    normalizeIngredientName(
                            ingredient.getName()
                    );

            String pantryUnit =
                    canonicalUnit(
                            ingredient.getUnit()
                    );

            if (pantryName.equals(requiredName)
                    &&
                    pantryUnit.equals(requiredUnit)) {

                totalAvailable +=
                        convertQuantity(
                                ingredient.getQuantity(),
                                ingredient.getUnit()
                        );
            }
        }

        return totalAvailable;
    }


    // =====================================================
    // SIMPLE REAL-WORLD NAME NORMALISATION
    // =====================================================

    private String normalizeIngredientName(
            String name) {

        if (name == null) {
            return "";
        }

        String cleaned =
                name.toLowerCase(
                                Locale.ROOT
                        )
                        .trim();

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
        }

        if (cleaned.endsWith("s")
                &&
                cleaned.length() > 3
                &&
                !cleaned.endsWith("ss")) {

            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 1
                    );
        }

        return cleaned;
    }


    // =====================================================
    // UNIT NORMALISATION
    // =====================================================

    private String canonicalUnit(
            String unit) {

        if (unit == null) {
            return "";
        }

        String u =
                unit.toLowerCase(
                                Locale.ROOT
                        )
                        .trim();

        switch (u) {

            case "g":
            case "gram":
            case "grams":
            case "kg":
            case "kilogram":
            case "kilograms":
                return "g";


            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "ml";


            case "piece":
            case "pieces":
            case "item":
            case "items":
                return "piece";


            case "slice":
            case "slices":
                return "slice";


            case "tbsp":
            case "tablespoon":
            case "tablespoons":
                return "tbsp";


            case "tsp":
            case "teaspoon":
            case "teaspoons":
                return "tsp";


            default:
                return u;
        }
    }


    private double convertQuantity(
            double quantity,
            String unit) {

        if (unit == null) {
            return quantity;
        }

        String u =
                unit.toLowerCase(
                                Locale.ROOT
                        )
                        .trim();

        switch (u) {

            case "kg":
            case "kilogram":
            case "kilograms":

                return quantity * 1000;


            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":

                return quantity * 1000;


            default:

                return quantity;
        }
    }
}