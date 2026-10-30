package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";


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


    public static final class CookResult {
        private final boolean success;
        private final int rescuedIngredients;

        CookResult(boolean success, int rescuedIngredients) {
            this.success = success;
            this.rescuedIngredients = rescuedIngredients;
        }

        public boolean isSuccess() {
            return success;
        }

        public int getRescuedIngredients() {
            return rescuedIngredients;
        }
    }


    public CookResult consumeRecipe(int recipeId) {

        ArrayList<RecipeRequirement> requirements = getRecipeRequirements(recipeId);
        ArrayList<Ingredient> pantry = getAllIngredients();

        if (!RecipeMatcher.canMake(requirements, pantry)) {
            return new CookResult(false, 0);
        }

        SQLiteDatabase db = getWritableDatabase();
        java.util.HashSet<Integer> rescuedRows = new java.util.HashSet<>();

        db.beginTransaction();

        try {
            for (RecipeRequirement requirement : requirements) {

                double remainingBase = RecipeMatcher.toBaseQuantity(
                        requirement.getQuantity(), requirement.getUnit());

                ArrayList<Ingredient> matches = new ArrayList<>();

                for (Ingredient ingredient : pantry) {
                    boolean sameName = RecipeMatcher.normalizeName(ingredient.getName())
                            .equals(RecipeMatcher.normalizeName(requirement.getIngredientName()));
                    boolean sameFamily = RecipeMatcher.unitFamily(ingredient.getUnit())
                            .equals(RecipeMatcher.unitFamily(requirement.getUnit()));

                    if (sameName && sameFamily && ingredient.getQuantity() > 0) {
                        matches.add(ingredient);
                    }
                }

                // Use food that is closest to expiry first; undated/expired rows come later.
                matches.sort((a, b) -> Integer.compare(
                        expiryPriority(a.getExpiryDate()),
                        expiryPriority(b.getExpiryDate())));

                for (Ingredient ingredient : matches) {
                    if (remainingBase <= 1e-9) {
                        break;
                    }

                    double unitFactor = RecipeMatcher.toBaseQuantity(1, ingredient.getUnit());
                    double rowBase = ingredient.getQuantity() * unitFactor;
                    double usedBase = Math.min(rowBase, remainingBase);

                    if (usedBase <= 1e-9) {
                        continue;
                    }

                    if (ExpiryUtils.isExpiringSoon(ingredient.getExpiryDate())) {
                        rescuedRows.add(ingredient.getId());
                    }

                    double newBase = rowBase - usedBase;
                    double newQuantity = newBase / unitFactor;

                    if (newQuantity <= 1e-9) {
                        db.delete(
                                TABLE_INGREDIENTS,
                                COL_ID + "=?",
                                new String[]{String.valueOf(ingredient.getId())});
                        ingredient.setQuantity(0);
                    } else {
                        ContentValues values = new ContentValues();
                        values.put(COL_QUANTITY, newQuantity);
                        db.update(
                                TABLE_INGREDIENTS,
                                values,
                                COL_ID + "=?",
                                new String[]{String.valueOf(ingredient.getId())});
                        ingredient.setQuantity(newQuantity);
                    }

                    remainingBase -= usedBase;
                }

                if (remainingBase > 1e-7) {
                    // Defensive guard. The pre-check should make this impossible.
                    throw new IllegalStateException("Pantry changed during recipe deduction");
                }
            }

            db.setTransactionSuccessful();
            return new CookResult(true, rescuedRows.size());

        } catch (Exception e) {
            return new CookResult(false, 0);

        } finally {
            db.endTransaction();
        }
    }

    private int expiryPriority(String expiryDate) {
        int days = ExpiryUtils.daysUntilExpiry(expiryDate);

        if (days == ExpiryUtils.NO_DATE) {
            return Integer.MAX_VALUE - 1;
        }

        if (days < 0) {
            return Integer.MAX_VALUE;
        }

        return days;
    }
}
