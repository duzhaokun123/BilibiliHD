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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class OtherUtils {

    public static String MD5(String key) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = key.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

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
