package com.duzhaokun123.bilibilihd.utils;

import android.content.Context;
import android.util.Log;

import com.duzhaokun123.bilibilihd.ui.widget.GeetestDialog;

public class GeetestUtil {
    public static void doTest(Context context, String url) {
        Log.d("GeetestUtil", url);
        GeetestDialog geetestDialog = new GeetestDialog(context, url);
        geetestDialog.show();

    }
}
