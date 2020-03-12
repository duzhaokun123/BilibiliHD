package com.duzhaokun123.bilibilihd.mybilibiliapi;

public class Util {
    public static long getAidFromBilibiliLink(String link) {
        int qm;
        try {
            if ((qm = link.indexOf('?')) != -1) {
                return Long.parseLong(link.substring(17, qm));
            } else {
                return Long.parseLong(link.substring(17));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
