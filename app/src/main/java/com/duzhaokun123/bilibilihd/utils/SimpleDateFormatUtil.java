package com.duzhaokun123.bilibilihd.utils;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;

import java.util.Locale;

public class SimpleDateFormatUtil {
    private static SimpleDateFormat format1;

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat getFormat1() {
        if (format1 == null) {
            format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }
        return format1;
    }
}
