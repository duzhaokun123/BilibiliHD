package com.duzhaokun123.bilibilihd.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.hiczp.bilibili.api.passport.model.LoginResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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

    public static boolean loadLoginResponse(Context context, PBilibiliClient pBilibiliClient) {
        LoginResponse loginResponse = null;
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            fileInputStream = context.openFileInput("LoginResponse");
            objectInputStream = new ObjectInputStream(fileInputStream);
            loginResponse = (LoginResponse) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (loginResponse != null) {
            pBilibiliClient.getBilibiliClient().setLoginResponse(loginResponse);
            Log.d("LoginResponse", loginResponse.getData().getTokenInfo().getAccessToken());

            return true;
        }
        return false;
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
}
