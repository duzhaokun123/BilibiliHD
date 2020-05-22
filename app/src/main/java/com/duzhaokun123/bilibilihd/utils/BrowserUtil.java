package com.duzhaokun123.bilibilihd.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.ui.WebViewActivity;

public class BrowserUtil {
    public static void openCustomTab(@NonNull Context context, @NonNull String url) {
        try {
            new CustomTabsIntent.Builder().setToolbarColor(context.getColor(R.color.colorPrimary)).build().launchUrl(context, Uri.parse(url));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void openWebViewActivity(@NonNull Context context, @NonNull String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public static void openWebViewDialog(@NonNull Context context, @NonNull String url) {
        throw new RuntimeException("stub");
    }
}
