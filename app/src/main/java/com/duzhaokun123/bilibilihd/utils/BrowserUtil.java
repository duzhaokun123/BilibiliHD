package com.duzhaokun123.bilibilihd.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.CookieManager;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.ui.WebViewActivity;
import com.hiczp.bilibili.api.passport.model.LoginResponse;

public class BrowserUtil {
    public static void openCustomTab(@NonNull Context context, @NonNull String url) {
        Log.d("BrowserUtil", "openCustomTab: openUrl = " + url);
        try {
            new CustomTabsIntent.Builder().setToolbarColor(context.getColor(R.color.colorPrimary)).build().launchUrl(context, Uri.parse(url));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void openWebViewActivity(@NonNull Context context, @NonNull String url, boolean desktopUA) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.setData(Uri.parse(url));
        intent.putExtra("desktop_ua", desktopUA);
        context.startActivity(intent);
    }

    public static void openWebViewDialog(@NonNull Context context, @NonNull String url) {
        throw new RuntimeException("stub");
    }

    public static void syncLoggedLoginResponse() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        LoginResponse loginResponse = Settings.getLoginUserInfoMap().getLoggedLoginResponse();
        cookieManager.removeAllCookies(null);
        if (loginResponse == null) {
            return;
        }
        for (String url : loginResponse.getData().getCookieInfo().getDomains()) {
            for (LoginResponse.Data.CookieInfo.Cookie cookie : loginResponse.getData().getCookieInfo().getCookies()) {
                cookieManager.setCookie(url, cookie.getName() + "=" + cookie.getValue());
            }
            cookieManager.setCookie(url, "Domain=" + url);
            cookieManager.setCookie(url, "Path=/");
        }
        cookieManager.flush();
    }
}
