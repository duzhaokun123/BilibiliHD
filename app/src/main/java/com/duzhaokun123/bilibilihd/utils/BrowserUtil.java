package com.duzhaokun123.bilibilihd.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;

import com.duzhaokun123.bilibilihd.R;

public class BrowserUtil {
    public static void openDefault(Context context, String url) {
        if (context == null) {
        Log.w("BrowserUtil", "openDefault: url is null");
        return;
    }
        // TODO: 20-4-30 实现其他打开
        openCustomTab(context, url);
    }

    public static void openCustomTab(Context context, String url) {

        new CustomTabsIntent.Builder().setToolbarColor(context.getColor(R.color.colorPrimary)).build().launchUrl(context, Uri.parse(url));
    }

    public static void openWebViewActivity(Context context, String url) {
        throw new RuntimeException("stub");
    }

    public static void openWebViewDialog(Context context, String url) {
        throw new RuntimeException("stub");
    }
}
