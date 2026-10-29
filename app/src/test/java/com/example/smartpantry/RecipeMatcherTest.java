package com.example.smartpantry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Local unit tests for the strict-matching rule (no emulator needed). */
public class RecipeMatcherTest {

    private static Ingredient stock(String name, double qty, String unit) {
        return new Ingredient(name, qty, unit, "");
    }

    private static List<RecipeRequirement> tomatoToast() {
        return Arrays.asList(
                new RecipeRequirement("bread", 2, "slice"),
                new RecipeRequirement("tomato", 1, "piece"));
    }

    @Test
    public void allIngredientsPresent_isSuggested() {
        List<Ingredient> pantry = Arrays.asList(
                stock("Bread", 4, "slices"), stock("Tomatoes", 3, "pieces"));

        assertTrue(RecipeMatcher.canMake(tomatoToast(), pantry));
    }

    @Test
    public void oneIngredientMissing_isRejected() {
        List<Ingredient> pantry = Arrays.asList(stock("Bread", 4, "slice"));

        assertFalse(RecipeMatcher.canMake(tomatoToast(), pantry));
    }

    @Test
    public void notEnoughQuantity_isRejected() {
        List<Ingredient> pantry = Arrays.asList(
                stock("Bread", 1, "slice"), stock("Tomato", 1, "piece"));

        assertFalse(RecipeMatcher.canMake(tomatoToast(), pantry));
    }

    @Test
    public void quantitiesAcrossRowsAreSummed() {
        List<Ingredient> pantry = Arrays.asList(
                stock("Bread", 1, "slice"), stock("bread", 1, "slice"),
                stock("Tomato", 1, "piece"));

        assertTrue(RecipeMatcher.canMake(tomatoToast(), pantry));
    }

    @Test
    public void unitsAreConverted_kgAndTbsp() {
        List<RecipeRequirement> needs = Arrays.asList(
                new RecipeRequirement("rice", 150, "g"),
                new RecipeRequirement("oil", 1, "tbsp"));
        List<Ingredient> pantry = Arrays.asList(
                stock("Rice", 1, "kg"), stock("Oil", 500, "ml"));

        assertTrue(RecipeMatcher.canMake(needs, pantry));
    }

    @Test
    public void incompatibleUnits_areRejected() {
        List<RecipeRequirement> needs = Arrays.asList(new RecipeRequirement("rice", 150, "g"));
        List<Ingredient> pantry = Arrays.asList(stock("Rice", 3, "pieces"));

        assertFalse(RecipeMatcher.canMake(needs, pantry));
    }

    @Test
    public void emptyPantryOrEmptyRecipe_isRejected() {
        assertFalse(RecipeMatcher.canMake(tomatoToast(), new ArrayList<Ingredient>()));
        assertFalse(RecipeMatcher.canMake(new ArrayList<RecipeRequirement>(),
                Arrays.asList(stock("Bread", 4, "slice"))));
    }

    @Test
    public void normalizeName_handlesPlurals() {
        assertEquals("tomato", RecipeMatcher.normalizeName("Tomatoes "));
        assertEquals("egg", RecipeMatcher.normalizeName("EGGS"));
        assertEquals("berry", RecipeMatcher.normalizeName("berries"));
        assertEquals("peach", RecipeMatcher.normalizeName("peaches"));
        assertEquals("cheese", RecipeMatcher.normalizeName("cheese"));
        assertEquals("hummus", RecipeMatcher.normalizeName("hummus"));
    }
}
