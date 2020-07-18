package com.duzhaokun123.bilibilihd.utils;

import java.util.HashMap;
import java.util.Map;

public class ObjectCache {
    private static Map<Long, Object> objectMap = new HashMap<>();

    public static long put(Object value) {
        long id = System.currentTimeMillis();
        objectMap.put(id, value);
        return id;
    }

    public static Object get(long id) {
        return objectMap.remove(id);
    }
}
