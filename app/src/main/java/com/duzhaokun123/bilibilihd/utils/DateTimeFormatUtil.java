package com.duzhaokun123.bilibilihd.utils;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;

import com.google.android.exoplayer2.util.Util;

import java.util.Formatter;
import java.util.Locale;

public class DateTimeFormatUtil {
    private static DateFormat format1;
    private static StringBuilder formatBuilder;
    private static Formatter formatter;

    public static DateFormat getFormat1() {
        if (format1 == null) {
            format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }
        return format1;
    }

    public static String getStringForTime(long timeMs) {
        if (formatter == null) {
            formatBuilder = new StringBuilder();
            formatter = new Formatter(formatBuilder, Locale.getDefault());
        }
        return Util.getStringForTime(formatBuilder, formatter, timeMs);
    }

    public static long getTimeSForString(String time) {
        long timeS = 0;
        String[] strings = time.split(":");
        for (String string : strings) {
            timeS = timeS * 60 + Long.parseLong(string);
        }
        return timeS;
    }
}
