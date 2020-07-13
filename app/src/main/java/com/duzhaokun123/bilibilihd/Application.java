package com.duzhaokun123.bilibilihd;

import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.Handler;
import com.duzhaokun123.bilibilihd.utils.NotificationUtil;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.hiczp.bilibili.api.passport.model.LoginResponse;

public class Application extends android.app.Application implements Handler.IHandlerMessageCallback {
    private static final int WHAT_RUN_ON_UI_THREAD = 0;

    private static Application application;
    private static Handler handler;

    @NonNull
    public static Application getInstance() {
        return application;
    }

    public static void runOnUiThread(Runnable callback) {
        Message message = Message.obtain(handler, callback);
        handler.sendMessage(message);
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
        LoginResponse loginResponse = Settings.getLoginUserInfoMap().getLoggedLoginResponse();
        PBilibiliClient.Companion.getInstance().setLoginResponse(loginResponse);
        if (Settings.isFirstStart()) {
            NotificationUtil.init(getApplicationContext());
            Settings.setFirstStart(false);
        }
        AppCompatDelegate.setDefaultNightMode(Settings.layout.getUiMode());
    }
}
