package com.duzhaokun123.bilibilihd.utils;

import java.util.Map;
import java.util.WeakHashMap;

public class ObjectCache {
    private static Map<Long, Object> objectMap = new WeakHashMap<>();

    public static long put(Object value) {
        long key = System.currentTimeMillis();
        objectMap.put(key, value);
        return key;
    }

    public static Object get(long id) {
        return objectMap.remove(id);
    }
}
