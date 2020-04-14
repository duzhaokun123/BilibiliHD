package com.duzhaokun123.bilibilihd.utils;

import android.icu.text.SimpleDateFormat;

public class SimpleDateFormatUtil {
    private static SimpleDateFormat format1;

    public static SimpleDateFormat getFormat1() {
        if (format1 == null) {
            format1 = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        }
        return format1;
    }
}
