package com.duzhaokun123.bilibilihd.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

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

    private static final int INT = 0;
    private static final int FLOAT = 1;
    private static final int STRING = 2;
    private static final int BOOLEAN = 3;
    private static final int STRING_SET = 4;

    private static final String TAG = "SettingsManager";

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
        if (settingsManager == null) {
            settingsManager = new SettingsManager();
            Log.w(TAG, "hadnot call SettingsManager.init()");
        }
        if (!settingsManager.inited) {
            Log.w(TAG, "SettingsManager is uninitialized");
        }
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

    private void save(String key, int type, Object value) {
        if (editor == null) {
            Log.e(TAG, "editor is null, settings cannot be saved");
        } else {
            switch (type) {
                case INT:
                    editor.putInt(key, Integer.parseInt(value.toString()));
                    break;
                case FLOAT:
                    editor.putFloat(key, Float.parseFloat(value.toString()));
                    break;
                case BOOLEAN:
                    editor.putBoolean(key, Boolean.parseBoolean(value.toString()));
                    break;
                case STRING:
                    editor.putString(key, (String) value);
                    break;
                case STRING_SET:
                    editor.putStringSet(key, (Set<String>) value);
                    break;
                default:
                    Log.wtf(TAG, type + " is not defined");
                    break;
            }
            editor.apply();
        }
    }

    public class Develop {
        private boolean test = true;

        public boolean isTest() {
            return test;
        }

        public void setTest(boolean test) {
            this.test = test;
            save("test", BOOLEAN, test);
        }
    }

    public class Layout {
        public int getColumn() {
            return column;
        }

        public void setColumn(int column) {
            this.column = column;
            save("column", INT, column);
        }

        public int getColumnLand() {
            return columnLand;
        }

        public void setColumnLand(int columnLand) {
            this.columnLand = columnLand;
            save("column_land", INT, columnLand);
        }

        private int column = 0;
        private int columnLand = 0;
    }

    public class Download {
        public static final int OKHTTP = 0;
        public static final int DOWNLOAD_MANAGER = 1;

        private int downloader = OKHTTP;

        public int getDownloader() {
            return downloader;
        }

        public void setDownloader(int downloader) {
            this.downloader = downloader;
            save("downloader", INT, downloader);
        }
    }
}
