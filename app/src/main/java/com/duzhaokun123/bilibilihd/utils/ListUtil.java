package com.duzhaokun123.bilibilihd.utils;

import java.util.Collection;
import java.util.List;

public class ListUtil {
    public static void addAll(List<Object> target, Collection<Object> source) {
        if (target == null || source == null) {
            return;
        }
        target.addAll(source);
    }
}
