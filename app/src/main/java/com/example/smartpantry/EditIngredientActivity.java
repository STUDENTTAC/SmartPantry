package com.example.smartpantry;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditIngredientActivity extends AppCompatActivity {

    EditText edtName;
    EditText edtQuantity;
    EditText edtUnit;
    EditText edtExpiry;

    Button btnUpdateIngredient;

    DatabaseHelper databaseHelper;

    int ingredientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ingredient);

        edtName = findViewById(R.id.edtEditName);
        edtQuantity = findViewById(R.id.edtEditQuantity);
        edtUnit = findViewById(R.id.edtEditUnit);
        edtExpiry = findViewById(R.id.edtEditExpiry);

        btnUpdateIngredient = findViewById(R.id.btnUpdateIngredient);

        databaseHelper = new DatabaseHelper(this);

        ingredientId = getIntent().getIntExtra("id", -1);

        String name = getIntent().getStringExtra("name");
        double quantity = getIntent().getDoubleExtra("quantity", 0);
        String unit = getIntent().getStringExtra("unit");
        String expiry = getIntent().getStringExtra("expiry");

        edtName.setText(name);
        edtQuantity.setText(String.valueOf(quantity));
        edtUnit.setText(unit);
        edtExpiry.setText(expiry);

        btnUpdateIngredient.setOnClickListener(v -> updateIngredient());
    }

    private void updateIngredient() {

        String name = edtName.getText().toString().trim();
        String quantityText = edtQuantity.getText().toString().trim();
        String unit = edtUnit.getText().toString().trim();
        String expiry = edtExpiry.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Enter ingredient name");
            return;
        }

        if (quantityText.isEmpty()) {
            edtQuantity.setError("Enter quantity");
            return;
        }

        if (unit.isEmpty()) {
            edtUnit.setError("Enter unit");
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            edtQuantity.setError("Enter a valid number");
            return;
        }

        Ingredient ingredient = new Ingredient(
                ingredientId,
                name,
                quantity,
                unit,
                expiry
        );

        boolean updated = databaseHelper.updateIngredient(ingredient);

        if (updated) {

            Toast.makeText(
                    this,
                    "Ingredient updated",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Could not update ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}