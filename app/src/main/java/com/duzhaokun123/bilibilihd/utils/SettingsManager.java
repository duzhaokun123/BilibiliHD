package com.duzhaokun123.bilibilihd.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {
    private SettingsManager() {
        develop = new Develop();
    }

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

        settingsManager.inited = true;
    }


    public static SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public final Develop develop;

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
}
