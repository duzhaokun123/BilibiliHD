package com.duzhaokun123.bilibilihd.utils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

public class GsonUtil {
    private static Gson gson;

    @NonNull
    public static Gson getGsonInstance() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}
