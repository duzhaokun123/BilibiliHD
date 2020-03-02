package com.duzhaokun123.bilibilihd.utils;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.pBilibiliApi.api.PBilibiliClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DownloadUtil {
    public static void picturesDownload(Context context, String url) {
//        ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        switch (SettingsManager.getSettingsManager().download.getDownloader()) {
            case SettingsManager.Download.OKHTTP:
                OkHttpClient client = new OkHttpClient();
                Request okRequest = new Request.Builder()
                        .url(url)
                        .build();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileOutputStream fileOutputStream = null;
                        try {
                            Response response = client.newCall(okRequest).execute();
                            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "bilibili HD");
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }
                            File file = new File(dir, String.valueOf(System.currentTimeMillis()));
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(response.body().bytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }).start();
                break;
            case SettingsManager.Download.DOWNLOAD_MANAGER:
                DownloadManager.Request dmRequest = new DownloadManager.Request(Uri.parse(url))
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setTitle(context.getString(R.string.download))
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                        .setRequiresCharging(false)
                        .setRequiresDeviceIdle(false)
                        .addRequestHeader("User-Agent", PBilibiliClient.Companion.getPBilibiliClient().getBilibiliClient().getBillingClientProperties().getDefaultUserAgent())
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "bilibili HD" + File.separator + System.currentTimeMillis());
                try {
                    ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(dmRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }
}
