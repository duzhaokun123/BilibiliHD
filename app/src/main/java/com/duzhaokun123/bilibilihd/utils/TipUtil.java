package com.duzhaokun123.bilibilihd.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.duzhaokun123.bilibilihd.Application;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

public class TipUtil {
    private TipUtil() {
    }

    private static final Map<Context, CoordinatorLayout> map = new HashMap<>();

    public static void registerCoordinatorLayout(Context context, CoordinatorLayout coordinatorLayout) {
        if (context != null && coordinatorLayout != null) {
            map.put(context, coordinatorLayout);
        }
    }

    public static void unregisterCoordinatorLayout(Context context) {
        map.remove(context);
    }

    public static void showToast(CharSequence msg) {
        Toast.makeText(Application.getInstance(), msg, Toast.LENGTH_LONG).show();
    }

    public static void showToast(@StringRes int resId) {
        Toast.makeText(Application.getInstance(), resId, Toast.LENGTH_LONG).show();
    }

    public static void showSnackbar(CoordinatorLayout coordinatorLayout, CharSequence msg) {
        Snackbar.make(coordinatorLayout, msg, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    public static void showSnackbar(CoordinatorLayout coordinatorLayout, @StringRes int resId) {
        showSnackbar(coordinatorLayout, Application.getInstance().getString(resId));
    }

    public static void showTip(Context context, CharSequence msg) {
        for (Context registeredContext : map.keySet()) {
            if (registeredContext == context) {
                if (context instanceof Activity && ((Activity) context).getWindow().getDecorView().getVisibility() == View.VISIBLE) {
                    CoordinatorLayout coordinatorLayout = map.get(registeredContext);
                    showSnackbar(coordinatorLayout, msg);
                } else {
                    showToast(msg);
                }
                return;
            }
        }
        showToast(msg);
    }

    public static void showTip(Context context, @StringRes int resId) {
        showTip(context, Application.getInstance().getString(resId));
    }
}
