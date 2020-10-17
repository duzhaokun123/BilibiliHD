package com.duzhaokun123.bilibilihd.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.Application;
import com.duzhaokun123.bilibilihd.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class DownloadUtil {
    public static void downloadPicture(Context context, String url) {
        switch (Settings.download.getDownloader()) {
            case Settings.Download.DOWNLOAD_MANAGER:
                DownloadManager.Request dmRequest = new DownloadManager.Request(Uri.parse(url))
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setTitle(context.getString(R.string.download))
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                        .setRequiresCharging(false)
                        .setRequiresDeviceIdle(false)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "bilibili HD" + File.separator + System.currentTimeMillis());
                try {
                    ((DownloadManager) Objects.requireNonNull(context.getSystemService(Context.DOWNLOAD_SERVICE))).enqueue(dmRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                    TipUtil.showTip(context, e.getMessage());
                }
                break;
            case Settings.Download.GLIDE_CACHE_FIRST:
                new Thread(() -> {
                    FileInputStream fileInputStream = null;
                    FileOutputStream fileOutputStream = null;
                    try {
                        File srcFile = Glide.with(context).asFile().load(url).submit().get();
                        fileInputStream = new FileInputStream(Objects.requireNonNull(srcFile));
                        File dir;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "bilibili HD");
                        } else {
                            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "bilibili HD");
                        }
                        if (!dir.exists() && !dir.mkdirs()) {
                            return;
                        }
                        File file = new File(dir, String.valueOf(System.currentTimeMillis()));
                        fileOutputStream = new FileOutputStream(file);
                        IOUtil.copy(fileInputStream, fileOutputStream);
                        Application.runOnUiThread(() -> TipUtil.showToast(Application.getInstance().getString(R.string.saved_to_s, file.getPath())));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Application.runOnUiThread(() -> TipUtil.showToast(e.getMessage()));
                    } finally {
                        try {
                            if (fileInputStream != null) {
                                fileInputStream.close();
                            }
                            if (fileOutputStream != null) {
                                fileOutputStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
                break;
        }

    }
}
