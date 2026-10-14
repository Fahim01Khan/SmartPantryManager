package com.fahimkhan.smartpantry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.fahimkhan.smartpantry.utils.ExpiryChecker;
import com.fahimkhan.smartpantry.utils.ExpiryChecker.Status;

import org.junit.Test;

/** Verifies expiry classification using fixed dates, so results never depend on the real date. */
public class ExpiryCheckerTest {

    private static final String TODAY = "2026-10-13";
    private static final int WINDOW = 3;

    @Test
    public void expiresToday_isExpiringSoon() {
        assertEquals(Status.EXPIRING_SOON, ExpiryChecker.statusOf("2026-10-13", TODAY, WINDOW));
    }

    @Test
    public void expiresOnLastDayOfWindow_isExpiringSoon() {
        assertEquals(Status.EXPIRING_SOON, ExpiryChecker.statusOf("2026-10-16", TODAY, WINDOW));
    }

    @Test
    public void expiresDayAfterWindow_isFine() {
        assertEquals(Status.FINE, ExpiryChecker.statusOf("2026-10-17", TODAY, WINDOW));
    }

    @Test
    public void expiredYesterday_isExpired() {
        assertEquals(Status.EXPIRED, ExpiryChecker.statusOf("2026-10-12", TODAY, WINDOW));
    }

    @Test
    public void noExpiryDate_isNone() {
        assertEquals(Status.NONE, ExpiryChecker.statusOf(null, TODAY, WINDOW));
        assertEquals(Status.NONE, ExpiryChecker.statusOf("", TODAY, WINDOW));
    }

    @Test
    public void dayCount_crossesMonthAndLeapYear() {
        assertEquals(Long.valueOf(2), ExpiryChecker.daysUntilExpiry("2026-11-02", "2026-10-31"));
        assertEquals(Long.valueOf(2), ExpiryChecker.daysUntilExpiry("2028-03-01", "2028-02-28"));  // 2028 is a leap year
    }

    @Test
    public void impossibleDate_isRejected() {
        assertNull(ExpiryChecker.daysUntilExpiry("2026-02-31", TODAY));
    }
}
