package com.duzhaokun123.bilibilihd.utils;

import android.content.Context;
import android.net.Uri;

import com.duzhaokun123.bilibilihd.Application;
import com.duzhaokun123.bilibilihd.R;
import com.hiczp.bilibili.api.passport.model.LoginResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Objects;

public class OtherUtils {
    public static LoginResponse readLoginResponseFromUri(Context context, Uri uri) {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        LoginResponse loginResponse = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream));
            loginResponse = GsonUtil.getGsonInstance().fromJson(inputStreamReader, LoginResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return loginResponse;
    }

    public static boolean writeLoginResponseToUri(Context context, LoginResponse loginResponse, Uri uri) {
        boolean re = false;
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStream = context.getContentResolver().openOutputStream(uri);
            outputStreamWriter = new OutputStreamWriter(Objects.requireNonNull(outputStream));
            outputStreamWriter.write(GsonUtil.getGsonInstance().toJson(loginResponse, LoginResponse.class));
            re = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return re;
    }

    public static int dp2px(float dp) {
        final float scale = Application.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(float px) {
        final float scale = Application.getInstance().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static void doNothing() {
    }

    public static boolean isNightMode() {
        return Application.getInstance().getResources().getBoolean(R.bool.night_mode);
    }
}
