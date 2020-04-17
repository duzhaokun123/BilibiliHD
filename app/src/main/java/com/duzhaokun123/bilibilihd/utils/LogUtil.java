package com.duzhaokun123.bilibilihd.utils;

import android.app.Activity;
import android.os.Build;
import android.os.Environment;

import com.duzhaokun123.bilibilihd.BuildConfig;
import com.duzhaokun123.bilibilihd.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class LogUtil {
    public static void saveLog(Activity activity) {
        new Thread() {
            @Override
            public void run() {
                InputStreamReader inputStreamReader = null;
                BufferedReader bufferedReader = null;
                FileOutputStream fileOutputStream = null;
                OutputStreamWriter outputStreamWriter = null;
                try {
                    fileOutputStream = new FileOutputStream(new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "" + System.currentTimeMillis() + ".log"));
                    outputStreamWriter = new OutputStreamWriter(fileOutputStream);

                    outputStreamWriter.write("========= beginning of info\n");
                    outputStreamWriter.write("versionName:\t" + BuildConfig.VERSION_NAME + "\n");
                    outputStreamWriter.write("versionCode:\t" + BuildConfig.VERSION_CODE + "\n");
                    outputStreamWriter.write("buildType:\t\t" + BuildConfig.BUILD_TYPE + "\n");
                    outputStreamWriter.write("gitVersion:\t\t" + BuildConfig.IS_GIT_VERSION + "\n");
                    outputStreamWriter.write("sdkVersion:\t\t" + Build.VERSION.SDK_INT + "\n");
                    outputStreamWriter.write("========= beginning of log\n");
                    Process exec = Runtime.getRuntime().exec("logcat");
                    inputStreamReader = new InputStreamReader(exec.getInputStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String lien;
                    while ((lien = bufferedReader.readLine()) != null) {
                        outputStreamWriter.write(lien + "\n");
                    }
                    exec.destroy();
                    activity.runOnUiThread(() -> ToastUtil.sendMsg(activity, R.string.saved));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (inputStreamReader != null) {
                            inputStreamReader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (outputStreamWriter != null) {
                            outputStreamWriter.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
