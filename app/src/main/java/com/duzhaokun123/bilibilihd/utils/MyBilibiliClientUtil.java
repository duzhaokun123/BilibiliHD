package com.duzhaokun123.bilibilihd.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class MyBilibiliClientUtil {
    public static long getAidFromBilibiliLink(String link) {
        int qm;
        long re = 0;
        Log.d("getAidFromBilibiliLink", "input " + link);
        try {
            if ((qm = link.indexOf('?')) != -1) {
                link = link.substring(17, qm);
                if (link.endsWith("/")) {
                    re = Long.parseLong(link.substring(0, link.length() - 1));
                } else {
                    re = Long.parseLong(link);
                }
            } else {
                re = Long.parseLong(link.substring(17));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("getAidFromBilibiliLink", "output " + re);
        return re;
    }

    /*
    av bv 转换算法 感谢 mcfx@zhihu.com
    如何看待 2020 年 3 月 23 日哔哩哔哩将稿件的「av 号」变更为「BV 号」？ - mcfx的回答 - 知乎
    https://www.zhihu.com/question/381784377/answer/1099438784
     */
    private static final char[] table = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF".toCharArray();
    private static Map<Character, Integer> tr = null;
    private static final int[] s = new int[]{11, 10, 3, 8, 4, 6};
    private static final long xor = 177451812L;
    private static final long add = 8728348608L;

    /**
     *
     * @return 错误时返回 0
     */
    public static long bv2av(String bv) {
        if (bv == null) {
            return 0;
        }

        if (tr == null) {
            tr = new HashMap<>();
            for (int i = 0; i < 58; i++) {
                tr.put(table[i], i);
            }
        }
        long re = 0;
        try {
            for (int i = 0; i < 6; i++) {
                re += tr.get(bv.charAt(s[i])) * Math.pow(58, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return (re - add) ^ xor;
    }

    public static String av2bv(long av) {
        av = (av ^ xor) + add;
        char[] re = "BV1  4 1 7  ".toCharArray();
        for (int i = 0; i < 6; i++) {
            re[s[i]] = table[(int) (av / (int) Math.pow(58, i) % 58)];
        }
        return String.valueOf(re);
    }

    public static String getCvUrl(long id) {
        return "https://www.bilibili.com/read/cv" + id;
    }

    public static String getB23Url(String bvid) {
        return "https://b23.tv/" + bvid;
    }

    public static String getB23Url(long aid) {
        return "https://b23.tv/" + av2bv(aid);
    }

    public static String getUserSpaceLink(long uid) {
        return "https://space.bilibili.com/" + uid;
    }
}
