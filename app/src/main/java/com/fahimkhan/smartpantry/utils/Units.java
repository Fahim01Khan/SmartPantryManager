package com.fahimkhan.smartpantry.utils;

/**
 * The fixed set of measurement units the app supports.
 * These values must match the entries in res/values/arrays.xml exactly.
 */
public final class Units {

    public static final String GRAM = "g";
    public static final String KILOGRAM = "kg";
    public static final String MILLILITRE = "ml";
    public static final String LITRE = "L";
    public static final String COUNT = "unit";

    private Units() {
        // Constants only; never instantiated
    }
}
