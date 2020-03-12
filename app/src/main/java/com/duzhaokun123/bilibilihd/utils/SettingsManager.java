package com.duzhaokun123.bilibilihd.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SettingsManager {
    private SettingsManager() {
        develop = new Develop();
        layout = new Layout();
        download = new Download();
    }

    private LoginUserInfoMap loginUserInfoMap;
    private static SettingsManager settingsManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean inited = false;

    public boolean isInited() {
        return inited;
    }

    public static void init(Context context) {
        settingsManager = new SettingsManager();

        settingsManager.sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        settingsManager.editor = settingsManager.sharedPreferences.edit();

        settingsManager.develop.test = settingsManager.sharedPreferences.getBoolean("test", true);
        settingsManager.layout.column = settingsManager.sharedPreferences.getInt("column", 0);
        settingsManager.layout.columnLand = settingsManager.sharedPreferences.getInt("column_land", 0);
        settingsManager.download.downloader = settingsManager.sharedPreferences.getInt("downloader", Download.OKHTTP);

        settingsManager.inited = true;
    }


    public static SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public final Develop develop;
    public final Layout layout;
    public final Download download;

    public LoginUserInfoMap getLoginUserInfoMap(Context context) {
        if (loginUserInfoMap == null) {
            File file = new File(context.getFilesDir(), "LoginUserInfoMap");
            if (file.exists()) {
                FileInputStream fileInputStream = null;
                ObjectInputStream objectInputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                    objectInputStream = new ObjectInputStream(fileInputStream);
                    loginUserInfoMap = (LoginUserInfoMap) objectInputStream.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    loginUserInfoMap = new LoginUserInfoMap();
                } finally {
                    try {
                        if (objectInputStream != null) {
                            objectInputStream.close();
                        }
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                loginUserInfoMap = new LoginUserInfoMap();
            }
        }
        return loginUserInfoMap;
    }

    public void saveLoginUserInfoMap(Context context) {
        if (loginUserInfoMap != null) {
            File file = new File(context.getFilesDir(), "LoginUserInfoMap");
            FileOutputStream fileOutputStream = null;
            ObjectOutputStream objectOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(loginUserInfoMap);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (objectOutputStream != null) {
                        objectOutputStream.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class Develop {
        private boolean test;

        public boolean isTest() {
            return test;
        }

        public void setTest(boolean test) {
            this.test = test;
            editor.putBoolean("test", test).apply();
        }
    }

    public class Layout {
        public int getColumn() {
            return column;
        }

        public void setColumn(int column) {
            this.column = column;
            editor.putInt("column", column).apply();
        }

        public int getColumnLand() {
            return columnLand;
        }

        public void setColumnLand(int columnLand) {
            this.columnLand = columnLand;
            editor.putInt("column_land", columnLand).apply();
        }

        private int column;
        private int columnLand;
    }

    public class Download {
        public static final int OKHTTP = 0;
        public static final int DOWNLOAD_MANAGER = 1;

        private int downloader;

        public int getDownloader() {
            return downloader;
        }

        public void setDownloader(int downloader) {
            this.downloader = downloader;
            editor.putInt("downloader", downloader).apply();
        }
    }
}
