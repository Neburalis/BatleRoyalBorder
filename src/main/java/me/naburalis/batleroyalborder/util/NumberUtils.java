package me.naburalis.batleroyalborder.util;

import java.util.Locale;

public class NumberUtils {
    /**
     * Parses strings like "1k", "5m", "2g", "3t", "4p" into long values.
     * Without suffix returns the raw long value.
     * Power-of-ten multipliers:
     *  k = 10^3, m = 10^6, g = 10^9, t = 10^12, p = 10^15
     */
    public static long parseWithSuffix(String input) {
        input = input.trim().toLowerCase(Locale.ROOT);
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Empty numeric string");
        }

        char last = input.charAt(input.length() - 1);
        long multiplier;
        switch (last) {
            case 'k': multiplier = 1_000L; break;
            case 'm': multiplier = 1_000_000L; break;
            case 'b': multiplier = 1_000_000_000L; break;
            case 't': multiplier = 1_000_000_000_000L; break;
            case 'p': multiplier = 1_000_000_000_000_000L; break;
            default:
                // No suffix, parse entire string as number
                return Long.parseLong(input);
        }
        long value = Long.parseLong(input.substring(0, input.length() - 1));
        return value * multiplier;
    }

    /**
     * Formats a long value into short form with suffix (k,m,g,t,p).
     * Keeps only the most significant unit (drops lower orders).
     * For example 1_234_000 -> "1m".
     */
    public static String formatWithSuffix(long value) {
        if (value >= 1_000_000_000_000_000L) {
            return (value / 1_000_000_000_000_000L) + "p";
        } else if (value >= 1_000_000_000_000L) {
            return (value / 1_000_000_000_000L) + "t";
        } else if (value >= 1_000_000_000L) {
            return (value / 1_000_000_000L) + "b";
        } else if (value >= 1_000_000L) {
            return (value / 1_000_000L) + "m";
        } else if (value >= 1_000L) {
            return (value / 1_000L) + "k";
        } else {
            return Long.toString(value);
        }
    }

    private NumberUtils() {
        // utility class
    }
} 