package com.duzhaokun123.bilibilihd.utils;

import android.os.Build;
import android.os.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
    public static void copy(InputStream in, OutputStream out) throws IOException {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            FileUtils.copy(in, out);
        } else {
            byte[] bytes = new byte[100000];
            int len;
            while ((len = in.read(bytes, 0, bytes.length)) != -1) {
                out.write(bytes, 0, len);
            }
        }
    }
    public static boolean deleteDir(File dir) {
        if (dir.isFile()) {
            return dir.delete();
        } else {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!deleteDir(file)) {
                        return false;
                    }
                }
            }
            return dir.delete();
        }
    }

}
