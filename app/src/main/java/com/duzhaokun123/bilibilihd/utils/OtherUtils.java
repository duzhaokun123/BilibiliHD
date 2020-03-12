package com.duzhaokun123.bilibilihd.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.hiczp.bilibili.api.passport.model.LoginResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class OtherUtils {
    public static void setLevelDrawable(ImageView imageView, int level) {
        switch (level) {
            case 0:
                imageView.setImageResource(R.drawable.ic_user_level_0);
                break;
            case 1:
                imageView.setImageResource(R.drawable.ic_user_level_1);
                break;
            case 2:
                imageView.setImageResource(R.drawable.ic_user_level_2);
                break;
            case 3:
                imageView.setImageResource(R.drawable.ic_user_level_3);
                break;
            case 4:
                imageView.setImageResource(R.drawable.ic_user_level_4);
                break;
            case 5:
                imageView.setImageResource(R.drawable.ic_user_level_5);
                break;
            case 6:
                imageView.setImageResource(R.drawable.ic_user_level_6);
                break;
        }
    }

    public static void setSixDrawable(ImageView imageView, String sex) {
        if (sex.equals("男")) {
            imageView.setImageResource(R.drawable.ic_m);
        } else if (sex.equals("女")) {
            imageView.setImageResource(R.drawable.ic_f);
        } else {
            imageView.setImageDrawable(null);
        }
    }

    public static String MD5(String key) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = key.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
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
        ObjectInputStream objectInputStream = null;
        LoginResponse loginResponse = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            objectInputStream = new ObjectInputStream(inputStream);
            loginResponse = (LoginResponse) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
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
        ObjectOutputStream objectOutputStream = null;
        try {
            outputStream = context.getContentResolver().openOutputStream(uri);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(loginResponse);
            re = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
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
}
