package com.duzhaokun123.bilibilihd.utils;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;

import com.duzhaokun123.bilibilihd.R;

public class CustomTabUtil {
    public static void openUrl(Context context, String url) {
        if (context == null) {
            return;
        }
        new CustomTabsIntent.Builder().setToolbarColor(context.getColor(R.color.colorPrimary)).build().launchUrl(context, Uri.parse(url));
    }
}
