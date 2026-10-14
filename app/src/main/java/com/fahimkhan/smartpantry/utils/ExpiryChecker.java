package com.fahimkhan.smartpantry.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Works out whether a pantry item has expired or is expiring soon.
 * Dates use the stored format yyyy-MM-dd. "Today" is passed in as a
 * parameter so the logic can be tested with fixed dates.
 */
public final class ExpiryChecker {

    /**
     * How an item's expiry date relates to today.
     */
    public enum Status {NONE, FINE, EXPIRING_SOON, EXPIRED}

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final long MILLIS_PER_DAY = 24L * 60 * 60 * 1000;

    private ExpiryChecker() {
        // Static methods only
    }

    /**
     * Today's date in stored format, using the phone's own time zone.
     */
    public static String today() {
        return new SimpleDateFormat(DATE_PATTERN, Locale.US).format(new Date());
    }

    /**
     * Whole days from today until the expiry date: 0 means it expires today,
     * negative means it has already expired. Returns null if a date can't be read.
     */
    public static Long daysUntilExpiry(String expiryDate, String today) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));   // Both dates at UTC midnight: no daylight-saving gaps
        format.setLenient(false);                          // Reject impossible dates like 2026-02-31
        try {
            Date expiry = format.parse(expiryDate);
            Date now = format.parse(today);
            if (expiry == null || now == null) {
                return null;
            }
            return (expiry.getTime() - now.getTime()) / MILLIS_PER_DAY;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Classifies an expiry date against today and the user's warning window.
     */
    public static Status statusOf(String expiryDate, String today, int warningDays) {
        if (expiryDate == null || expiryDate.isEmpty()) {
            return Status.NONE;
        }
        Long days = daysUntilExpiry(expiryDate, today);
        if (days == null) {
            return Status.NONE;
        }
        if (days < 0) {
            return Status.EXPIRED;
        }
        if (days <= warningDays) {
            return Status.EXPIRING_SOON;
        }
        return Status.FINE;
    }
}
