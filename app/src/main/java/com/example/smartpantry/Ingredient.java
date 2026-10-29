package com.example.smartpantry;

/** One row of the pantry (model class for the ingredients table). */
public class Ingredient {

    private int id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate;

    public Ingredient() {
    }

    public Ingredient(int id, String name, double quantity,
                      String unit, String expiryDate) {

        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public Ingredient(String name, double quantity,
                      String unit, String expiryDate) {

        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    /** Shows 2.0 as "2" and 0.5 as "0.5". */
    public static String formatQuantity(double quantity) {

        if (quantity == Math.rint(quantity)) {
            return String.valueOf((long) quantity);
        }

        return String.valueOf(quantity);
    }
}
