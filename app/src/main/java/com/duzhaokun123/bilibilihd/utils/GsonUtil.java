package com.duzhaokun123.bilibilihd.utils;

import com.google.gson.Gson;

public class GsonUtil {
    private static Gson gson;
    public static Gson getGsonInstance() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}
