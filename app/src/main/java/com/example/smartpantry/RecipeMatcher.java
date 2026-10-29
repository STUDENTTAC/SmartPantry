package com.example.smartpantry;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Core business logic of the app: the STRICT-MATCHING rule.
 *
 * <p>A recipe may only be suggested when EVERY ingredient it needs is in the
 * pantry in at least the required quantity. If even one ingredient is missing
 * (or there is not enough of it) the recipe is rejected.</p>
 *
 * <p>To cope with simple real-world messiness the matcher normalises:</p>
 * <ul>
 *     <li>singular / plural names ("tomato" vs "Tomatoes ")</li>
 *     <li>units inside the same family (g/kg, ml/l/tsp/tbsp/cup)</li>
 *     <li>the same ingredient stored on several pantry rows (quantities are summed)</li>
 * </ul>
 *
 * <p>This class deliberately uses no Android classes so it can be unit tested
 * on the development machine (see RecipeMatcherTest).</p>
 */
public final class RecipeMatcher {

    /** A unit's family (e.g. "mass") and its factor relative to the family base unit. */
    private static final class UnitInfo {
        final String family;
        final double factor;

        UnitInfo(String family, double factor) {
            this.family = family;
            this.factor = factor;
        }
    }

    private static final Map<String, UnitInfo> UNITS = new HashMap<>();

    static {
        // Mass - base unit: gram
        put("mass", 1, "g", "gram", "grams", "gm", "gms");
        put("mass", 1000, "kg", "kilogram", "kilograms");

        // Volume - base unit: millilitre
        put("volume", 1, "ml", "millilitre", "millilitres", "milliliter", "milliliters");
        put("volume", 1000, "l", "litre", "litres", "liter", "liters");
        put("volume", 5, "tsp", "teaspoon", "teaspoons");
        put("volume", 15, "tbsp", "tablespoon", "tablespoons");
        put("volume", 240, "cup", "cups");

        // Counted items - base unit: piece
        put("piece", 1, "piece", "pieces", "pc", "pcs", "item", "items", "whole");

        // Slices are their own family (a slice of bread is not "a piece")
        put("slice", 1, "slice", "slices");
    }

    private static void put(String family, double factor, String... names) {
        for (String name : names) {
            UNITS.put(name, new UnitInfo(family, factor));
        }
    }

    private RecipeMatcher() {
        // utility class - no instances
    }

    /**
     * Strict rule: returns true only if every requirement is covered by the pantry.
     * A recipe with no requirements is never suggested.
     */
    public static boolean canMake(List<RecipeRequirement> requirements,
                                  List<Ingredient> pantry) {

        if (requirements == null || requirements.isEmpty()) {
            return false;
        }

        for (RecipeRequirement requirement : requirements) {
            double available = availableQuantity(pantry, requirement);
            double required = toBaseQuantity(
                    requirement.getQuantity(), requirement.getUnit());

            // small tolerance avoids floating-point surprises (e.g. 0.1 + 0.2)
            if (available + 1e-9 < required) {
                return false;
            }
        }

        return true;
    }

    /**
     * Total pantry quantity (in base units) of the ingredient and unit family
     * that the requirement asks for.
     */
    public static double availableQuantity(List<Ingredient> pantry,
                                           RecipeRequirement requirement) {

        double total = 0;

        String requiredName = normalizeName(requirement.getIngredientName());
        String requiredFamily = unitFamily(requirement.getUnit());

        for (Ingredient ingredient : pantry) {

            boolean sameName =
                    normalizeName(ingredient.getName()).equals(requiredName);
            boolean sameFamily =
                    unitFamily(ingredient.getUnit()).equals(requiredFamily);

            if (sameName && sameFamily) {
                total += toBaseQuantity(
                        ingredient.getQuantity(), ingredient.getUnit());
            }
        }

        return total;
    }

    /**
     * Lower-cases, trims and singularises an ingredient name so that
     * "Tomatoes", "tomato " and "TOMATO" all become "tomato".
     */
    public static String normalizeName(String name) {

        if (name == null) {
            return "";
        }

        String cleaned = name.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");

        if (cleaned.length() <= 3) {
            return cleaned;
        }

        // never touch words that naturally end in s: "hummus", "couscous", "swiss"
        if (cleaned.endsWith("ss") || cleaned.endsWith("us") || cleaned.endsWith("is")) {
            return cleaned;
        }

        if (cleaned.endsWith("ies")) {                 // berries -> berry
            return cleaned.substring(0, cleaned.length() - 3) + "y";
        }

        if (cleaned.endsWith("oes")                    // tomatoes -> tomato
                || cleaned.endsWith("ches")            // peaches  -> peach
                || cleaned.endsWith("shes")            // radishes -> radish
                || cleaned.endsWith("xes")) {          // boxes    -> box
            return cleaned.substring(0, cleaned.length() - 2);
        }

        if (cleaned.endsWith("s")) {                   // eggs -> egg
            return cleaned.substring(0, cleaned.length() - 1);
        }

        return cleaned;
    }

    /** Family a unit belongs to ("mass", "volume", "piece", "slice" or the unit itself). */
    public static String unitFamily(String unit) {
        return lookup(unit).family;
    }

    /** Converts a quantity to its family's base unit (kg -> g, l -> ml, tbsp -> ml...). */
    public static double toBaseQuantity(double quantity, String unit) {
        return quantity * lookup(unit).factor;
    }

    private static UnitInfo lookup(String unit) {

        String key = unit == null ? "" : unit.toLowerCase(Locale.ROOT).trim();
        UnitInfo info = UNITS.get(key);

        // unknown units only match themselves, with no conversion
        return info != null ? info : new UnitInfo("other:" + key, 1);
    }
}
