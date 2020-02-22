package com.duzhaokun123.bilibilihd.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private ToastUtil() {}

    public static void sendMsg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void sendMsg(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }
}
