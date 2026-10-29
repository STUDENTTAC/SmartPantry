package com.example.smartpantry;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/** Add Ingredient screen (CREATE). Validates the form, then inserts into SQLite. */
public class AddIngredientActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtQuantity;
    private EditText edtUnit;
    private EditText edtExpiry;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        NavHelper.setupBackHeader(this, R.string.add_title);

        edtName = findViewById(R.id.edtName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtUnit = findViewById(R.id.edtUnit);
        edtExpiry = findViewById(R.id.edtExpiry);

        databaseHelper = new DatabaseHelper(this);

        // the date is picked from a calendar dialog, never typed by hand
        edtExpiry.setOnClickListener(v -> ExpiryUtils.showDatePicker(this, edtExpiry));
        findViewById(R.id.btnClearExpiry).setOnClickListener(v -> edtExpiry.setText(""));

        findViewById(R.id.btnSaveIngredient).setOnClickListener(v -> saveIngredient());
    }

    private void saveIngredient() {

        Ingredient ingredient =
                IngredientForm.validate(edtName, edtQuantity, edtUnit, edtExpiry);

        if (ingredient == null) {
            return; // an error is already shown on the offending field
        }

        if (databaseHelper.addIngredient(ingredient)) {

            Toast.makeText(this, R.string.ingredient_added, Toast.LENGTH_SHORT).show();
            finish();

        } else {

            Toast.makeText(this, R.string.ingredient_add_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
