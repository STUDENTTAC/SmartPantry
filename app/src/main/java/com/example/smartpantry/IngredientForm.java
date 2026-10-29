package com.example.smartpantry;

import android.widget.EditText;

/**
 * Validation shared by the Add and Edit screens, so both enforce exactly the
 * same rules and show the same error messages.
 */
final class IngredientForm {

    private IngredientForm() {
        // utility class - no instances
    }

    /**
     * Validates the four fields. On the first problem it shows an error on that
     * field and returns null; otherwise it returns the ingredient to save.
     * The id is left as 0 - the Edit screen sets it afterwards.
     */
    static Ingredient validate(EditText edtName, EditText edtQuantity,
                               EditText edtUnit, EditText edtExpiry) {

        String name = edtName.getText().toString().trim();
        String quantityText = edtQuantity.getText().toString().trim();
        String unit = edtUnit.getText().toString().trim();
        String expiry = edtExpiry.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError(edtName.getContext().getString(R.string.err_name));
            edtName.requestFocus();
            return null;
        }

        if (quantityText.isEmpty()) {
            edtQuantity.setError(edtQuantity.getContext().getString(R.string.err_quantity_empty));
            edtQuantity.requestFocus();
            return null;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            quantity = -1;
        }

        // also rejects NaN / infinity / zero / negatives
        if (!(quantity > 0) || Double.isInfinite(quantity)) {
            edtQuantity.setError(edtQuantity.getContext().getString(R.string.err_quantity_invalid));
            edtQuantity.requestFocus();
            return null;
        }

        if (unit.isEmpty()) {
            edtUnit.setError(edtUnit.getContext().getString(R.string.err_unit));
            edtUnit.requestFocus();
            return null;
        }

        // expiry is optional, but if present it must be a real dd/MM/yyyy date
        if (!expiry.isEmpty() && !ExpiryUtils.isValidDate(expiry)) {
            edtExpiry.setError(edtExpiry.getContext().getString(R.string.err_expiry));
            return null;
        }

        return new Ingredient(name, quantity, unit, expiry);
    }
}
