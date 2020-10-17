package com.duzhaokun123.bilibilihd.utils;

import java.util.Map;
import java.util.WeakHashMap;

public class ObjectCache {
    private static final Map<String, Object> objectMap = new WeakHashMap<>();

    public static String  put(Object value) {
        String key = "" + System.currentTimeMillis();
        if (value != null) {
            key = key + value.hashCode();
        }
        objectMap.put(key, value);
        return key;
    }

    public static Object get(String id) {
        return objectMap.remove(id);
    }
}
