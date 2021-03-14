package com.duzhaokun123.bilibilihd.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

import okhttp3.logging.HttpLoggingInterceptor;

public class Settings {
    private static LoginUserInfoMap loginUserInfoMap;
    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        Settings.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        try {
            loginUserInfoMap = GsonUtil.getGsonInstance().fromJson(sharedPreferences.getString("login_user_info_map", ""), LoginUserInfoMap.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (loginUserInfoMap != null) {
            loginUserInfoMap.setLoggedUid(sharedPreferences.getLong("logged_uid", 0));
        }
    }

    public static final Layout layout = new Layout();
    public static final Play play = new Play();
    public static final Download download = new Download();
    public static final Danmaku danmaku = new Danmaku();
    public static final Ads ads = new Ads();
    public static final BilibiliApi bilibiliApi = new BilibiliApi();

    public static LoginUserInfoMap getLoginUserInfoMap() {
        if (loginUserInfoMap == null) {
            loginUserInfoMap = new LoginUserInfoMap();
        }
        return loginUserInfoMap;
    }

    public static void saveLoginUserInfoMap() {
        sharedPreferences.edit()
                .putString("login_user_info_map", GsonUtil.getGsonInstance().toJson(loginUserInfoMap, LoginUserInfoMap.class))
                .putLong("logged_uid", loginUserInfoMap.getLoggedUdi())
                .apply();
    }

    public static boolean isFirstStart() {
        return sharedPreferences.getBoolean("firstStart", false);
    }

    public static void setFirstStart(boolean firstStart) {
        sharedPreferences.edit().putBoolean("firstStart", firstStart).apply();
    }

    public static int getLastVersionCode() {
        return sharedPreferences.getInt("last_version_code", 0);
    }

    public static void setLastVersionCode(int versionCode) {
        sharedPreferences.edit().putInt("last_version_code", versionCode).apply();
    }

    public static class Layout {

        public int getColumn() {
            return getIntByString("column", 0);
        }

        public int getColumnLand() {
            return getIntByString("column_land", 0);
        }

        public int getUiMode() {
            String uiMod = sharedPreferences.getString("ui_mod", "0");
            if ("2".equals(uiMod)) {
                return AppCompatDelegate.MODE_NIGHT_YES;
            } else if ("1".equals(uiMod)) {
                return AppCompatDelegate.MODE_NIGHT_NO;
            } else {
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }
        }

        public boolean isUserSpaceUseWebView() {
            return sharedPreferences.getBoolean("user_space_use_web_view", false);
        }

        public boolean isLiveUseWebView() {
            return sharedPreferences.getBoolean("live_use_web_view", false);
        }

        public boolean isDisableWelcome() {
            return sharedPreferences.getBoolean("disable_welcome", false);
        }
    }

    public static class Play {
        public boolean isAutoRecordingHistory() {
            return sharedPreferences.getBoolean("auto_recording_history", true);
        }

        public boolean isPlayBackground() {
            return sharedPreferences.getBoolean("play_background", false);
        }

        public int getDefaultQualityType() {
            return getIntByString("quality", 0);
        }
    }

    public static class Download {
        public static final int DOWNLOAD_MANAGER = 1;
        public static final int GLIDE_CACHE_FIRST = 2;

        public int getDownloader() {
            String downloader = sharedPreferences.getString("downloader", "0");
            if ("1".equals(downloader)) {
                return DOWNLOAD_MANAGER;
            } else {
                return GLIDE_CACHE_FIRST;
            }
        }

        @Nullable
        public String getOfficialAppDownloadDir() {
            return sharedPreferences.getString("official_app_download_dir", null);
        }

        public void setOfficialAppDownloadDir(String dir) {
            sharedPreferences.edit().putString("official_app_download_dir", dir).apply();
        }
    }

    public static class Danmaku {
        public int getDanmakuStyle() {
            return getIntByString("danmaku_style", 1);
        }

//        public boolean isDuplicateMerging() {
//            return sharedPreferences.getBoolean("duplicate_merging", false);
//        }

        public boolean isDrawDebugInfo() {
            return sharedPreferences.getBoolean("danmaku_draw_debug_info", false);
        }

        public float getTextSize() {
            return getIntByString("text_size", 1);
        }

        public int getLineHeight() {
            return getIntByString("danmaku_line_height", 40);
        }

        public int getMarginTop() {
            return getIntByString("danmaku_margin_top", 0);
        }

        public int getMarginBottom() {
            return getIntByString("danmaku_margin_bottom", 0);
        }

        public float getShadowDx() {
            return getFloatByString("danmaku_shadow_dx", 0);
        }

        public float getShadowDy() {
            return getFloatByString("danmaku_shadow_dy", 0);
        }

        public float getShadowRadius() {
            return getFloatByString("danmaku_shadow_radius", 5F);
        }

        public float getDurationCoeff() {
            float f = getFloatByString("danmaku_duration_coeff", 1.0f);
            return f != 0 ? f : 1.0f;
        }

        public int getMaximumVisibleSizeInScreen() {
            return getIntByString("danmaku_maximum_visible_size_in_screen", -1);
        }

        public Set<String> getBlockByPlace() {
            return sharedPreferences.getStringSet("block_by_place", new HashSet<>());
        }

        public Boolean getAllowDanmakuOverlapping() {
            return sharedPreferences.getBoolean("prevent_danmaku_overlapping", true);
        }

        public int getDanmakuVisibility() {
            return getIntByString("danmaku_visibility", 0);
        }

        public int getTypefaceUse() {
            return getIntByString("danmaku_typeface", 0);
        }
    }

    public static class Ads {
        public boolean isShowWelcomeAd() {
            return sharedPreferences.getBoolean("welcome_ads", true);
        }

        public boolean isAllowAllAds() {
            return sharedPreferences.getBoolean("allow_all_ads", true);
        }

        public boolean shouldShowWelcomeAd() {
            return isShowWelcomeAd() && isAllowAllAds();
        }
    }

    public static class BilibiliApi {
        public boolean isCustom() {
            return sharedPreferences.getBoolean("bilibili_api_custom", false);
        }

        public void setCustom(boolean custom) {
            sharedPreferences.edit().putBoolean("bilibili_api_custom", custom).apply();
        }

        public HttpLoggingInterceptor.Level getLogLevel() {
            switch (getIntByString("bilibili_api_log_level", 0)) {
                case 1:
                    return HttpLoggingInterceptor.Level.BASIC;
                case 2:
                    return HttpLoggingInterceptor.Level.HEADERS;
                case 3:
                    return HttpLoggingInterceptor.Level.BODY;
                default:
                    return HttpLoggingInterceptor.Level.NONE;
            }
        }

        public String getDefaultUserAgent() {
            return sharedPreferences.getString("defaultUserAgent", null);
        }

        public String getAppKey() {
            return sharedPreferences.getString("appKey", null);
        }

        public String getAppSecret() {
            return sharedPreferences.getString("appSecret", null);
        }

        public String getPlatform() {
            return sharedPreferences.getString("platform", null);
        }

        public String getChannel() {
            return sharedPreferences.getString("channel", null);
        }

        public String getHardwareId() {
            return sharedPreferences.getString("hardwareId", null);
        }

        public String getVersion() {
            return sharedPreferences.getString("version", null);
        }

        public String getBuild() {
            return sharedPreferences.getString("build", null);
        }

        public String getBuildVersionId() {
            return sharedPreferences.getString("buildVersionId", null);
        }
    }

    public static boolean getNeverShowFW() {
        return sharedPreferences.getBoolean("never_show_fw", false);
    }

    public static void setNeverShowFW(boolean v) {
        sharedPreferences.edit().putBoolean("never_show_fw", v).apply();
    }

    public static int getIntByString(String key, int def) {
        String i = sharedPreferences.getString(key, null);
        try {
            if (i != null) {
                return Integer.parseInt(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return def;
    }

    public static float getFloatByString(String key, float def) {
        String f = sharedPreferences.getString(key, null);
        try {
            if (f != null) {
                return Float.parseFloat(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return def;
    }
}
