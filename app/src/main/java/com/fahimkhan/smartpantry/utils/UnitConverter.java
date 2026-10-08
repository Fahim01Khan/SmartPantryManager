package com.fahimkhan.smartpantry.utils;

/**
 * Groups units into categories and converts quantities to base units
 * (grams, millilitres, or a plain count) so they can be compared.
 * Units in different categories (e.g. grams and millilitres) are never comparable.
 */
public final class UnitConverter {

    /** The kind of measurement a unit represents. */
    public enum Category { MASS, VOLUME, COUNT, UNKNOWN }

    private static final double THOUSAND = 1000.0;

    private UnitConverter() {
        // Static methods only
    }

    /** Returns the category of a unit, ignoring capitals ("L" and "l" are both VOLUME). */
    public static Category categoryOf(String unit) {
        if (unit == null) {
            return Category.UNKNOWN;
        }
        String u = unit.trim();
        if (u.equalsIgnoreCase(Units.GRAM) || u.equalsIgnoreCase(Units.KILOGRAM)) {
            return Category.MASS;
        }
        if (u.equalsIgnoreCase(Units.MILLILITRE) || u.equalsIgnoreCase(Units.LITRE)) {
            return Category.VOLUME;
        }
        if (u.equalsIgnoreCase(Units.COUNT)) {
            return Category.COUNT;
        }
        return Category.UNKNOWN;
    }

    /** Converts a quantity to its base unit: kg to g and L to ml; other units unchanged. */
    public static double toBaseQuantity(double quantity, String unit) {
        if (unit == null) {
            return quantity;
        }
        String u = unit.trim();
        if (u.equalsIgnoreCase(Units.KILOGRAM) || u.equalsIgnoreCase(Units.LITRE)) {
            return quantity * THOUSAND;
        }
        return quantity;
    }

    /** True only if both units are known and measure the same kind of thing. */
    public static boolean areCompatible(String unitA, String unitB) {
        Category a = categoryOf(unitA);
        return a != Category.UNKNOWN && a == categoryOf(unitB);
    }
}
