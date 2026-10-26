package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_INGREDIENTS = "ingredients";

    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_QUANTITY = "quantity";
    private static final String COL_UNIT = "unit";
    private static final String COL_EXPIRY = "expiry_date";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        onCreate(db);
    }

    public boolean addIngredient(Ingredient ingredient) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NAME, ingredient.getName());
        values.put(COL_QUANTITY, ingredient.getQuantity());
        values.put(COL_UNIT, ingredient.getUnit());
        values.put(COL_EXPIRY, ingredient.getExpiryDate());

        long result = db.insert(TABLE_INGREDIENTS, null, values);

        return result != -1;
    }

    public ArrayList<Ingredient> getAllIngredients() {

        ArrayList<Ingredient> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_INGREDIENTS +
                        " ORDER BY " + COL_NAME + " ASC",
                null
        );

        if (cursor.moveToFirst()) {

            do {

                Ingredient ingredient = new Ingredient();

                ingredient.setId(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
                );

                ingredient.setName(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME))
                );

                ingredient.setQuantity(
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY))
                );

                ingredient.setUnit(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT))
                );

                ingredient.setExpiryDate(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY))
                );

                list.add(ingredient);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }

    public boolean updateIngredient(Ingredient ingredient) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NAME, ingredient.getName());
        values.put(COL_QUANTITY, ingredient.getQuantity());
        values.put(COL_UNIT, ingredient.getUnit());
        values.put(COL_EXPIRY, ingredient.getExpiryDate());

        int rows = db.update(
                TABLE_INGREDIENTS,
                values,
                COL_ID + "=?",
                new String[]{String.valueOf(ingredient.getId())}
        );

        return rows > 0;
    }

    public boolean deleteIngredient(int id) {

        SQLiteDatabase db = getWritableDatabase();

        int rows = db.delete(
                TABLE_INGREDIENTS,
                COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        return rows > 0;
    }
}