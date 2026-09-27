package com.fahimkhan.smartpantry.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Converts stored values into user-friendly text for display.
 * All methods are static; this class is never instantiated.
 */
public final class FormatUtils {

    private static final String STORED_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DISPLAY_DATE_PATTERN = "dd MMM yyyy";

    private FormatUtils() {
        // Prevents instantiation
    }

    /** Shows whole numbers without a decimal point: 6.0 becomes "6", 0.5 stays "0.5". */
    public static String formatQuantity(double quantity) {
        if (quantity == Math.rint(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    /** Combines a quantity and unit, e.g. "500 g", "1 unit", "6 units". */
    public static String formatAmount(double quantity, String unit) {
        String amount = formatQuantity(quantity);
        if ("unit".equals(unit)) {
            return amount + (quantity == 1 ? " unit" : " units");
        }
        return amount + " " + unit;
    }

    /** Converts a stored date ("2026-10-05") into display form ("05 Oct 2026"). */
    public static String formatExpiryDate(String storedDate) {
        try {
            SimpleDateFormat input = new SimpleDateFormat(STORED_DATE_PATTERN, Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat(DISPLAY_DATE_PATTERN, Locale.getDefault());
            Date date = input.parse(storedDate);
            return date == null ? storedDate : output.format(date);
        } catch (ParseException e) {
            return storedDate;   // Fall back to the raw value rather than crashing
        }
    }
}
