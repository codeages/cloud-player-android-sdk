package com.edusoho.playerdemo.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Strings {

    /**
     * Convert time to a string
     *
     * @param second e.g.time/length from file
     * @return formated string (hh:)mm:ss
     */
    public static String secondToString(long second) {
        return Strings.millisToString(second * 1000, false);
    }

    static String millisToString(long millis, boolean text) {
        boolean negative = millis < 0;
        millis = Math.abs(millis);

        millis /= 1000;
        int sec = (int) (millis % 60);
        millis /= 60;
        int min = (int) (millis % 60);
        millis /= 60;
        int hours = (int) millis;

        String        time;
        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        format.applyPattern("00");
        if (text) {
            if (millis > 0)
                time = (negative ? "-" : "") + hours + "h" + format.format(min) + "min";
            else if (min > 0)
                time = (negative ? "-" : "") + min + "min";
            else
                time = (negative ? "-" : "") + sec + "s";
        } else {
            if (millis > 0)
                time = (negative ? "-" : "") + hours + ":" + format.format(min) + ":" + format.format(sec);
            else
                time = (negative ? "-" : "") + min + ":" + format.format(sec);
        }
        return time;
    }
}
