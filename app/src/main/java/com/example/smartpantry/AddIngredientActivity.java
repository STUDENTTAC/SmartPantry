package com.example.smartpantry;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddIngredientActivity extends AppCompatActivity {

    EditText edtName;
    EditText edtQuantity;
    EditText edtUnit;
    EditText edtExpiry;

    Button btnSaveIngredient;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        edtName = findViewById(R.id.edtName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtUnit = findViewById(R.id.edtUnit);
        edtExpiry = findViewById(R.id.edtExpiry);

        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);

        databaseHelper = new DatabaseHelper(this);

        btnSaveIngredient.setOnClickListener(v -> saveIngredient());
    }

    private void saveIngredient() {

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
                name,
                quantity,
                unit,
                expiry
        );

        boolean inserted = databaseHelper.addIngredient(ingredient);

        if (inserted) {

            Toast.makeText(
                    this,
                    "Ingredient added",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Could not add ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}