package me.naburalis.batleroyalborder.util;

public class TimeUtils {
    /**
     * Converts a duration string to seconds.
     * Formats supported:
     *  - "2000" → 2000 milliseconds (2 seconds)
     *  - "20s" → 20 seconds
     *  - "5m" → 5 minutes (300 seconds)
     *  - "1h" → 1 hour (3600 seconds)
     */
    public static int parseTime(String input) {
        input = input.toLowerCase();
        char last = input.charAt(input.length() - 1);

        double multiplier;
        String numberPart;

        switch (last) {
            case 's':
                multiplier = 1000.0; // seconds to ms
                numberPart = input.substring(0, input.length() - 1);
                break;
            case 'm':
                multiplier = 60_000.0; // minutes to ms
                numberPart = input.substring(0, input.length() - 1);
                break;
            case 'h':
                multiplier = 3_600_000.0; // hours to ms
                numberPart = input.substring(0, input.length() - 1);
                break;
            default:
                multiplier = 1.0; // already ms
                numberPart = input;
        }

        // Support inputs like ".5" by prefixing 0
        if (numberPart.startsWith(".")) {
            numberPart = "0" + numberPart;
        }

        double value = Double.parseDouble(numberPart);
        return (int) Math.round(value * multiplier);
    }

    /**
     * Formats milliseconds into a string "h:m:ss.SSS" (hours:minutes:seconds.milliseconds).
     * Example: 3920200 -> "1:5:20.200".
     */
    public static String formatMillis(long millis) {
        long hours = millis / 3_600_000;
        millis %= 3_600_000;
        long minutes = millis / 60_000;
        millis %= 60_000;
        long seconds = millis / 1_000;
        long ms = millis % 1_000;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append(":");
        }

        if (minutes > 0) {
            sb.append(minutes).append(":");
            // when minutes are present, pad seconds to 2 digits
            sb.append(String.format("%02d", seconds));
        } else {
            // minutes are zero and omitted
            sb.append(seconds);
        }

        if (ms > 0) {
            sb.append(".").append(String.format("%03d", ms));
        }
        return sb.toString();
    }
} 