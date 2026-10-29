package com.example.smartpantry;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Edit Ingredient screen (UPDATE). Receives the current values through Intent
 * extras, lets the user change them, then updates the row in SQLite.
 */
public class EditIngredientActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtQuantity;
    private EditText edtUnit;
    private EditText edtExpiry;

    private DatabaseHelper databaseHelper;

    private int ingredientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ingredient);

        NavHelper.setupBackHeader(this, R.string.edit_title);

        edtName = findViewById(R.id.edtEditName);
        edtQuantity = findViewById(R.id.edtEditQuantity);
        edtUnit = findViewById(R.id.edtEditUnit);
        edtExpiry = findViewById(R.id.edtEditExpiry);

        databaseHelper = new DatabaseHelper(this);

        // values sent by PantryAdapter
        ingredientId = getIntent().getIntExtra("id", -1);

        edtName.setText(getIntent().getStringExtra("name"));
        edtQuantity.setText(Ingredient.formatQuantity(
                getIntent().getDoubleExtra("quantity", 0)));
        edtUnit.setText(getIntent().getStringExtra("unit"));
        edtExpiry.setText(getIntent().getStringExtra("expiry"));

        edtExpiry.setOnClickListener(v -> ExpiryUtils.showDatePicker(this, edtExpiry));
        findViewById(R.id.btnClearEditExpiry).setOnClickListener(v -> edtExpiry.setText(""));

        findViewById(R.id.btnUpdateIngredient).setOnClickListener(v -> updateIngredient());
    }

    private void updateIngredient() {

        Ingredient ingredient =
                IngredientForm.validate(edtName, edtQuantity, edtUnit, edtExpiry);

        if (ingredient == null) {
            return;
        }

        ingredient.setId(ingredientId);

        if (databaseHelper.updateIngredient(ingredient)) {

            Toast.makeText(this, R.string.ingredient_updated, Toast.LENGTH_SHORT).show();
            finish();

        } else {

            Toast.makeText(this, R.string.ingredient_update_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
