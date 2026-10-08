package com.fahimkhan.smartpantry;

import static org.junit.Assert.assertEquals;

import com.fahimkhan.smartpantry.utils.IngredientNormaliser;

import org.junit.Test;

/** Verifies that names meaning the same ingredient normalise to the same value. */
public class IngredientNormaliserTest {

    @Test
    public void plurals_becomeSingular() {
        assertEquals("tomato", IngredientNormaliser.normalise("tomatoes"));
        assertEquals("egg", IngredientNormaliser.normalise("eggs"));
        assertEquals("berry", IngredientNormaliser.normalise("berries"));
        assertEquals("peach", IngredientNormaliser.normalise("peaches"));
        assertEquals("loaf", IngredientNormaliser.normalise("loaves"));
    }

    @Test
    public void apples_matchesApple() {
        // A naive "strip es, then s" rule produces "appl" here
        assertEquals(IngredientNormaliser.normalise("apple"),
                IngredientNormaliser.normalise("apples"));
    }

    @Test
    public void singularWordsEndingInS_areUnchanged() {
        assertEquals("hummus", IngredientNormaliser.normalise("Hummus"));
        assertEquals("couscous", IngredientNormaliser.normalise("couscous"));
    }

    @Test
    public void capitalsAndExtraSpaces_areIgnored() {
        assertEquals("green pepper", IngredientNormaliser.normalise("  Green   Peppers "));
    }

    @Test
    public void synonyms_mapToRecipeName() {
        assertEquals("flour", IngredientNormaliser.normalise("Cake flour"));
        assertEquals("oil", IngredientNormaliser.normalise("Sunflower Oil"));
        assertEquals("garlic", IngredientNormaliser.normalise("Garlic cloves"));
        assertEquals("cheese", IngredientNormaliser.normalise("Gouda"));
        assertEquals("flour", IngredientNormaliser.normalise("All-purpose flour"));
    }

    @Test
    public void nullOrBlank_returnsEmpty() {
        assertEquals("", IngredientNormaliser.normalise(null));
        assertEquals("", IngredientNormaliser.normalise("   "));
    }
}
