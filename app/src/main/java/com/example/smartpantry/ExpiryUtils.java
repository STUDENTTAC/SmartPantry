package com.example.smartpantry;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Helpers for the optional expiry date, which is stored as text in
 * dd/MM/yyyy format (e.g. 30/10/2026).
 */
public final class ExpiryUtils {

    /** Ingredients expiring within this many days count as "expiring soon". */
    public static final int SOON_DAYS = 3;

    /** Returned by {@link #daysUntilExpiry(String)} when there is no valid date. */
    public static final int NO_DATE = Integer.MIN_VALUE;

    private static final String PATTERN = "dd/MM/yyyy";

    private ExpiryUtils() {
        // utility class - no instances
    }

    /** True if the text is a real calendar date in dd/MM/yyyy format. */
    public static boolean isValidDate(String text) {
        return parse(text) != null;
    }

    /**
     * Whole days from today until the expiry date (0 = today, negative = already expired),
     * or {@link #NO_DATE} if the text is empty or not a valid date.
     */
    public static int daysUntilExpiry(String text) {

        Date expiry = parse(text);

        if (expiry == null) {
            return NO_DATE;
        }

        long diff = startOfDay(expiry).getTime() - startOfDay(new Date()).getTime();

        return (int) TimeUnit.MILLISECONDS.toDays(diff);
    }

    /** True when the item expires today or within {@link #SOON_DAYS} days. */
    public static boolean isExpiringSoon(String text) {

        int days = daysUntilExpiry(text);

        return days != NO_DATE && days >= 0 && days <= SOON_DAYS;
    }

    /** True when the expiry date has already passed. */
    public static boolean isExpired(String text) {

        int days = daysUntilExpiry(text);

        return days != NO_DATE && days < 0;
    }

    /**
     * Opens a date picker and writes the chosen date into the given field.
     * Used by the Add and Edit screens so the user never types a date by hand.
     */
    public static void showDatePicker(Context context, EditText target) {

        Calendar calendar = Calendar.getInstance();

        Date existing = parse(target.getText().toString().trim());

        if (existing != null) {
            calendar.setTime(existing);
        }

        DatePickerDialog dialog = new DatePickerDialog(
                context,
                (view, year, month, day) -> {
                    Calendar picked = Calendar.getInstance();
                    picked.set(year, month, day);
                    target.setText(format(picked.getTime()));
                    target.setError(null);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private static String format(Date date) {
        return new SimpleDateFormat(PATTERN, Locale.US).format(date);
    }

    /** Strict parse: "31/02/2026" or "abc" return null. */
    private static Date parse(String text) {

        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat(PATTERN, Locale.US);
            format.setLenient(false);
            return format.parse(text.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static Date startOfDay(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}
