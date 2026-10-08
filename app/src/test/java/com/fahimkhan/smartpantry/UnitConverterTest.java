package com.fahimkhan.smartpantry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fahimkhan.smartpantry.utils.UnitConverter;

import org.junit.Test;

/** Verifies unit categories, conversions and compatibility checks. */
public class UnitConverterTest {

    private static final double DELTA = 1e-9;

    @Test
    public void categories_areCorrect() {
        assertEquals(UnitConverter.Category.MASS, UnitConverter.categoryOf("kg"));
        assertEquals(UnitConverter.Category.VOLUME, UnitConverter.categoryOf("L"));
        assertEquals(UnitConverter.Category.VOLUME, UnitConverter.categoryOf("l"));
        assertEquals(UnitConverter.Category.COUNT, UnitConverter.categoryOf("unit"));
        assertEquals(UnitConverter.Category.UNKNOWN, UnitConverter.categoryOf("cup"));
    }

    @Test
    public void largeUnits_convertToBaseUnits() {
        assertEquals(1000, UnitConverter.toBaseQuantity(1, "kg"), DELTA);
        assertEquals(1500, UnitConverter.toBaseQuantity(1.5, "L"), DELTA);
        assertEquals(250, UnitConverter.toBaseQuantity(250, "ml"), DELTA);
        assertEquals(3, UnitConverter.toBaseQuantity(3, "unit"), DELTA);
    }

    @Test
    public void differentCategories_areNotCompatible() {
        assertTrue(UnitConverter.areCompatible("kg", "g"));
        assertTrue(UnitConverter.areCompatible("L", "ml"));
        assertFalse(UnitConverter.areCompatible("g", "ml"));
        assertFalse(UnitConverter.areCompatible("unit", "L"));
        assertFalse(UnitConverter.areCompatible("cup", "cup"));   // Unknown never matches
    }
}
