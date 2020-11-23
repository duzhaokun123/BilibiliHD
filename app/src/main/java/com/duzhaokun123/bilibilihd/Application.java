package com.duzhaokun123.bilibilihd;

import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.CustomBilibiliClientProperties;
import com.duzhaokun123.bilibilihd.utils.DanmakuUtil;
import com.duzhaokun123.bilibilihd.utils.Handler;
import com.duzhaokun123.bilibilihd.utils.NotificationUtil;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.hiczp.bilibili.api.passport.model.LoginResponse;

public class Application extends android.app.Application implements Handler.IHandlerMessageCallback {
    private static Application application;
    private static Handler handler;
    private static PBilibiliClient pBilibiliClient;

    @NonNull
    public static Application getInstance() {
        return application;
    }

    public static void runOnUiThread(Runnable callback) {
        Message message = Message.obtain(handler, callback);
        handler.sendMessage(message);
    }

    @NonNull
    public static PBilibiliClient getPBilibiliClient() {
        if (pBilibiliClient == null) {
            recreatePBilibiliClient();
        }
        return pBilibiliClient;
    }

    public static void recreatePBilibiliClient() {
        LoginResponse loginResponse = null;
        if (pBilibiliClient != null) {
            loginResponse = pBilibiliClient.getLoginResponse();
        }
        if (Settings.bilibiliApi.isCustom()) {
            pBilibiliClient = new PBilibiliClient(new CustomBilibiliClientProperties(),
                    Settings.bilibiliApi.getLogLevel());
        } else {
            pBilibiliClient = new PBilibiliClient();
        }
        pBilibiliClient.setLoginResponse(loginResponse);
    }

    public Application() {
        super();
        application = this;
        handler = new Handler(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Settings.init(this);
        NotificationUtil.INSTANCE.setContext(this);
        if (Settings.getLastVersionCode() < BuildConfig.VERSION_CODE) {
            // 做一些更新
            NotificationUtil.INSTANCE.init(this);
            Settings.setLastVersionCode(BuildConfig.VERSION_CODE);
        }
        LoginResponse loginResponse = Settings.getLoginUserInfoMap().getLoggedLoginResponse();
        if (Settings.isFirstStart()) {
            NotificationUtil.INSTANCE.init(this);
            Settings.setFirstStart(false);
        }
        getPBilibiliClient().setLoginResponse(loginResponse);
        AppCompatDelegate.setDefaultNightMode(Settings.layout.getUiMode());
        DanmakuUtil.INSTANCE.syncDanmakuSettings();
    }
}
