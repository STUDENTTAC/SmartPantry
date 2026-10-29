package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Local on-device SQLite database (SQLiteOpenHelper).
 *
 * <p>Tables:</p>
 * <ul>
 *     <li><b>ingredients</b> - the user's pantry (full CRUD)</li>
 *     <li><b>recipes</b> - seeded recipe collection (name + method)</li>
 *     <li><b>recipe_ingredients</b> - what each recipe needs (many rows per recipe)</li>
 * </ul>
 *
 * <p>SQLite stores its file in the app's private storage, so pantry data survives
 * closing and reopening the app. Queries here are tiny, so they run directly on
 * the main thread; a larger app would move them to a background thread.</p>
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";

    /** Version 3 repairs/ensures the preloaded recipe collection exists. */
    private static final int DATABASE_VERSION = 3;

    // ---- ingredients table ----
    static final String TABLE_INGREDIENTS = "ingredients";
    static final String COL_ID = "id";
    static final String COL_NAME = "name";
    static final String COL_QUANTITY = "quantity";
    static final String COL_UNIT = "unit";
    static final String COL_EXPIRY = "expiry_date";

    // ---- recipes table ----
    static final String TABLE_RECIPES = "recipes";
    static final String RECIPE_ID = "id";
    static final String RECIPE_NAME = "name";
    static final String RECIPE_METHOD = "method";

    // ---- recipe_ingredients table ----
    static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    static final String RI_ID = "id";
    static final String RI_RECIPE_ID = "recipe_id";
    static final String RI_NAME = "ingredient_name";
    static final String RI_QUANTITY = "quantity";
    static final String RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // =====================================================
    // CREATE / UPGRADE
    // =====================================================

    /** Runs once, the first time the database file is created. */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE " + TABLE_INGREDIENTS + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_NAME + " TEXT NOT NULL, " +
                        COL_QUANTITY + " REAL NOT NULL, " +
                        COL_UNIT + " TEXT NOT NULL, " +
                        COL_EXPIRY + " TEXT)");

        createRecipeTablesAndSeed(db);
    }

    /**
     * Migrates an existing database WITHOUT deleting the user's pantry.
     * Only the changes introduced after oldVersion are applied.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 3) {
            // Create any missing recipe tables and seed only if the collection is empty.
            // This also repairs databases upgraded previously without any seeded recipes.
            createRecipeTablesAndSeed(db);
        }
    }

    private void createRecipeTablesAndSeed(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_RECIPES + " (" +
                        RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RECIPE_NAME + " TEXT NOT NULL, " +
                        RECIPE_METHOD + " TEXT NOT NULL)");

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_RECIPE_INGREDIENTS + " (" +
                        RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RI_RECIPE_ID + " INTEGER NOT NULL, " +
                        RI_NAME + " TEXT NOT NULL, " +
                        RI_QUANTITY + " REAL NOT NULL, " +
                        RI_UNIT + " TEXT NOT NULL, " +
                        "FOREIGN KEY(" + RI_RECIPE_ID + ") REFERENCES " +
                        TABLE_RECIPES + "(" + RECIPE_ID + "))");

        // Avoid duplicate recipes on upgrades, but recover cleanly if an older
        // installation has the tables yet contains no preloaded recipes.
        Cursor countCursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RECIPES, null);
        int recipeCount = 0;
        if (countCursor.moveToFirst()) {
            recipeCount = countCursor.getInt(0);
        }
        countCursor.close();

        if (recipeCount == 0) {
            RecipeSeeder.seed(db);
        }
    }

    // =====================================================
    // PANTRY CRUD
    // =====================================================

    /** CREATE: inserts a pantry item. Returns true on success. */
    public boolean addIngredient(Ingredient ingredient) {

        SQLiteDatabase db = getWritableDatabase();

        return db.insert(TABLE_INGREDIENTS, null, toValues(ingredient)) != -1;
    }

    /** READ: all pantry items, sorted A-Z by name. */
    public ArrayList<Ingredient> getAllIngredients() {

        ArrayList<Ingredient> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_INGREDIENTS +
                        " ORDER BY " + COL_NAME + " COLLATE NOCASE ASC",
                null);

        while (cursor.moveToNext()) {

            Ingredient ingredient = new Ingredient();

            ingredient.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            ingredient.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
            ingredient.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)));
            ingredient.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)));
            ingredient.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY)));

            list.add(ingredient);
        }

        cursor.close();

        return list;
    }

    /** UPDATE: overwrites the pantry item with the same id. */
    public boolean updateIngredient(Ingredient ingredient) {

        SQLiteDatabase db = getWritableDatabase();

        int rows = db.update(
                TABLE_INGREDIENTS,
                toValues(ingredient),
                COL_ID + "=?",
                new String[]{String.valueOf(ingredient.getId())});

        return rows > 0;
    }

    /** DELETE: removes the pantry item with the given id. */
    public boolean deleteIngredient(int id) {

        SQLiteDatabase db = getWritableDatabase();

        int rows = db.delete(
                TABLE_INGREDIENTS,
                COL_ID + "=?",
                new String[]{String.valueOf(id)});

        return rows > 0;
    }

    private ContentValues toValues(Ingredient ingredient) {

        ContentValues values = new ContentValues();

        values.put(COL_NAME, ingredient.getName());
        values.put(COL_QUANTITY, ingredient.getQuantity());
        values.put(COL_UNIT, ingredient.getUnit());
        values.put(COL_EXPIRY, ingredient.getExpiryDate());

        return values;
    }

    // =====================================================
    // RECIPES
    // =====================================================

    /** All recipes in the collection, sorted A-Z. */
    public ArrayList<Recipe> getAllRecipes() {

        ArrayList<Recipe> recipes = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_RECIPES +
                        " ORDER BY " + RECIPE_NAME + " ASC",
                null);

        while (cursor.moveToNext()) {

            recipes.add(new Recipe(
                    cursor.getInt(cursor.getColumnIndexOrThrow(RECIPE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RECIPE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RECIPE_METHOD))));
        }

        cursor.close();

        return recipes;
    }

    /** The ingredient list (name, quantity, unit) one recipe needs. */
    public ArrayList<RecipeRequirement> getRecipeRequirements(int recipeId) {

        ArrayList<RecipeRequirement> requirements = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                RI_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)},
                null, null, null);

        while (cursor.moveToNext()) {

            requirements.add(new RecipeRequirement(
                    cursor.getString(cursor.getColumnIndexOrThrow(RI_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(RI_QUANTITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RI_UNIT))));
        }

        cursor.close();

        return requirements;
    }

    /**
     * Runs the strict-matching rule (see {@link RecipeMatcher}) for every recipe
     * against the current pantry and returns ONLY the recipes that can be made now.
     */
    public ArrayList<Recipe> getSuggestedRecipes() {

        ArrayList<Recipe> suggested = new ArrayList<>();

        ArrayList<Ingredient> pantry = getAllIngredients();

        for (Recipe recipe : getAllRecipes()) {

            if (RecipeMatcher.canMake(getRecipeRequirements(recipe.getId()), pantry)) {
                suggested.add(recipe);
            }
        }

        return suggested;
    }
}
